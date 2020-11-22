package com.laidw.asn1.handler.impl;

import java.nio.charset.StandardCharsets;

import com.laidw.asn1.to.MyByteArr;

public class Utf8StringSimpleHandler extends AbstractSimpleHandler {

	protected String handleInternal(MyByteArr content) {

	    //ÒÔUTF8±àÂë´´½¨×Ö·û´®
		return new String(content.getBytes(), StandardCharsets.UTF_8);
	}

}
