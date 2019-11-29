package com.laidw.asn1.bean;

import java.security.InvalidParameterException;

//定义ASN.1中的一些常用类型
//注意，下列注释中没有说明是复杂类型的或看起来像简单类型的其实都是有可能是复杂类型的
//比如BIT_STRING和OCTET_STRING类型都可能会嵌套其它类型，此时称其为encapsulates的
//因此，不管是什么类型，最好先用处理复杂类型的方式处理一遍，处理失败才用处理简单类型的方式来处理
public enum Asn1TypeName {
	
	//几个属于context-specific类的类型，有简单类型也有复杂类型的；其它类型都是universal类的
	CTX_SPECIFIC_00(128), CTX_SPECIFIC_01(129), CTX_SPECIFIC_02(130), CTX_SPECIFIC_03(131), 
	CTX_SPECIFIC_04(132), CTX_SPECIFIC_05(133), CTX_SPECIFIC_06(134), CTX_SPECIFIC_07(135),
	
	//Boolean
	BOOLEAN(1),
	
	//普通的整数
	INTEGER(2),
	
	//字符串"00000001"用BIT_STRING的方式表示就是一个字节0x01
	BIT_STRING(3),
	
	//字符串"ab"用OCTET_STRING的方式表示就是一个字节0xab
	OCTET_STRING(4),
	
	//空值
	NULL(5),
	
	//由一系列整形序列构成
	OBJECT_IDENTIFIER(6),
	
	//使用UTF8编码的字符串，可显示中文
	UTF8_STRING(12),
	
	//有序的类型集合，复杂类型
	SEQUENCE(16),
	
	//无序的类型集合，复杂类型
	SET(17),
	
	//任意可打印的字符串，其数据通过new String(byte[])的方式即可转成它表示的字符串
	PRINTABLE_STRING(19),
	
	//由8比特字符构成的字符串
	T61_STRING(20),
	
	//ASCII码表示的字符串
	IA5_STRING(22),
	
	//都表示的是时间值
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
