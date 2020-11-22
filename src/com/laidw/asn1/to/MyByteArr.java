package com.laidw.asn1.to;

import java.security.InvalidParameterException;

/**
 * 把文件数据读取到byte[]数组后需要对该数组进行分部分解析
 * 如果直接根据byte[]数组创建一个子数组会比较浪费时间和空间
 * 考虑到我们不会去更改byte[]数组，因此定义该类来对byte[]数组进行包装
 * 该类用起来和普通数组是一样的
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
		
		//如果from == to，则不管from和to是否合法，直接创建一个MyByteArr返回，反正也获取不到数据
		if(from == to)
			return new MyByteArr(this.bytes, this.begin + from, to - from);
		
		if(from < 0 || from >= length || to < 0 || to > length || from > to)
			throw new InvalidParameterException("Parameter wrong! from: " + from + ", to: " + to + "!");
		
		return new MyByteArr(this.bytes, this.begin + from, to - from);
	}
}
