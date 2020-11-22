package com.laidw.asn1.bean;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.laidw.asn1.to.MyByteArr;
import com.laidw.asn1.tool.Asn1Helper;

//用于表示ASN.1的类型
public class Asn1Type {
	
	//类型名称
	private Asn1TypeName typeName;
	
	//表示该类型是否为简单类型
	private boolean isSimple;
	
	//简单类型的值，为了通用，使用String来存储
	private String value;
	
	//如果本类型是复杂类型，则用该属性存放其子属性
	private List<Asn1Type> subTypes;
	
	//构造函数，通过文件名称构造
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public Asn1Type(String fileLocation) throws IOException {
		File file = new File(fileLocation);
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		byte[] bytes = new byte[(int) file.length()];
		in.read(bytes);
		in.close();
		initialize(new MyByteArr(bytes, 0, bytes.length));
	}
	
	//构造函数，通过已有的MyByteArr构造
	public Asn1Type(MyByteArr arr) {
		initialize(arr);
	}
	
	//初始化，这个arr是包含了声明部分的，类似于"class Xxx{int x1; int x2;}"
	private void initialize(MyByteArr arr) {
		int tem = arr.get(0);
		
		//先判断是不是context-specific类型的，即判断第一个字节的前2个比特是否为10

		//如果是context-specific类型的
		if((tem >>> 6) == 2) {

		    //将第一个字节的第3个比特置为0，得到的结果就是对应类型的tagNum
		    typeName = Asn1TypeName.getAsn1TypeNameByTagNum(tem & 0xdf);

			//[1][2][6]是简单类型的，其它的应该是复杂类型或者不确定的
			isSimple = (typeName == Asn1TypeName.CTX_SPECIFIC_01 || typeName == Asn1TypeName.CTX_SPECIFIC_02 || typeName == Asn1TypeName.CTX_SPECIFIC_06);

		//如果是universal类型的
		} else {

		    //第一个字节的后5位表示tagNum
			typeName = Asn1TypeName.getAsn1TypeNameByTagNum(tem & 0x1f);
			
			//第一个字节的第3个比特表示该类型是否为复杂类型，为1表示是复杂类型
			isSimple = (tem & 0x20) != 0x20;
		}
		
		//content表示内容，即去掉了声明部分的语句
		MyByteArr content = Asn1Helper.getContent(arr);
		
		//如果是这几种类型的，则可能是简单或复杂类型的，因此重新判断isSimple
		if(typeName == Asn1TypeName.BIT_STRING || typeName == Asn1TypeName.OCTET_STRING || typeName == Asn1TypeName.IA5_STRING || typeName == Asn1TypeName.CTX_SPECIFIC_00) {
			
			//尝试对内容进行切分，切分成功则说明是复杂类型
			//特别地，BIT_STRING的content中第一个字节表示的是填充了多少个0比特，在切分时需要把它忽略
            List<MyByteArr> list = Asn1Helper.trySplit(typeName == Asn1TypeName.BIT_STRING ? content.getSubArr(1, content.getLength()) : content);
			isSimple = (list == null);
		}
		
		//如果是简单类型，则解析其内容并将其赋值给value
		if(isSimple) {
            value = Asn1Helper.parseSimpleContent(content, typeName);

        //如果是复杂类型，则将内容切分成若干个更小的声明语句
        //每个声明语句都用于创建Asn1Type对象，并把对象添加到subTypes列表中
        } else {
			subTypes = new ArrayList<>();

			//这里同样要注意BIT_STRING的content的特殊性
			for(MyByteArr mba : Asn1Helper.split(typeName == Asn1TypeName.BIT_STRING ? content.getSubArr(1, content.getLength()) : content))
				subTypes.add(new Asn1Type(mba));
		}
	}
	
	public String getStructTree() {
		return getStructTree(0);
	}
	
	public String getStructTree(int level) {
		StringBuilder sb = new StringBuilder();
		sb.append(Asn1Helper.getSpace(level)).append(typeName);
		if(isSimple){
            sb.append(": ").append(value).append(Asn1Helper.newLine);
        } else {
			sb.append(Asn1Helper.newLine);
			for(Asn1Type type: subTypes)
				sb.append(type.getStructTree(level + 1));
		}
		return sb.toString();
	}

	public boolean isSimple(){
	    return isSimple;
    }
	
	public Asn1TypeName getTypeName() {
		return typeName;
	}

	public String forceToGetValue() {
		return forceToGetValue(0);
	}
	
	public String forceToGetValue(int level) {

	    //如果是复杂类型的String类型，此时也可以返回其value
		if(!isSimple && (typeName == Asn1TypeName.OCTET_STRING || typeName == Asn1TypeName.BIT_STRING || typeName == Asn1TypeName.IA5_STRING))
			return forceToGetValueInternal(level);

		return Asn1Helper.getSpace(level) + value;
	}
	
	private String forceToGetValueInternal(int level) {
		
		if(isSimple)
		    return Asn1Helper.getSpace(level) + value;
		
		//注意有的奇葩的复杂类型的内容为空，如空的SEQUENCE，其数据为30 00，获取其子类型会抛出下标越界异常，因此要try-catch
		//如果捕获到异常，说明是空的复杂类型，此时返回空字符串即可
		try {
			StringBuilder sb = new StringBuilder();
			String newLine = Asn1Helper.newLine;
			for(Asn1Type tem : subTypes)
				sb.append(tem.forceToGetValueInternal(level)).append(newLine);
			if(sb.lastIndexOf(newLine) == sb.length() - newLine.length())
				sb.delete(sb.lastIndexOf(newLine), sb.length());
			return sb.toString();
		} catch(Exception e) {
			System.out.println("Force to get value failed! Will return empty string!");
		}
		return "";
	}
	
	public String getValue() {
		return value;
	}

	public List<Asn1Type> getSubTypes() {
		return subTypes;
	}
}
