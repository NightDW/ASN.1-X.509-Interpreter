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

//���ڱ�ʾASN.1������
public class Asn1Type {
	
	//��������
	private Asn1TypeName typeName;
	
	//��ʾ�������Ƿ�Ϊ������
	private boolean isSimple;
	
	//�����͵�ֵ��Ϊ��ͨ�ã�ʹ��String���洢
	private String value;
	
	//����������Ǹ������ͣ����ø����Դ����������
	private List<Asn1Type> subTypes;
	
	//���캯����ͨ���ļ����ƹ���
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public Asn1Type(String fileLocation) throws IOException {
		File file = new File(fileLocation);
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		byte[] bytes = new byte[(int) file.length()];
		in.read(bytes);
		in.close();
		initialize(new MyByteArr(bytes, 0, bytes.length));
	}
	
	//���캯����ͨ�����е�MyByteArr����
	public Asn1Type(MyByteArr arr) {
		initialize(arr);
	}
	
	//��ʼ�������arr�ǰ������������ֵģ�������"class Xxx{int x1; int x2;}"
	private void initialize(MyByteArr arr) {
		int tem = arr.get(0);
		
		//���ж��ǲ���context-specific���͵ģ����жϵ�һ���ֽڵ�ǰ2�������Ƿ�Ϊ10

		//�����context-specific���͵�
		if((tem >>> 6) == 2) {

		    //����һ���ֽڵĵ�3��������Ϊ0���õ��Ľ�����Ƕ�Ӧ���͵�tagNum
		    typeName = Asn1TypeName.getAsn1TypeNameByTagNum(tem & 0xdf);

			//[1][2][6]�Ǽ����͵ģ�������Ӧ���Ǹ������ͻ��߲�ȷ����
			isSimple = (typeName == Asn1TypeName.CTX_SPECIFIC_01 || typeName == Asn1TypeName.CTX_SPECIFIC_02 || typeName == Asn1TypeName.CTX_SPECIFIC_06);

		//�����universal���͵�
		} else {

		    //��һ���ֽڵĺ�5λ��ʾtagNum
			typeName = Asn1TypeName.getAsn1TypeNameByTagNum(tem & 0x1f);
			
			//��һ���ֽڵĵ�3�����ر�ʾ�������Ƿ�Ϊ�������ͣ�Ϊ1��ʾ�Ǹ�������
			isSimple = (tem & 0x20) != 0x20;
		}
		
		//content��ʾ���ݣ���ȥ�����������ֵ����
		MyByteArr content = Asn1Helper.getContent(arr);
		
		//������⼸�����͵ģ�������Ǽ򵥻������͵ģ���������ж�isSimple
		if(typeName == Asn1TypeName.BIT_STRING || typeName == Asn1TypeName.OCTET_STRING || typeName == Asn1TypeName.IA5_STRING || typeName == Asn1TypeName.CTX_SPECIFIC_00) {
			
			//���Զ����ݽ����з֣��зֳɹ���˵���Ǹ�������
			//�ر�أ�BIT_STRING��content�е�һ���ֽڱ�ʾ��������˶��ٸ�0���أ����з�ʱ��Ҫ��������
            List<MyByteArr> list = Asn1Helper.trySplit(typeName == Asn1TypeName.BIT_STRING ? content.getSubArr(1, content.getLength()) : content);
			isSimple = (list == null);
		}
		
		//����Ǽ����ͣ�����������ݲ����丳ֵ��value
		if(isSimple) {
            value = Asn1Helper.parseSimpleContent(content, typeName);

        //����Ǹ������ͣ��������зֳ����ɸ���С���������
        //ÿ��������䶼���ڴ���Asn1Type���󣬲��Ѷ�����ӵ�subTypes�б���
        } else {
			subTypes = new ArrayList<>();

			//����ͬ��Ҫע��BIT_STRING��content��������
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

	    //����Ǹ������͵�String���ͣ���ʱҲ���Է�����value
		if(!isSimple && (typeName == Asn1TypeName.OCTET_STRING || typeName == Asn1TypeName.BIT_STRING || typeName == Asn1TypeName.IA5_STRING))
			return forceToGetValueInternal(level);

		return Asn1Helper.getSpace(level) + value;
	}
	
	private String forceToGetValueInternal(int level) {
		
		if(isSimple)
		    return Asn1Helper.getSpace(level) + value;
		
		//ע���е�����ĸ������͵�����Ϊ�գ���յ�SEQUENCE��������Ϊ30 00����ȡ�������ͻ��׳��±�Խ���쳣�����Ҫtry-catch
		//��������쳣��˵���ǿյĸ������ͣ���ʱ���ؿ��ַ�������
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
