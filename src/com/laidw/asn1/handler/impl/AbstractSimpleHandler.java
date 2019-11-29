package com.laidw.asn1.handler.impl;

import com.laidw.asn1.handler.TypeSimpleHandler;
import com.laidw.asn1.to.MyByteArr;

public abstract class AbstractSimpleHandler implements TypeSimpleHandler {

	public final String handle(MyByteArr content) {
		if(content.getLength() == 0)
			return "";
		return handleInternal(content);
	}

	protected abstract String handleInternal(MyByteArr content);
}
