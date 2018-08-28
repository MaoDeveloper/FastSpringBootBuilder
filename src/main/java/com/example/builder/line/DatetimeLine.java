package com.example.builder.line;

import com.example.builder.entity.DataEntity;
import com.example.tools.CamelHumpUnderLineSwitcher;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年2月13日 下午4:08:20 类说明
 */

public class DatetimeLine extends DataLine {

	@Override
	public String getSqlLine() {
		String key = CamelHumpUnderLineSwitcher.toUnderLine(name);
		return "  `" + key + "` datetime DEFAULT '0000-00-00 00:00:00' " + getSqlComment() + ",";
	}

	@Override
	public String fromJsonGetDataString(DataEntity entity) {
		return "dateFormat.parse(param.getString(" + entity.getPojoClassName() + ".KEY_"
				+ CamelHumpUnderLineSwitcher.toUnderLine(name).toUpperCase() + "))";
	}

}
