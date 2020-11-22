package com.laidw.asn1.interpreter.impl.x509;

import java.security.InvalidParameterException;
import java.util.List;

import com.laidw.asn1.bean.Asn1Type;
import com.laidw.asn1.bean.Asn1TypeName;
import com.laidw.asn1.interpreter.Interpreter;
import com.laidw.asn1.tool.Asn1Helper;

//SubjectPublicKeyInfo本身是SEQUENCE类型
public class SubjectPublicKeyInfo implements Interpreter {
	
	private AlgorithmIdentifier algorithm;
	
	//ASN1类型为BITSTRING
	private Asn1Type subjectPublicKey;

	public SubjectPublicKeyInfo(Asn1Type type) {
		setContent(type);
	}

	public void setContent(Asn1Type type) {
		List<Asn1Type> list = type.getSubTypes();
		algorithm = new AlgorithmIdentifier(list.get(0));
		if(list.get(1).getTypeName() != Asn1TypeName.BIT_STRING)
			throw new InvalidParameterException("Set subjectPublicKey failed! It isn't BITSTRING type!");
		subjectPublicKey = list.get(1);
	}

	public String getParseResult(int level) {
		StringBuilder sb = new StringBuilder();
		String space = Asn1Helper.getSpace(level);
		String newLine = Asn1Helper.newLine;
		sb.append(space).append("algorithm:").append(newLine).append(algorithm.getParseResult(level + 1)).append(newLine);
		sb.append(space).append("subjectPublicKey:").append(newLine).append(subjectPublicKey.forceToGetValue(level + 1));
		return sb.toString();
	}
}
