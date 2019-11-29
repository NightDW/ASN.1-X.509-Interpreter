package com.laidw.asn1.handler.impl;

import com.laidw.asn1.to.MyByteArr;

public class NormalStringSimpleHandler extends AbstractSimpleHandler {

	protected String handleInternal(MyByteArr content) {
		return new String(content.getBytes());
	}
}
