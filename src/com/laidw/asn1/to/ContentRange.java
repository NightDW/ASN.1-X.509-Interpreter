package com.laidw.asn1.to;

//定义一个类，用于表示Content的范围，但其实不需要定义这个类
//因为我们可以直接用long类型数据的前32位存储from，后32位存储to
public class ContentRange{

    private int from;
	private int to;

	public ContentRange(int from, int to) {
		this.from = from;
		this.to = to;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}
}
