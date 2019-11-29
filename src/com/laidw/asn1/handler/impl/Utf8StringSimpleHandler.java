package com.laidw.asn1.handler.impl;

import java.io.UnsupportedEncodingException;

import com.laidw.asn1.to.MyByteArr;

public class Utf8StringSimpleHandler extends AbstractSimpleHandler {

	protected String handleInternal(MyByteArr content) {
		try {
			return new String(content.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported encoding! Will use default encoding to construct a new string!");
			return new String(content.getBytes());
		}
	}
}
