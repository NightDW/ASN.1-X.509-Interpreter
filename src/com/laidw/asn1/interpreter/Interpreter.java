package com.laidw.asn1.interpreter;

import com.laidw.asn1.bean.Asn1Type;

//�����������Ǹ���ָ����Asn1Type�е��������������������˼
public interface Interpreter {
	
	//����һЩ��̬�����;�̬����
	public static String newLine = System.getProperty("line.separator");
	public static String getSpace(int level) {
		String space = "    ";
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < level; i++)
			sb.append(space);
		return sb.toString();
	}
	
	public void setContent(Asn1Type type);
	
	//level��ʾҪ��ǰ����Ӽ����Ʊ��
	public String getParseResult(int level);
}
