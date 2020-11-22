package com.laidw.asn1.to;

import java.security.InvalidParameterException;

/**
 * ���ļ����ݶ�ȡ��byte[]�������Ҫ�Ը�������зֲ��ֽ���
 * ���ֱ�Ӹ���byte[]���鴴��һ���������Ƚ��˷�ʱ��Ϳռ�
 * ���ǵ����ǲ���ȥ����byte[]���飬��˶����������byte[]������а�װ
 * ��������������ͨ������һ����
 */
public class MyByteArr {
	
	private byte[] bytes;
	private int begin;
	private int length;
	
	public MyByteArr(byte[] bytes, int begin, int length) {
		this.bytes = bytes;
		this.begin = begin;
		this.length = length;
	}
	
	public int get(int index) {
		if(index >= length || index < 0)
			throw new IndexOutOfBoundsException("Index: " + index + " is larger than length: " + length + "!");
		return Byte.toUnsignedInt(bytes[index + begin]);
	}
	
	public int getBegin() {
		return this.begin;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public byte[] getBytes() {
		byte[] ret = new byte[length];
        System.arraycopy(bytes, begin, ret, 0, length);
		return ret;
	}
	
	public MyByteArr getSubArr(int from, int to) {
		
		//���from == to���򲻹�from��to�Ƿ�Ϸ���ֱ�Ӵ���һ��MyByteArr���أ�����Ҳ��ȡ��������
		if(from == to)
			return new MyByteArr(this.bytes, this.begin + from, to - from);
		
		if(from < 0 || from >= length || to < 0 || to > length || from > to)
			throw new InvalidParameterException("Parameter wrong! from: " + from + ", to: " + to + "!");
		
		return new MyByteArr(this.bytes, this.begin + from, to - from);
	}
}
