package com.example.builder.writer;

import java.io.File;

import com.example.builder.entity.DataEntity;
import com.example.tools.CamelHumpUnderLineSwitcher;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年3月2日 下午3:24:51 类说明
 */

public class ServiceFileWriter implements FileWriter {

	@Override
	public void writeFile(DataEntity entity) {

		String entityPackagePath = (entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".service")
				.replaceAll("\\.", "/");
		String valobjPackagePath = (entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".valobj");

		String fullPath = FileWriterFactory.EXPORT_PATH + "/" + entityPackagePath;
		File file = new File(fullPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		String className = entity.getClassName();

		String javaServiceInterfaceFilePath = FileWriterFactory.EXPORT_PATH + "/" + entityPackagePath + "/" + className
				+ "Service.java";

		StringBuilder builder = new StringBuilder();
		builder.append("package " + entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".service;\n");
		builder.append("\n");
		builder.append("import java.util.List;\n");
		builder.append("\n");
		builder.append("import " + valobjPackagePath + ".Param" + className + ";\n");
		builder.append("import " + valobjPackagePath + ".Result" + className + ";\n");
		builder.append("\n");
		builder.append("\n");
		builder.append("public interface " + className + "Service {\n");
		builder.append("\n");
		String exception = entity.getExceptionEntity();
		if (exception != null) {
			String[] ss = entity.getExceptionEntity().split("\\.");
			if (ss.length > 0)
				exception = ss[ss.length - 1];
		}
		String functionParam = ServiceImplFileWriter.getFunctionParam(entity);
		if (!functionParam.equals("")) {
			functionParam += ", ";
		}
		builder.append("	List<Result" + className + "> list(" + functionParam + "int page, int size);\n");
		builder.append("\n");

		builder.append("	int count(" + ServiceImplFileWriter.getFunctionParam(entity) + ");\n");
		builder.append("\n");

		functionParam = ServiceImplFileWriter.getNeedKeyDefine(entity);
		if (!functionParam.equals("")) {
			functionParam += ", ";
		}
		builder.append("	void save(" + functionParam + "Param" + className + " param);\n");
		builder.append("\n");
		builder.append("	void delete(" + functionParam + "Param" + className + " param);\n");
		builder.append("\n");
		builder.append("}");

		FileWriterFactory.writeDataIntoFile(javaServiceInterfaceFilePath, builder.toString().getBytes());
	}

}
