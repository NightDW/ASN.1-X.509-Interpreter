package com.laidw.asn1.handler.impl;

import com.laidw.asn1.to.MyByteArr;

public class BooleanSimpleHandler extends AbstractSimpleHandler{

	protected String handleInternal(MyByteArr content) {

	    //该字节为0说明表示false
		return "" + (content.get(0) != 0);
	}
}
