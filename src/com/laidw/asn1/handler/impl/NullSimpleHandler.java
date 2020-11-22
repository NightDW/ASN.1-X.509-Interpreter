package com.laidw.asn1.handler.impl;

import com.laidw.asn1.to.MyByteArr;

public class NullSimpleHandler extends AbstractSimpleHandler {

	protected String handleInternal(MyByteArr content) {

		//Ö±½Ó·µ»Ø"null"×Ö·û´®
		return "null";
	}
}
