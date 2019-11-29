package com.laidw.asn1.bean;

import java.security.InvalidParameterException;

//����ASN.1�е�һЩ��������
//ע�⣬����ע����û��˵���Ǹ������͵Ļ�����������͵���ʵ�����п����Ǹ������͵�
//����BIT_STRING��OCTET_STRING���Ͷ����ܻ�Ƕ���������ͣ���ʱ����Ϊencapsulates��
//��ˣ�������ʲô���ͣ�������ô��������͵ķ�ʽ����һ�飬����ʧ�ܲ��ô�������͵ķ�ʽ������
public enum Asn1TypeName {
	
	//��������context-specific������ͣ��м�����Ҳ�и������͵ģ��������Ͷ���universal���
	CTX_SPECIFIC_00(128), CTX_SPECIFIC_01(129), CTX_SPECIFIC_02(130), CTX_SPECIFIC_03(131), 
	CTX_SPECIFIC_04(132), CTX_SPECIFIC_05(133), CTX_SPECIFIC_06(134), CTX_SPECIFIC_07(135),
	
	//Boolean
	BOOLEAN(1),
	
	//��ͨ������
	INTEGER(2),
	
	//�ַ���"00000001"��BIT_STRING�ķ�ʽ��ʾ����һ���ֽ�0x01
	BIT_STRING(3),
	
	//�ַ���"ab"��OCTET_STRING�ķ�ʽ��ʾ����һ���ֽ�0xab
	OCTET_STRING(4),
	
	//��ֵ
	NULL(5),
	
	//��һϵ���������й���
	OBJECT_IDENTIFIER(6),
	
	//ʹ��UTF8������ַ���������ʾ����
	UTF8_STRING(12),
	
	//��������ͼ��ϣ���������
	SEQUENCE(16),
	
	//��������ͼ��ϣ���������
	SET(17),
	
	//����ɴ�ӡ���ַ�����������ͨ��new String(byte[])�ķ�ʽ����ת������ʾ���ַ���
	PRINTABLE_STRING(19),
	
	//��8�����ַ����ɵ��ַ���
	T61_STRING(20),
	
	//ASCII���ʾ���ַ���
	IA5_STRING(22),
	
	//����ʾ����ʱ��ֵ
	UTC_TIME(23),
	GENERAL_TIME(24);
	
	private int tagNum;
	
	private Asn1TypeName(int tagNum) {this.tagNum = tagNum; }
	
	public static Asn1TypeName getAsn1TypeNameByTagNum(int tagNum) {
		for(Asn1TypeName type : Asn1TypeName.values())
			if(type.tagNum == tagNum)
				return type;
		throw new InvalidParameterException("Tag number: " + tagNum + " is not defined in class Asn1TypeName!");
	}
}
