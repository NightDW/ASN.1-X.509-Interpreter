package com.laidw.asn1.handler.impl;

import com.laidw.asn1.to.MyByteArr;
import com.laidw.asn1.tool.Asn1Helper;

public class BitStringSimpleHandler extends AbstractSimpleHandler {

	protected String handleInternal(MyByteArr content) {
		
		//��һ���ֽڱ�ʾ������ĩβ����˶��ٸ�0����
		int length = content.get(0);
		
		StringBuilder ret = new StringBuilder(Asn1Helper.toBinString(content, 1, content.getLength() - 1));
		
		//ȥ��ĩβ������0
		for(int i = 0; i < length; i++)
			ret.deleteCharAt(ret.length() - 1);
		
		return ret.toString();
	}
}