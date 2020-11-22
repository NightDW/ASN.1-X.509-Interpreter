package com.laidw.asn1.handler.impl;

import com.laidw.asn1.to.MyByteArr;

public class NormalStringSimpleHandler extends AbstractSimpleHandler {

	protected String handleInternal(MyByteArr content) {

		//直接构造一个字符串返回即可
		return new String(content.getBytes());
	}
}
