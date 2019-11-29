package com.laidw.asn1.handler.impl;

import com.laidw.asn1.to.MyByteArr;

public class BooleanSimpleHandler extends AbstractSimpleHandler{

	protected String handleInternal(MyByteArr content) {
		return content.get(0) == 0 ? "false" : "true";
	}
}
