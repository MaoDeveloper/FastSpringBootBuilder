package com.example.builder.line;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年2月14日 下午5:48:56 类说明
 */

public class AttributeLine implements BaseLine {

	private String name;
	private String value;

	public AttributeLine(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
