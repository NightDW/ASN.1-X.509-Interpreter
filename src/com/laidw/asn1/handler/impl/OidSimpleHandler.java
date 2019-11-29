package com.laidw.asn1.handler.impl;

import java.util.HashMap;
import java.util.Map;

import com.laidw.asn1.to.MyByteArr;

public class OidSimpleHandler extends AbstractSimpleHandler {
	
	public static Map<String, String> mapping;
	
	static {
		mapping = new HashMap<>();
		mapping.put("1.2.840.113549.1.1.1", "rsaEncryption");
		mapping.put("1.2.840.113549.1.1.11", "sha256WithRSAEncryption");
		mapping.put("1.3.6.1.4.1.4146.1.20", "globalsignOVPolicy");
		mapping.put("1.3.6.1.5.5.7.1.1", "authorityInfoAccess");
		mapping.put("1.3.6.1.5.5.7.2.1", "cps");
		mapping.put("1.3.6.1.5.5.7.3.1", "serverAuth");
		mapping.put("1.3.6.1.5.5.7.3.2", "clientAuth");
		mapping.put("1.3.6.1.5.5.7.48.1", "ocsp");
		mapping.put("1.3.6.1.5.5.7.48.2", "caIssuers");
		mapping.put("2.5.4.3", "commonName");
		mapping.put("2.5.4.6", "countryName");
		mapping.put("2.5.4.7", "localityName");
		mapping.put("2.5.4.8", "stateOrProvinceName");
		mapping.put("2.5.4.10", "organizationName");
		mapping.put("2.5.4.11", "organizationalUnitName");
		mapping.put("2.5.29.14", "subjectKeyIdentifier");
		mapping.put("2.5.29.15", "keyUsage");
		mapping.put("2.5.29.17", "subjectAltName");
		mapping.put("2.5.29.19", "basicConstraints");
		mapping.put("2.5.29.31", "cRLDistributionPoints");
		mapping.put("2.5.29.32", "certificatePolicies");
		mapping.put("2.5.29.35", "authorityKeyIdentifier");
		mapping.put("2.5.29.37", "extKeyUsage");
	}

	//Oid的格式为v1.v2.v3.v4.v5..vn
	//content的第一个字节表示v1 * 40 + v2，其中v1在0-2间取值，v2在0-39间取值
	//v3及之后的数解析方法相同：从左往右遍历，直到找到一个小于0x80的数
	//从开始遍历的字节到这个小于0x80的字节表示的就是一个vn，用128进制计算出真实值（大于0x80的字节要减掉0x80）
	protected String handleInternal(MyByteArr content) {
		StringBuilder sb = new StringBuilder();
		int tem = content.get(0);
		sb.append(tem / 40).append(".").append(tem % 40).append(".");
		int i = 1; tem = 0;
		while(i < content.getLength()) {
			while(content.get(i) >= 0x80) {
				tem *= 128;
				tem += content.get(i) - 0x80;
				i++;
			}
			tem *= 128;
			tem += content.get(i);
			sb.append(tem).append(".");
			i++; tem = 0;
		}
		//去掉末尾的点，并查找相关的含义
		sb.deleteCharAt(sb.length() - 1);
		String value = mapping.get(sb.toString());
		sb.append(" (").append(value == null ? "unknown" : value).append(")");
		return sb.toString();
	}
}
