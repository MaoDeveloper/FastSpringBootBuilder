package com.example.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年2月14日 下午9:11:32 类说明
 */

public class CamelHumpUnderLineSwitcher {

	public static String toCamelHump(String s) {
		Pattern pattern = Pattern.compile("_[a-z]");
		Matcher matcher = pattern.matcher(s);
		StringBuffer builder = new StringBuffer();
		while (matcher.find()) {
			String m = matcher.group();
			matcher.appendReplacement(builder, m.replace("_", "").toUpperCase());
		}
		matcher.appendTail(builder);

		String result = builder.toString();
		result = result.replaceAll("_", "");
		return result;
	}

	public static String toUnderLine(String s) {
		Pattern pattern = Pattern.compile("[A-Z]");
		Matcher matcher = pattern.matcher(s);
		StringBuffer builder = new StringBuffer();
		while (matcher.find()) {
			String m = matcher.group();
			matcher.appendReplacement(builder, "_" + m.toLowerCase());
		}
		matcher.appendTail(builder);
		String result = builder.toString();
		return result;
	}

}
