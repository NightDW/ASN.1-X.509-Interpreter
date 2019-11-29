package com.laidw.asn1.interpreter.impl.x509;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import com.laidw.asn1.bean.Asn1Type;
import com.laidw.asn1.bean.Asn1TypeName;
import com.laidw.asn1.interpreter.Interpreter;

//TbsCertificate是ASN1中的SEQUENCE类型
public class TbsCertificate implements Interpreter{
	
	//对应的ASN1类型为CTX_00，它又包含了一个INTEGER类型
	private Asn1Type version;
	
	//X.509中定义的是CertificateSerialNumber类型，但直接用ASN.1的INTEGER类型表示即可
	//CertificateSerialNumber::=INTEGER
	private Asn1Type serialNumber;
	
	private AlgorithmIdentifier signature;
	private Name issuer;
	private Validity validity;
	private Name subject;
	private SubjectPublicKeyInfo subjectPublicKeyInfo;
	
	//接下来3个都是可选的，对应的ASN1类型分别为CTX_01, CTX_02, CTX_03
	//且前两者是UniqueIdentifier类型，UniqueIdentifier::=BIT STRING，直接用Asn1Type来表示即可
	private Asn1Type issuerUniqueID;
	private Asn1Type subjectUniqueID;
	private List<Extension> extensions;

	public TbsCertificate(Asn1Type type) {
		setContent(type);
	}

	public void setContent(Asn1Type type) {
		List<Asn1Type> list = type.getSubTypes();
		if(list.get(0).getTypeName() != Asn1TypeName.CTX_SPECIFIC_00)
			throw new InvalidParameterException("Set version failed! It isn't CTX_SPECIFIC_00 type!");
		version = list.get(0);
		if(list.get(1).getTypeName() != Asn1TypeName.INTEGER)
			throw new InvalidParameterException("Set serialNumber failed! It isn't INTEGER type!");
		serialNumber = list.get(1);
		signature = new AlgorithmIdentifier(list.get(2));
		issuer = new Name(list.get(3));
		validity = new Validity(list.get(4));
		subject = new Name(list.get(5));
		subjectPublicKeyInfo = new SubjectPublicKeyInfo(list.get(6));
		for(int i = 7; i < list.size(); i++) {
			switch(list.get(i).getTypeName()) {
				case CTX_SPECIFIC_01: issuerUniqueID = list.get(i); break;
				case CTX_SPECIFIC_02: subjectUniqueID = list.get(i); break;
				case CTX_SPECIFIC_03: {
					extensions = new ArrayList<>();
					//CTX_SPECIFIC_03包含了SEQUENCE，该SEQUENCE中的内容才表示extensions
					for(Asn1Type tem : list.get(i).getSubTypes().get(0).getSubTypes())
						extensions.add(new Extension(tem));
					break;
				}
				default: throw new InvalidParameterException("An unexpected type occurs in TbsCertificate!");
			}
		}
	}

	public String getParseResult(int level) {
		StringBuilder sb = new StringBuilder();
		String space = Interpreter.getSpace(level);
		sb.append(space).append("version(0-v1; 1-v2; 2-v3): ").append(version.getSubTypes().get(0).forceToGetValue()).append(newLine);
		sb.append(space).append("serialNumber: ").append(serialNumber.forceToGetValue()).append(newLine);
		sb.append(space).append("signature:").append(newLine).append(signature.getParseResult(level + 1)).append(newLine);
		sb.append(space).append("issuer:").append(newLine).append(issuer.getParseResult(level + 1)).append(newLine);
		sb.append(space).append("validity:").append(newLine).append(validity.getParseResult(level + 1)).append(newLine);
		sb.append(space).append("subject:").append(newLine).append(subject.getParseResult(level + 1)).append(newLine);
		sb.append(space).append("subjectPublicKeyInfo:").append(newLine).append(subjectPublicKeyInfo.getParseResult(level + 1)).append(newLine);
		if(issuerUniqueID != null)
			sb.append(space).append("issuerUniqueID: ").append(issuerUniqueID.forceToGetValue()).append(newLine);
		if(subjectUniqueID != null)
			sb.append(space).append("subjectUniqueID: ").append(subjectUniqueID.forceToGetValue()).append(newLine);
		if(extensions != null) {
			sb.append(space).append("extensions:").append(newLine);
			space = Interpreter.getSpace(level + 1);
			for(Extension ex : extensions)
				sb.append(space).append("extension:").append(newLine).append(ex.getParseResult(level + 2)).append(newLine);
		}
		if(sb.lastIndexOf(newLine) == sb.length() - newLine.length())
			sb.delete(sb.lastIndexOf(newLine), sb.length());
		return sb.toString();
	}
}
