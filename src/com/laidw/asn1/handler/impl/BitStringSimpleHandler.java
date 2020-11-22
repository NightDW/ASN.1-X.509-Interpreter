package com.laidw.asn1.handler.impl;

import com.laidw.asn1.to.MyByteArr;
import com.laidw.asn1.tool.Asn1Helper;

public class BitStringSimpleHandler extends AbstractSimpleHandler {

	protected String handleInternal(MyByteArr content) {
		
		//第一个字节表示的是在末尾填充了多少个0比特
		int length = content.get(0);
		
		StringBuilder ret = new StringBuilder(Asn1Helper.toBinString(content, 1, content.getLength() - 1));
		
		//去掉末尾的填充的0
		for(int i = 0; i < length; i++)
			ret.deleteCharAt(ret.length() - 1);
		
		return ret.toString();
	}
}