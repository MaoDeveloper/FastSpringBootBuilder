package com.example.builder.line;

import com.example.builder.entity.DataEntity;
import com.example.tools.CamelHumpUnderLineSwitcher;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年2月13日 下午4:06:58 类说明
 */

public class StringDataLine extends DataLine {

	@Override
	public String getSqlLine() {
		if (length == 0) {
			length = 64;
		}
		String key = CamelHumpUnderLineSwitcher.toUnderLine(name);
		return "  `" + key + "` varchar(" + length + ") NOT NULL " + getSqlComment() + ",";
	}

	@Override
	public String fromJsonGetDataString(DataEntity entity) {
		return "param.getString(" + entity.getPojoClassName() + ".KEY_"
				+ CamelHumpUnderLineSwitcher.toUnderLine(name).toUpperCase() + ")";
	}
}
