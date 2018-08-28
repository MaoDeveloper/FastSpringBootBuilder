package com.example.builder.writer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.builder.entity.DataEntity;
import com.example.builder.line.DataLine;
import com.example.tools.CamelHumpUnderLineSwitcher;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年3月2日 下午9:41:07 类说明
 */

public class ControllerFileWriter implements FileWriter {

	@Override
	public void writeFile(DataEntity entity) {

		String entityPackagePath = (entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".controller")
				.replaceAll("\\.", "/");
		String modulePackage = entity.getBasePackage() + "." + entity.getModule().toLowerCase();

		String fullPath = FileWriterFactory.EXPORT_PATH + "/" + entityPackagePath;
		File file = new File(fullPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		String className = entity.getClassName();
		String javaControllerFilePath = FileWriterFactory.EXPORT_PATH + "/" + entityPackagePath + "/" + className
				+ "Controller.java";

		String homePath = "";
		String[] packagePaths = entity.getBasePackage().split(".");
		if (packagePaths.length <= 2) {
			homePath = entity.getBasePackage();
		} else {
			homePath = packagePaths[0] + "." + packagePaths[1];
		}

		String serviceQuto = CamelHumpUnderLineSwitcher.toCamelHump(entity.getEntityName()) + "Service";

		String functionParamQuto = ServiceImplFileWriter.getFunctionParamQuto(entity);
		if (!functionParamQuto.equals("")) {
			functionParamQuto += ", ";
		}
		String needParamQuto = ServiceImplFileWriter.getNeedKeyDefineQuto(entity);
		if (!needParamQuto.equals("")) {
			needParamQuto += ", ";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("package " + modulePackage + ".controller;\n");

		builder.append("\n");
		builder.append("import java.util.HashMap;\n");
		builder.append("import java.util.List;");
		builder.append("\n");
		builder.append("import org.springframework.beans.factory.annotation.Autowired;\n");
		builder.append("import org.springframework.web.bind.annotation.RequestBody;\n");
		builder.append("import org.springframework.web.bind.annotation.RequestMapping;\n");
		builder.append("import org.springframework.web.bind.annotation.RequestParam;\n");
		builder.append("import org.springframework.web.bind.annotation.RestController;\n");
		builder.append("\n");
		builder.append("import " + entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".service."
				+ className + "Service;\n");

		builder.append("import " + homePath + ".core.utils.response.MessageEntity;\n");
		builder.append("import " + modulePackage + ".valobj.Param" + className + ";\n");
		builder.append("import " + modulePackage + ".valobj.Result" + className + ";\n");

		builder.append("\n");
		builder.append("\n");
		builder.append("@RestController\n");
		builder.append("@RequestMapping(\"/" + entity.getModule() + "/"
				+ CamelHumpUnderLineSwitcher.toUnderLine(entity.getEntityName()).replaceAll("_", "/") + "\")\n");
		builder.append("public class " +className + "Controller {\n");
		builder.append("\n");
		builder.append("	@Autowired\n");
		builder.append("	private " + className + "Service " + serviceQuto + ";\n");
		builder.append("\n");

		// list controller
		builder.append("	@RequestMapping(\"/list\")\n");
		builder.append("	public MessageEntity list(");
		String param = getControllerFunctionParam(entity);
		if (param != null && !param.trim().equals("")) {
			builder.append(getControllerFunctionParam(entity));
			builder.append("			@RequestParam(value = \"page\", defaultValue = \"1\") int page,\n");
		} else {
			builder.append("@RequestParam(value = \"page\", defaultValue = \"1\") int page,\n");
		}
		builder.append("			@RequestParam(value = \"size\", defaultValue = \"10\") int size) {\n");
		builder.append("		MessageEntity.Builder builder = new MessageEntity.Builder();\n");
		builder.append("		List<Result" + className + "> result = " + serviceQuto + ".list(" + functionParamQuto
				+ "page, size);\n");
		builder.append("		int count = " + serviceQuto + ".count("
				+ ServiceImplFileWriter.getFunctionParamQuto(entity) + ");");
		builder.append("		HashMap<String, Object> fullResult = new HashMap<>();\n");
		builder.append("		fullResult.put(\"total\", count);\n");
		builder.append("		fullResult.put(\"list\", result);\n");
		builder.append("\n");
		builder.append("		return builder.code(200).success(true).content(fullResult).create();\n");
		builder.append("	}\n");
		builder.append("\n");

		// save controller
		builder.append("	@RequestMapping(\"/save\")\n");
		builder.append("	public MessageEntity save(@RequestBody Param" + className + " param) {\n");
		builder.append("		MessageEntity.Builder builder = new MessageEntity.Builder();\n");
		builder.append("		" + serviceQuto + ".save(" + needParamQuto + "param);\n");
		builder.append("		return builder.code(200).success(true).create();\n");
		builder.append("	}\n");
		builder.append("\n");

		// delete controller
		builder.append("	@RequestMapping(\"/delete\")\n");
		builder.append("	public MessageEntity delete(@RequestBody Param" + className + " param) {\n");
		builder.append("		MessageEntity.Builder builder = new MessageEntity.Builder();\n");
		builder.append("		" + serviceQuto + ".delete(" + needParamQuto + "param);\n");
		builder.append("		return builder.code(200).success(true).create();\n");
		builder.append("	}\n");
		builder.append("\n");
		builder.append("}\n");

		FileWriterFactory.writeDataIntoFile(javaControllerFilePath, builder.toString().getBytes());
	}

	private String getControllerFunctionParam(DataEntity entity) {
		List<DataLine> lines = new ArrayList<>();
		lines.addAll(entity.getAllConditionKeys());
		StringBuilder ControllerFunctionParam = new StringBuilder();
		for (int i = 0; i < lines.size(); i++) {
			DataLine line = lines.get(i);
			ControllerFunctionParam.append("			@RequestParam(value = \"" + line.getCamelHumpName()
					+ "\", defaultValue = \"\") " + line.getType() + " " + line.getCamelHumpName() + ",\n");
		}
		return ControllerFunctionParam.toString();
	}

}
