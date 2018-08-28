package com.example.builder.line;

import com.example.builder.entity.DataEntity;
import com.example.tools.CamelHumpUnderLineSwitcher;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年2月13日 下午4:07:31 类说明
 */

public class IntegerDataLine extends DataLine {

	@Override
	public String getSqlLine() {
		String key = CamelHumpUnderLineSwitcher.toUnderLine(name);
		return "  `" + key + "` int(11) NOT NULL DEFAULT '0' " + getSqlComment() + ",";
	}

	@Override
	public String fromJsonGetDataString(DataEntity entity) {
		return "param.getInt(" + entity.getPojoClassName() + ".KEY_"
				+ CamelHumpUnderLineSwitcher.toUnderLine(name).toUpperCase() + ")";
	}
}
