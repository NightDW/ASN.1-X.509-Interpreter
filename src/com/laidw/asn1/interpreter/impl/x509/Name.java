package com.laidw.asn1.interpreter.impl.x509;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import com.laidw.asn1.bean.Asn1Type;
import com.laidw.asn1.bean.Asn1TypeName;
import com.laidw.asn1.interpreter.Interpreter;
import com.laidw.asn1.tool.Asn1Helper;

/**
 * X.509中实际的定义：
 * Name::=CHOICE{RDNSequence}
 * RDNSequence::=SEQUENCE OF RelativeDistinguishedName
 * RelativeDistinguishedName::=SET OF AttributeTypeAndValue
 * AttributeTypeAndValue::=SEQUENCE{type AttributeType, value AttributeValue}
 */
public class Name implements Interpreter {
	
	//实际的格式为：一个SET包含一个SEQUENCE，该SEQUENCE又包含一个Oid和一个PrintableStr
	private List<Asn1Type> sets;
	
	public Name(Asn1Type type) {
		setContent(type);
	}

	public void setContent(Asn1Type type) {
		sets = new ArrayList<>();
		List<Asn1Type> list = type.getSubTypes();
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getTypeName() != Asn1TypeName.SET)
				throw new InvalidParameterException("Set set[" + i + "] failed! It isn't SET type!");
			sets.add(list.get(i));
		}
	}

	public String getParseResult(int level) {
		StringBuilder sb = new StringBuilder();
		String space = Asn1Helper.getSpace(level);
		String newLine = Asn1Helper.newLine;
		Asn1Type sequence;
        for (Asn1Type set : sets) {
            sequence = set.getSubTypes().get(0);
            sb.append(space)
                    .append(sequence.getSubTypes().get(0).forceToGetValue()).append(": ")
                    .append(sequence.getSubTypes().get(1).forceToGetValue()).append(newLine);
        }
		if(sb.lastIndexOf(newLine) == sb.length() - newLine.length())
			sb.delete(sb.lastIndexOf(newLine), sb.length());
		return sb.toString();
	}
}
