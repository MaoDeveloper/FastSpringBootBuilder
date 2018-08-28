package com.example.builder.writer;

import java.io.File;

import com.example.builder.entity.DataEntity;
import com.example.builder.line.DataLine;
import com.example.tools.CamelHumpUnderLineSwitcher;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年3月2日 上午11:38:44 类说明
 */

public class JavaValObjFileWriter implements FileWriter {

	@Override
	public void writeFile(DataEntity entity) {
		this.writeParamFile(entity);
		this.writeResultFile(entity);
	}
	
	private void writeResultFile(DataEntity entity) {
		String entityPackagePath = (entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".entity");
		String packagePath = (entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".valobj")
				.replaceAll("\\.", "/");

		String fullPath = FileWriterFactory.EXPORT_PATH + "/" + packagePath;
		File file = new File(fullPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		String className = entity.getClassName();
		String pojoFilePath = FileWriterFactory.EXPORT_PATH + "/" + packagePath + "/Result" + className + ".java";

		StringBuilder builder = new StringBuilder();
		builder.append("package " + entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".valobj;");
		builder.append("\n");
		builder.append(entity.getImportString());
		builder.append("import " + entityPackagePath + "." + className + ";\n");
		builder.append("\n");
		builder.append("public class Result" + className + " {");
		builder.append("\n");
		builder.append("\n");
		for (DataLine line : entity.getDataLines()) {
			builder.append(line.getPojoAttriture());
		}
		builder.append("\n");

		for (DataLine line : entity.getDataLines()) {
			builder.append(line.getPojoGetLine());
		}
		// 定义初始化方法
		String p = entity.getParamsName();
		builder.append("	public void initFromEntity(" + className + " " + p + ") {\n");
		for (DataLine line : entity.getDataLines()) {
			builder.append("		this." + line.getQuota() + " = " + p + "." + line.getMethod() + ";\n");
		}
		builder.append("	}\n");
		builder.append("}");

		FileWriterFactory.writeDataIntoFile(pojoFilePath, builder.toString().getBytes());
	}

	private void writeParamFile(DataEntity entity) {
		String entityPackagePath = (entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".entity");
		String packagePath = (entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".valobj")
				.replaceAll("\\.", "/");

		String fullPath = FileWriterFactory.EXPORT_PATH + "/" + packagePath;
		File file = new File(fullPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		String className = entity.getClassName();
		String pojoFilePath = FileWriterFactory.EXPORT_PATH + "/" + packagePath + "/Param" + className + ".java";

		StringBuilder builder = new StringBuilder();
		builder.append("package " + entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".valobj;");
		builder.append("\n");
		builder.append(entity.getImportString());
		builder.append("import " + entityPackagePath + "." + className + ";\n");
		builder.append("\n");
		builder.append("public class Param" + className + " {");
		builder.append("\n");
		builder.append("\n");
		for (DataLine line : entity.getDataLines()) {
			builder.append(line.getPojoAttriture());
		}
		builder.append("\n");

		for (DataLine line : entity.getDataLines()) {
			builder.append(line.getPojoGetSetLine());
		}
		
		// 定义初始化方法
		builder.append("}");

		FileWriterFactory.writeDataIntoFile(pojoFilePath, builder.toString().getBytes());
	}

}
