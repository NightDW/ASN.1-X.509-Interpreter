package com.laidw.asn1.interpreter.impl.x509;

import java.security.InvalidParameterException;
import java.util.List;

import com.laidw.asn1.bean.Asn1Type;
import com.laidw.asn1.bean.Asn1TypeName;
import com.laidw.asn1.interpreter.Interpreter;
import com.laidw.asn1.tool.Asn1Helper;

//AlgorithmIdentifier是ASN1中的SEQUENCE类型
public class AlgorithmIdentifier implements Interpreter {

	//ASN1类型为Oid
	private Asn1Type algorithm;
	
	//可选
	private Asn1Type parameters;
	
	public AlgorithmIdentifier(Asn1Type type) {
		setContent(type);
	}

	public void setContent(Asn1Type type) {
		List<Asn1Type> list = type.getSubTypes();
		if(list.size() > 2)
			throw new InvalidParameterException("Error! The Asn1Type you provided isn't suitable for AlgorithmIdentifier!");
		if(list.get(0).getTypeName() != Asn1TypeName.OBJECT_IDENTIFIER)
			throw new InvalidParameterException("Set algorithm failed! It isn't OID type!");
		algorithm = list.get(0);
		if(list.size() == 2)
			parameters = list.get(1);
	}

	public String getParseResult(int level) {
		StringBuilder sb = new StringBuilder();
		String space = Asn1Helper.getSpace(level);
		sb.append(space).append("algorithm: ").append(algorithm.forceToGetValue());
		if(parameters != null)
			sb.append(Asn1Helper.newLine).append(space).append("parameters: ").append(parameters.forceToGetValue());
		return sb.toString();
	}
}
