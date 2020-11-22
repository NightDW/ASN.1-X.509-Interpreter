package com.laidw.asn1.handler.impl;

import java.math.BigInteger;

import com.laidw.asn1.to.MyByteArr;
import com.laidw.asn1.tool.Asn1Helper;

public class IntegerSimpleHandler extends AbstractSimpleHandler {

	protected String handleInternal(MyByteArr content) {

		//注意，ASN.1中的INTEGER可能有很多位，甚至连Java的long都装不下
		//因此使用BigInteger来表示INTEGER，顺便把十六进制字符串返回
		return new BigInteger(content.getBytes()).toString() + " (0x" + Asn1Helper.toHexString(content, 0, content.getLength()) + ")";
	}
}
