package com.laidw.asn1.interpreter;

import com.laidw.asn1.bean.Asn1Type;

//解释器，就是根据指定的Asn1Type中的内容来解释它代表的意思
public interface Interpreter {
	
	//定义一些静态常量和静态方法
	public static String newLine = System.getProperty("line.separator");
	public static String getSpace(int level) {
		String space = "    ";
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < level; i++)
			sb.append(space);
		return sb.toString();
	}
	
	public void setContent(Asn1Type type);
	
	//level表示要在前面添加几个制表符
	public String getParseResult(int level);
}
