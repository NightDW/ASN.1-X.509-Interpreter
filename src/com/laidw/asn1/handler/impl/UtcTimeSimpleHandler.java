package com.laidw.asn1.handler.impl;

import com.laidw.asn1.to.MyByteArr;

public class UtcTimeSimpleHandler extends AbstractSimpleHandler {

	protected String handleInternal(MyByteArr content) {
		String time = new String(content.getBytes()), ret = "";
		ret += time.substring(0, 2) + "-" + time.substring(2, 4) + "-" + time.substring(4, 6) + " ";
		ret += time.substring(6, 8) + ":" + time.substring(8, 10) + ":" + time.substring(10, 12) + " ";
		return ret + time.substring(12);
	}
}
