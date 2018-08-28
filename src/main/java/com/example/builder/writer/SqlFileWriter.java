package com.example.builder.writer;

import com.example.builder.entity.DataEntity;
import com.example.builder.line.DataLine;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年3月2日 上午11:32:17 类说明
 */

public class SqlFileWriter implements FileWriter {

	@Override
	public void writeFile(DataEntity entity) {

		String sqlFilePath = FileWriterFactory.EXPORT_PATH + "/struct.sql";
		// 关键的方法
		String tableName = entity.getTableName();
		StringBuilder builder = new StringBuilder();
		builder.append("-- ----------------------------;\n");
		builder.append("--  Table structure for `" + tableName + "`;\n");
		builder.append("-- ----------------------------;\n");
		builder.append("DROP TABLE IF EXISTS `" + tableName + "`;\n");
		builder.append("CREATE TABLE `" + tableName + "` (\n");
		builder.append("  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n");
		for (DataLine line : entity.getDataLines()) {
			builder.append(line.getSqlLine() + "\n");
		}

		builder.append("  `status` tinyint(4) NOT NULL DEFAULT '1',\n");
		builder.append("  `create_at` datetime NOT NULL ,\n");
		builder.append("  `update_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n");
		builder.append("  PRIMARY KEY (`id`)\n");
		builder.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;\n");
		builder.append("\n");

		FileWriterFactory.writeDataIntoFile(sqlFilePath, builder.toString().getBytes());

	}

}
