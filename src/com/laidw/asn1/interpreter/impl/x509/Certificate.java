package com.laidw.asn1.interpreter.impl.x509;

import java.security.InvalidParameterException;
import java.util.List;

import com.laidw.asn1.bean.Asn1Type;
import com.laidw.asn1.bean.Asn1TypeName;
import com.laidw.asn1.interpreter.Interpreter;
import com.laidw.asn1.tool.Asn1Helper;

//Certificate是ASN1中的SEQUENCE类型
public class Certificate implements Interpreter {
	
	//可分为3个部分，其中signatureValue对应的ASN1类型为BIT_STRING
	private TbsCertificate tbsCertificate;
	private AlgorithmIdentifier signatureAlgorithm;
	private Asn1Type signatureValue;
	
	public Certificate(Asn1Type type) {
		setContent(type);
	}

	public void setContent(Asn1Type type) {
		List<Asn1Type> list = type.getSubTypes();
		tbsCertificate = new TbsCertificate(list.get(0));
		signatureAlgorithm = new AlgorithmIdentifier(list.get(1));
		if(list.get(2).getTypeName() != Asn1TypeName.BIT_STRING)
			throw new InvalidParameterException("Set signatureValue failed! It isn't BIT_STRING type!");
		signatureValue = list.get(2);
	}

	public String getParseResult(int level) {
		StringBuilder sb = new StringBuilder();
		String space = Asn1Helper.getSpace(level);
		String newLine = Asn1Helper.newLine;
		sb.append(space).append("tbsCertificate:").append(newLine).append(tbsCertificate.getParseResult(level + 1)).append(newLine);
		sb.append(space).append("signatureAlgorithm:").append(newLine).append(signatureAlgorithm.getParseResult(level + 1)).append(newLine);
		sb.append(space).append("signatureValue: ").append(signatureValue.forceToGetValue()).append(newLine);
		return sb.toString();
	}
}