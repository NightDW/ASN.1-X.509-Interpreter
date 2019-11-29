package com.laidw.asn1.handler.impl;

import com.laidw.asn1.to.MyByteArr;
import com.laidw.asn1.tool.Asn1Helper;

public class HexStringSimpleHandler extends AbstractSimpleHandler {

	protected String handleInternal(MyByteArr content) {
		//�����ؽ�����ת��16�����ַ���
		return Asn1Helper.toHexString(content, 0, content.getLength());
	}
}
