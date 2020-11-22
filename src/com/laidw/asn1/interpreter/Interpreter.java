package com.laidw.asn1.interpreter;

import com.laidw.asn1.bean.Asn1Type;

//解释器，就是根据指定的Asn1Type中的内容来解释它代表的意思
public interface Interpreter {
	
	void setContent(Asn1Type type);
	
	//level表示要在前面添加几个制表符
	String getParseResult(int level);
}
