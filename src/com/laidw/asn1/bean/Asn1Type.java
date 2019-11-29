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
	
	//����һЩ��̬�����;�̬����
	public static String newLine = System.getProperty("line.separator");
	public static String getSpace(int level) {
		String space = "    ";
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < level; i++)
			sb.append(space);
		return sb.toString();
	}
	
	//��������
	private Asn1TypeName typeName;
	
	//��ʾ�������Ƿ�Ϊ������
	private boolean isSimple;
	
	//�����͵�ֵ��Ϊ��ͨ�ã�ʹ��String���洢
	private String value;
	
	//����������Ǹ������ͣ����ø����Դ����������
	private List<Asn1Type> subTypes;
	
	//���캯����ͨ���ļ����ƹ���
	public Asn1Type(String fileLocation) throws IOException {
		File file = new File(fileLocation);
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		byte[] tem = new byte[(int) file.length()];
		in.read(tem); in.close();
		initialize(new MyByteArr(tem, 0, tem.length));
	}
	
	//���캯����ͨ�����е�MyByteArr����
	public Asn1Type(MyByteArr arr) {
		initialize(arr);
	}
	
	//��ʼ�������arr�ǰ������������ֵģ�������"class Xxx{int x1; int x2;}"
	private void initialize(MyByteArr arr) {
		int tem = arr.get(0);
		
		//���ж��ǲ���context-specific���͵ģ����жϵ�һ���ֽڵ�ǰ2�������Ƿ�Ϊ10
		if((tem >>> 6) == 2) {
			typeName = Asn1TypeName.getAsn1TypeNameByTagNum(tem & 0xdf);
			
			//[1][2][6]�Ǽ����͵ģ�������Ӧ���Ǹ������ͻ��߲�ȷ����
			isSimple = (typeName == Asn1TypeName.CTX_SPECIFIC_01 || typeName == Asn1TypeName.CTX_SPECIFIC_02 || typeName == Asn1TypeName.CTX_SPECIFIC_06);
		}
		
		//�����universal���͵�
		else {
			//��һ���ֽڵĺ�5λ��ʾtagNum
			typeName = Asn1TypeName.getAsn1TypeNameByTagNum(tem & 0x1f);
			
			//��һ���ֽڵĵ�3�����ر�ʾ�������Ƿ�Ϊ�������ͣ�Ϊ1��ʾ�Ǹ�������
			isSimple = (tem & 0x20) != 0x20;
		}
		
		//content��ʾ���ݣ���ȥ�����������ֵ����
		MyByteArr content = Asn1Helper.getContent(arr);
		
		List<MyByteArr> list = null;
		
		//������⼸�����͵ģ�������Ǽ򵥻������͵ģ������ж�isSimple
		if(typeName == Asn1TypeName.BIT_STRING || typeName == Asn1TypeName.OCTET_STRING || typeName == Asn1TypeName.IA5_STRING || typeName == Asn1TypeName.CTX_SPECIFIC_00) {
			
			//���Զ����ݽ����з֣��зֳɹ���˵���Ǹ�������
			//�ر�أ�BIT_STRING��content�е�һ���ֽڱ�ʾ��������˶��ٸ�0���أ����з�ʱ��Ҫ��������
			list = Asn1Helper.trySplit(typeName == Asn1TypeName.BIT_STRING ? content.getSubArr(1, content.getLength()) : content);
			isSimple = list == null;
		}
		
		//����Ǽ����ͣ�����������ݲ����丳ֵ��value
		if(isSimple)
			value = Asn1Helper.parseSimpleContent(content, typeName);
		
		//����Ǹ������ͣ��������зֳ����ɸ���С��������䣬�����Ѿ��洢����list��
		//ÿ��������䶼���ڴ���Asn1Type���󣬲��Ѷ�����ӵ�subTypes�б���
		else {
			subTypes = new ArrayList<>();
			//ͬ��Ҫע��BIT_STRING��content��������
			for(MyByteArr mba : Asn1Helper.split(typeName == Asn1TypeName.BIT_STRING ? content.getSubArr(1, content.getLength()) : content))
				subTypes.add(new Asn1Type(mba));
		}
	}
	
	public String getStructTree() {
		return getStructTree(0);
	}
	
	public String getStructTree(int level) {
		StringBuilder sb = new StringBuilder();
		sb.append(getSpace(level)).append(typeName);
		if(isSimple)
			sb.append(": " + value + newLine);
		else {
			sb.append(newLine);
			for(Asn1Type type: subTypes)
				sb.append(type.getStructTree(level + 1));
		}
		return sb.toString();
	}
	
	public Asn1TypeName getTypeName() {
		return typeName;
	}

	public boolean isSimple() {
		return isSimple;
	}
	
	public String forceToGetValue() {
		return forceToGetValue(0);
	}
	
	public String forceToGetValue(int level) {
		//����Ǹ������͵�String���ͣ���ʱҲ���Է�����value
		if(!isSimple && (typeName == Asn1TypeName.OCTET_STRING || typeName == Asn1TypeName.BIT_STRING || typeName == Asn1TypeName.IA5_STRING))
			return forceToGetValueInternal(level);
		return getSpace(level) + value;
	}
	
	private String forceToGetValueInternal(int level) {
		
		if(isSimple) return getSpace(level) + value;
		
		//ע���е�����ĸ������͵�����Ϊ�գ���յ�SEQUENCE��������Ϊ30 00����ȡ�������ͻ��׳��±�Խ���쳣�����Ҫtry-catch
		//��������쳣��˵���ǿյĸ������ͣ���ʱ���ؿ��ַ�������
		try {
			StringBuilder sb = new StringBuilder();
			for(Asn1Type tem : getSubTypes())
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
