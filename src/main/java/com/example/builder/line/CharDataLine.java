package com.example.builder.line;

import com.example.builder.entity.DataEntity;
import com.example.tools.CamelHumpUnderLineSwitcher;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年2月28日 上午11:51:51 类说明
 */

public class CharDataLine extends DataLine {

	@Override
	public String getSqlLine() {
		String key = CamelHumpUnderLineSwitcher.toUnderLine(name);
		return "  `" + key + "` char(" + length + ") NOT NULL " + getSqlComment() + ",";
	}

	@Override
	public String fromJsonGetDataString(DataEntity entity) {
		return "param.getString(" + entity.getPojoClassName() + ".KEY_"
				+ CamelHumpUnderLineSwitcher.toUnderLine(name).toUpperCase() + ")";
	}

}
