package com.laidw.asn1.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.laidw.asn1.bean.Asn1TypeName;
import com.laidw.asn1.handler.TypeSimpleHandler;
import com.laidw.asn1.handler.impl.BitStringSimpleHandler;
import com.laidw.asn1.handler.impl.BooleanSimpleHandler;
import com.laidw.asn1.handler.impl.HexStringSimpleHandler;
import com.laidw.asn1.handler.impl.IntegerSimpleHandler;
import com.laidw.asn1.handler.impl.NormalStringSimpleHandler;
import com.laidw.asn1.handler.impl.NullSimpleHandler;
import com.laidw.asn1.handler.impl.OidSimpleHandler;
import com.laidw.asn1.handler.impl.UtcTimeSimpleHandler;
import com.laidw.asn1.handler.impl.Utf8StringSimpleHandler;
import com.laidw.asn1.to.ContentRange;
import com.laidw.asn1.to.MyByteArr;

public class Asn1Helper {
	
	public static Map<Asn1TypeName, TypeSimpleHandler> map;
	
	static {
		map = new HashMap<>();
		map.put(Asn1TypeName.CTX_SPECIFIC_00, new HexStringSimpleHandler());
		map.put(Asn1TypeName.CTX_SPECIFIC_01, new NormalStringSimpleHandler());
		map.put(Asn1TypeName.CTX_SPECIFIC_02, new NormalStringSimpleHandler());
		map.put(Asn1TypeName.CTX_SPECIFIC_06, new NormalStringSimpleHandler());
		map.put(Asn1TypeName.BOOLEAN, new BooleanSimpleHandler());
		map.put(Asn1TypeName.INTEGER, new IntegerSimpleHandler());
		map.put(Asn1TypeName.BIT_STRING, new BitStringSimpleHandler());
		map.put(Asn1TypeName.OCTET_STRING, new HexStringSimpleHandler());
		map.put(Asn1TypeName.NULL, new NullSimpleHandler());
		map.put(Asn1TypeName.OBJECT_IDENTIFIER, new OidSimpleHandler());
		map.put(Asn1TypeName.UTF8_STRING, new Utf8StringSimpleHandler());
		map.put(Asn1TypeName.PRINTABLE_STRING, new NormalStringSimpleHandler());
		map.put(Asn1TypeName.IA5_STRING, new NormalStringSimpleHandler());
		map.put(Asn1TypeName.UTC_TIME, new UtcTimeSimpleHandler());
	}

	public static String newLine = System.getProperty("line.separator");
	public static String getSpace(int level) {
		String space = "    ";
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < level; i++)
			sb.append(space);
		return sb.toString();
	}
	
	public static int parseInt(MyByteArr arr, int begin, int length) {
		if(length > 4) length = 4;
		int ret = 0;
		for(int i = begin; i < begin + length; i++) {
			ret <<= 8;
			ret += arr.get(i);
		}
		return ret;
	}
	
	public static String toHexString(MyByteArr arr, int begin, int length) {
		StringBuilder sb = new StringBuilder();
		String tem;
		for(int i = begin; i < begin + length; i++) {
			tem = Integer.toHexString(arr.get(i));
			sb.append("00".substring(tem.length())).append(tem);
		}
		return sb.toString();
	}
	
	public static String toBinString(MyByteArr arr, int begin, int length) {
		StringBuilder sb = new StringBuilder();
		String tem;
		for(int i = begin; i < begin + length; i++) {
			tem = Integer.toBinaryString(arr.get(i));
			sb.append("00000000".substring(tem.length())).append(tem);
		}
		return sb.toString();
	}
	
	//�÷����Ĺ��������ڣ���ȡ��"class Abc{int x1; int x2;}"�д�������������ݣ�����ȡ��"int x1;"�е�"x1;"
	public static MyByteArr getContent(MyByteArr arr) {
		ContentRange range = getContentRange(arr);
		return arr.getSubArr(range.getFrom(), range.getTo());
	}
	
	//�÷����Ĺ��������ڣ���"int x1; int x2;"�з�Ϊ"int x1;"��"int x2;"
	public static List<MyByteArr> split(MyByteArr arr) {
		List<MyByteArr> list = new ArrayList<>();
		ContentRange range;
		while(arr.getLength() != 0) {
			range = getContentRange(arr);
			list.add(arr.getSubArr(0, range.getTo()));
			arr = arr.getSubArr(range.getTo(), arr.getLength());
		}
		return list;
	}
	
	//�����з�content������з�ʧ�ܣ�˵��content���������Ǽ����͵�����
	//�з�ʧ���򷵻�null���зֳɹ��򷵻��зֺ�Ľ��
	public static List<MyByteArr> trySplit(MyByteArr content) {
		List<MyByteArr> ret = null;
		try {
			ret = split(content);
		} catch(Exception e) {
			return null;
		}
		try {
			//�պ�����ȷ�з��껹����������Ҫ�ٽ�һ���жϣ����ӿ��Ŷ�
			for(MyByteArr arr : ret)
				Asn1TypeName.getAsn1TypeNameByTagNum(arr.get(0) & 0xdf);
		} catch(Exception e) {
			return null;
		}
		return ret;
	}
	
	public static ContentRange getContentRange(MyByteArr arr) {
		int tem = arr.get(1);
		//���temС��128����ôtem��ʾ�ľ���data�ĳ���
		if(tem < 128)
			return new ContentRange(2, 2 + tem);
		//���tem����128���򲻶�������ң�ֱ���ҵ�������ǣ������ֽڣ�0000��
		if(tem == 128)
			return new ContentRange(2, findEndSignalIndex(arr, 2) + 2);
		//���tem����128����tem����128����������tem���ֽڱ�ʾ�Ĳ��������ĳ���
		tem -= 128;
		int length = parseInt(arr, 2, tem);
		return new ContentRange(2 + tem, 2 + tem + length);
	}
	
	public static int findEndSignalIndex(MyByteArr arr, int begin) {
		for(int i = begin; i < arr.getLength() - 1; i++)
			if(arr.get(i) == 0 && arr.get(i + 1) == 0)
				return i;
		return arr.getLength() - 2;
	}

	public static String parseSimpleContent(MyByteArr content, Asn1TypeName typeName) {
		if(map.get(typeName) == null)
			throw new RuntimeException("Unable to parse the content of " + typeName + "! You should create its type handler!");
		return map.get(typeName).handle(content);
	}
}