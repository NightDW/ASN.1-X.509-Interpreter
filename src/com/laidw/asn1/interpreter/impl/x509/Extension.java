package com.laidw.asn1.interpreter.impl.x509;

import java.security.InvalidParameterException;
import java.util.List;

import com.laidw.asn1.bean.Asn1Type;
import com.laidw.asn1.bean.Asn1TypeName;
import com.laidw.asn1.interpreter.Interpreter;

//Extension本身是SEQUENCE类型
public class Extension implements Interpreter {

	//Oid类型
	private Asn1Type extnID;
	
	//BOOLEAN类型，可选
	private Asn1Type critical;
	
	//OCTETSTR类型
	private Asn1Type extnValue;
	
	public Extension(Asn1Type type) {
		setContent(type);
	}

	public void setContent(Asn1Type type) {
		List<Asn1Type> list = type.getSubTypes();
		if(list.get(0).getTypeName() != Asn1TypeName.OBJECT_IDENTIFIER)
			throw new InvalidParameterException("Set extnID failed! It isn't OID type!");
		extnID = list.get(0);
		for(int i = 1; i < list.size(); i++) {
			switch(list.get(i).getTypeName()) {
				case BOOLEAN: critical = list.get(i); break;
				case OCTET_STRING: extnValue = list.get(i); break;
				default: throw new InvalidParameterException("An unexpected type occurs in Extension!");
			}
		}
	}

	public String getParseResult(int level) {
		StringBuilder sb = new StringBuilder();
		String space = Interpreter.getSpace(level);
		sb.append(space).append("extnID: ").append(extnID.forceToGetValue()).append(newLine);
		if(critical != null)
			sb.append(space).append("critical: ").append(critical.forceToGetValue()).append(newLine);
		sb.append(space).append("extnValue:").append(newLine).append(extnValue.forceToGetValue(level + 1));
		return sb.toString();
	}
}
