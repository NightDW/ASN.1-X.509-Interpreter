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
	
	//该方法的功能类似于：提取出"class Abc{int x1; int x2;}"中大括号里面的内容，或提取出"int x1;"中的"x1;"
	public static MyByteArr getContent(MyByteArr arr) {
		ContentRange range = getContentRange(arr);
		return arr.getSubArr(range.getFrom(), range.getTo());
	}
	
	//该方法的功能类似于：把"int x1; int x2;"切分为"int x1;"和"int x2;"
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
	
	//尝试切分content，如果切分失败，说明content本身代表的是简单类型的数据
	//切分失败则返回null，切分成功则返回切分后的结果
	public static List<MyByteArr> trySplit(MyByteArr content) {
		List<MyByteArr> ret = null;
		try {
			ret = split(content);
		} catch(Exception e) {
			return null;
		}
		try {
			//刚好能正确切分完还不够，还需要再进一步判断，增加可信度
			for(MyByteArr arr : ret)
				Asn1TypeName.getAsn1TypeNameByTagNum(arr.get(0) & 0xdf);
		} catch(Exception e) {
			return null;
		}
		return ret;
	}
	
	public static ContentRange getContentRange(MyByteArr arr) {
		int tem = arr.get(1);
		//如果tem小于128，那么tem表示的就是data的长度
		if(tem < 128)
			return new ContentRange(2, 2 + tem);
		//如果tem等于128，则不断往后查找，直到找到结束标记（两个字节，0000）
		if(tem == 128)
			return new ContentRange(2, findEndSignalIndex(arr, 2) + 2);
		//如果tem大于128，则tem减掉128，接下来的tem个字节表示的才是真正的长度
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