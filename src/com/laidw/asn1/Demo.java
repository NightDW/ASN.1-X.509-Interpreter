package com.laidw.asn1;

import java.io.IOException;

import com.laidw.asn1.bean.Asn1Type;
import com.laidw.asn1.interpreter.impl.x509.Certificate;

public class Demo {
	public static void main(String[] args) throws IOException {
		Asn1Type baidu = new Asn1Type("C:\\Users\\Acer\\Desktop\\baidu.cer");
		Asn1Type bilibili = new Asn1Type("C:\\Users\\Acer\\Desktop\\bilibili.cer");
		Asn1Type csdn = new Asn1Type("C:\\Users\\Acer\\Desktop\\csdn.cer");
		
		Certificate baiducer = new Certificate(baidu);
		Certificate bilibilicer = new Certificate(bilibili);
		Certificate csdncer = new Certificate(csdn);
		
		System.out.println(baidu.getStructTree());
		System.out.println(bilibili.getStructTree());
		System.out.println(csdn.getStructTree());
		
		System.out.println(baiducer.getParseResult(0));
		System.out.println(bilibilicer.getParseResult(0));
		System.out.println(csdncer.getParseResult(0));
	}
}
