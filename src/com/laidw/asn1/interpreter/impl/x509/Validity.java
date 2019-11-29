package com.laidw.asn1.interpreter.impl.x509;

import java.security.InvalidParameterException;
import java.util.List;

import com.laidw.asn1.bean.Asn1Type;
import com.laidw.asn1.bean.Asn1TypeName;
import com.laidw.asn1.interpreter.Interpreter;

//Validity本身是SEQUENCE类型
public class Validity implements Interpreter {

	//两个都是Time类型
	private Asn1Type from;
	private Asn1Type to;
	
	public Validity(Asn1Type type) {
		setContent(type);
	}

	public void setContent(Asn1Type type) {
		List<Asn1Type> list = type.getSubTypes();
		if(list.get(0).getTypeName() != Asn1TypeName.GENERAL_TIME && list.get(0).getTypeName() != Asn1TypeName.UTC_TIME)
			throw new InvalidParameterException("Set from time failed! It isn't TIME type!");
		from = list.get(0);
		if(list.get(1).getTypeName() != Asn1TypeName.GENERAL_TIME && list.get(1).getTypeName() != Asn1TypeName.UTC_TIME)
			throw new InvalidParameterException("Set to time failed! It isn't TIME type!");
		to = list.get(1);
	}

	public String getParseResult(int level) {
		StringBuilder sb = new StringBuilder();
		String space = Interpreter.getSpace(level);
		sb.append(space).append("from: ").append(from.forceToGetValue()).append(newLine);
		sb.append(space).append("to: ").append(to.forceToGetValue());
		return sb.toString();
	}
}
