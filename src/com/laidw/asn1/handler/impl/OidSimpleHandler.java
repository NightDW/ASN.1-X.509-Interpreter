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

	//Oid�ĸ�ʽΪv1.v2.v3.v4.v5..vn
	//content�ĵ�һ���ֽڱ�ʾv1 * 40 + v2������v1��0-2��ȡֵ��v2��0-39��ȡֵ
	//v3��֮���������������ͬ���������ұ�����ֱ���ҵ�һ��С��0x80����
	//�ӿ�ʼ�������ֽڵ����С��0x80���ֽڱ�ʾ�ľ���һ��vn����128���Ƽ������ʵֵ������0x80���ֽ�Ҫ����0x80��
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
		//ȥ��ĩβ�ĵ㣬��������صĺ���
		sb.deleteCharAt(sb.length() - 1);
		String value = mapping.get(sb.toString());
		sb.append(" (").append(value == null ? "unknown" : value).append(")");
		return sb.toString();
	}
}
