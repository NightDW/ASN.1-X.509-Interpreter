package com.laidw.asn1.interpreter;

import com.laidw.asn1.bean.Asn1Type;

//�����������Ǹ���ָ����Asn1Type�е��������������������˼
public interface Interpreter {
	
	void setContent(Asn1Type type);
	
	//level��ʾҪ��ǰ����Ӽ����Ʊ��
	String getParseResult(int level);
}
