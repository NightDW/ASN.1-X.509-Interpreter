package com.laidw.asn1.to;

//����һ���࣬���ڱ�ʾContent�ķ�Χ������ʵ����Ҫ���������
//��Ϊ���ǿ���ֱ����long�������ݵ�ǰ32λ�洢from����32λ�洢to
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
