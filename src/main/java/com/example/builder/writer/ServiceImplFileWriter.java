package com.example.builder.writer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.builder.entity.DataEntity;
import com.example.builder.line.DataLine;
import com.example.builder.line.DatetimeLine;
import com.example.tools.CamelHumpUnderLineSwitcher;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年3月2日 下午3:24:51 类说明
 */

public class ServiceImplFileWriter implements FileWriter {

	@Override
	public void writeFile(DataEntity entity) {

		String entityPackagePath = (entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".service")
				.replaceAll("\\.", "/");
		String modulePackage = entity.getBasePackage() + "." + entity.getModule().toLowerCase();

		String fullPath = FileWriterFactory.EXPORT_PATH + "/" + entityPackagePath;
		File file = new File(fullPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		String className = entity.getClassName();
		String javaServiceInterfaceFilePath = FileWriterFactory.EXPORT_PATH + "/" + entityPackagePath + "/" + className
				+ "ServiceImpl.java";

		// String homePath = "";
		// String[] packagePaths = entity.getBasePackage().split(".");
		// if (packagePaths.length <= 2) {
		// homePath = entity.getBasePackage();
		// } else {
		// homePath = packagePaths[0] + "." + packagePaths[1];
		// }

		String repoQuto = CamelHumpUnderLineSwitcher.toCamelHump(entity.getEntityName()) + "Repo";

		StringBuilder builder = new StringBuilder();
		builder.append("package " + entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".service;\n");
		builder.append("\n");
		builder.append("import java.text.SimpleDateFormat;\n");
		builder.append("import java.util.ArrayList;\n");
		builder.append("import java.util.Collection;\n");
		builder.append("import java.util.List;\n");
		builder.append("import java.util.UUID;\n");
		builder.append("\n");
		builder.append("import org.springframework.beans.factory.annotation.Autowired;\n");
		builder.append("import org.springframework.stereotype.Service;\n");
		builder.append("\n");
		builder.append("import " + modulePackage + ".entity." + className + ";\n");
		builder.append("import " + modulePackage + ".valobj.Param" + className + ";\n");
		builder.append("import " + modulePackage + ".valobj.Result" + className + ";\n");
		builder.append("import " + modulePackage + ".repository." + className + "Repo;\n");

		builder.append("\n");
		builder.append("\n");
		builder.append("@Service\n");
		builder.append("public class " + className + "ServiceImpl implements " + className + "Service {\n");
		builder.append("\n");
		builder.append("	private SimpleDateFormat dateFormat = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");\n");
		builder.append("\n");
		builder.append("/***************************以下是模块内部接口注入***************************/\n");
		builder.append("	@Autowired\n");
		builder.append("	private " + className + "Repo " + repoQuto + ";\n");
		builder.append("\n");
		builder.append("/***************************以下是模块外部服务注入***************************/\n");
		builder.append("\n");

		String exception = entity.getExceptionEntity();
		if (exception != null) {
			String[] ss = entity.getExceptionEntity().split("\\.");
			if (ss.length > 0)
				exception = ss[ss.length - 1];
		}

		// list method
		String functionParam = ServiceImplFileWriter.getFunctionParam(entity);
		if (!functionParam.equals("")) {
			functionParam += ", ";
		}
		builder.append("	@Override\n");
		builder.append("	public List<Result" + className + "> list(" + functionParam + "int page, int size) {\n");
		builder.append("		List<Result" + className + "> result = new ArrayList<>();\n");
		builder.append("\n");
		builder.append("		int skip = (page -1) * size;\n");
		builder.append("		Collection<" + className + "> list = this." + repoQuto + ".listByCondition("
				+ ServiceImplFileWriter.getFunctionParamQuto(entity)
				+ (ServiceImplFileWriter.getFunctionParamQuto(entity).equals("") ? "" : ", ") + "skip, size);\n");
		builder.append("		for (" + className + " obj : list) {\n");
		builder.append("			Result" + className + " res = new Result" + className + "();\n");
		builder.append("			res.initFromEntity(obj);\n");
		builder.append("			result.add(res);\n");
		builder.append("		}\n");
		builder.append("		return result;\n");
		builder.append("	}\n");
		builder.append("\n");

		builder.append("	@Override\n");
		builder.append("	public int count(" + ServiceImplFileWriter.getFunctionParam(entity) + ") {\n");
		builder.append("		int result = this." + repoQuto + ".countByCondition("
				+ ServiceImplFileWriter.getFunctionParamQuto(entity) + ");\n");
		builder.append("		return result;\n");
		builder.append("	}\n");
		builder.append("\n");

		// save method
		functionParam = ServiceImplFileWriter.getNeedKeyDefine(entity);
		if (!functionParam.equals("")) {
			functionParam += ", ";
		}
		builder.append("	@Override\n");
		builder.append("	public void save(" + functionParam + "Param" + className + " param) {\n");
		builder.append("		" + className + " obj = new " + className + "();\n");
		for (DataLine line : entity.getDataLines()) {
			if (line.isPrimary() || line.isNeedKey()) {
				continue;
			}
			String setMethod = CamelHumpUnderLineSwitcher.toCamelHump("set_" + line.getName());
			if (line.getClass().getName().equals(DatetimeLine.class.getName())) {
				builder.append("		try {\n");
				builder.append("			obj." + setMethod + "(param." + line.getMethod() + ");\n");
				builder.append("		} catch (Exception e) {\n");
				builder.append("			e.printStackTrace();\n");
				builder.append("		}\n");
			} else {
				builder.append("		obj." + setMethod + "(param." + line.getMethod() + ");\n");
			}
		}
		if (entity.hasPrimaryKey()) {
			builder.append("\n");
			DataLine primary = entity.getPrimaryDataLine();
			builder.append("		String primaryCode = "
					+ (primary == null ? "null" : "param." + primary.getMethod()) + ";");
			builder.append("\n");
			builder.append("		" + className + " old = null;\n");
			builder.append("		if (primaryCode != null) {\n");
			builder.append("			old = this." + repoQuto + ".getOne(primaryCode);\n");
			builder.append("			if(old != null) {\n");
			builder.append("				obj.setId(old.getId());\n");
			builder.append("			}\n");
			builder.append("		}\n");
		}
		builder.append("\n");
		builder.append("		if (obj.getId() != null) {\n");
		builder.append("			this." + repoQuto + ".update(obj);\n");
		builder.append("		} else {\n");

		if (entity.hasPrimaryKey()) {
			DataLine primary = entity.getPrimaryDataLine();
			String setMethod = primary.setMethod();
			builder.append("			obj." + setMethod + "(createPrimaryCode());\n");
		}
		for (int i = 0; i < entity.getAllNeedKeys().size(); i++) {
			DataLine line = entity.getAllNeedKeys().get(i);
			String setMethod = line.setMethod();
			builder.append("			obj." + setMethod + "(" + line.getCamelHumpName() + ");\n");
		}
		builder.append("			this." + repoQuto + ".create(obj);\n");
		builder.append("		}\n");
		builder.append("	}\n");
		builder.append("\n");

		// delete method
		builder.append("	@Override\n");
		builder.append("	public void delete(" + functionParam + "Param" + className + " param) {\n");
		if (entity.hasPrimaryKey()) {
			DataLine primary = entity.getPrimaryDataLine();
			builder.append("		String primaryCode = "
					+ (primary == null ? "null" : "param." + primary.getMethod()) + ";");
			builder.append("\n");
			builder.append("		" + className + " old = null;\n");
			builder.append("		if (primaryCode != null) {\n");
			builder.append("			old = this." + repoQuto + ".getOne(primaryCode);\n");
			builder.append("			if(old != null) {\n");
			builder.append("				this." + repoQuto + ".delete(old.getId());\n");
			builder.append("			}\n");
			builder.append("		}\n");
		}
		builder.append("	}\n");
		builder.append("	\n");

		// uuid method
		builder.append("	private String createPrimaryCode() {\n");
		builder.append("		return UUID.randomUUID().toString().replaceAll(\"-\", \"\");\n");
		builder.append("	}\n");

		builder.append("}");

		FileWriterFactory.writeDataIntoFile(javaServiceInterfaceFilePath, builder.toString().getBytes());
	}

	public static String getFunctionParam(DataEntity entity) {
		List<DataLine> lines = new ArrayList<>();
		lines.addAll(entity.getAllNeedKeys());
		lines.addAll(entity.getAllConditionKeys());
		StringBuilder functionParam = new StringBuilder();
		for (int i = 0; i < lines.size(); i++) {
			functionParam.append(lines.get(i).getJavaDefine());
			if (i < lines.size() - 1) {
				functionParam.append(", ");
			}
		}
		return functionParam.toString();
	}

	public static String getFunctionParamQuto(DataEntity entity) {
		List<DataLine> lines = new ArrayList<>();
		lines.addAll(entity.getAllNeedKeys());
		lines.addAll(entity.getAllConditionKeys());
		StringBuilder functionParam = new StringBuilder();
		for (int i = 0; i < lines.size(); i++) {
			functionParam.append(lines.get(i).getCamelHumpName());
			if (i < lines.size() - 1) {
				functionParam.append(", ");
			}
		}
		return functionParam.toString();
	}

	public static String getNeedKeyDefine(DataEntity entity) {
		List<DataLine> lines = new ArrayList<>();
		lines.addAll(entity.getAllNeedKeys());
		StringBuilder functionParam = new StringBuilder();
		for (int i = 0; i < lines.size(); i++) {
			functionParam.append(lines.get(i).getJavaDefine());
			if (i < lines.size() - 1) {
				functionParam.append(", ");
			}
		}
		return functionParam.toString();
	}

	public static String getNeedKeyDefineQuto(DataEntity entity) {
		List<DataLine> lines = new ArrayList<>();
		lines.addAll(entity.getAllNeedKeys());
		StringBuilder functionParam = new StringBuilder();
		for (int i = 0; i < lines.size(); i++) {
			functionParam.append(lines.get(i).getCamelHumpName());
			if (i < lines.size() - 1) {
				functionParam.append(", ");
			}
		}
		return functionParam.toString();
	}
}
