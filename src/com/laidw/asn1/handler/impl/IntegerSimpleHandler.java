package com.laidw.asn1.handler.impl;

import java.math.BigInteger;

import com.laidw.asn1.to.MyByteArr;
import com.laidw.asn1.tool.Asn1Helper;

public class IntegerSimpleHandler extends AbstractSimpleHandler {

	protected String handleInternal(MyByteArr content) {

		//ע�⣬ASN.1�е�INTEGER�����кܶ�λ��������Java��long��װ����
		//���ʹ��BigInteger����ʾINTEGER��˳���ʮ�������ַ�������
		return new BigInteger(content.getBytes()).toString() + " (0x" + Asn1Helper.toHexString(content, 0, content.getLength()) + ")";
	}
}
