package com.example.builder.writer;

import java.io.File;

import com.example.builder.entity.DataEntity;
import com.example.builder.line.DataLine;
import com.example.tools.CamelHumpUnderLineSwitcher;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年3月2日 上午11:04:17 类说明
 */

public class PojoFileWriter implements FileWriter {

	@Override
	public void writeFile(DataEntity entity) {

		String entityPackagePath = (entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".entity")
				.replaceAll("\\.", "/");

		String fullPath = FileWriterFactory.EXPORT_PATH + "/" + entityPackagePath;
		File file = new File(fullPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		String className = entity.getClassName();
		String pojoFilePath = FileWriterFactory.EXPORT_PATH + "/" + entityPackagePath + "/" + className + ".java";

		StringBuilder builder = new StringBuilder();
		builder.append("package " + entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".entity;");
		builder.append("\n");
		builder.append(entity.getImportString());
		builder.append("\n");
		builder.append("public class " + className + " {");
		builder.append("\n");
		builder.append("\n");
		// 不需要这些键定义
		// for (DataLine line : entity.getDataLines()) {
		// builder.append(line.getAttributeKeyDefine());
		// }
		// builder.append("\n");
		builder.append(DataLine.getIdAttribute());
		for (DataLine line : entity.getDataLines()) {
			builder.append(line.getPojoAttriture());
		}
		builder.append(DataLine.getStatusAttribute());
		builder.append(DataLine.getCreateAtAttribute());
		builder.append(DataLine.getUpdateAtAttribute());
		builder.append("\n");

		builder.append(DataLine.getIdGetSet());
		for (DataLine line : entity.getDataLines()) {
			builder.append(line.getPojoGetSetLine());
		}
		builder.append(DataLine.getStatusGetSet());
		builder.append(DataLine.getCreateAtGetSet());
		builder.append(DataLine.getUpdateAtGetSet());
		builder.append("}");

		FileWriterFactory.writeDataIntoFile(pojoFilePath, builder.toString().getBytes());
	}

}
