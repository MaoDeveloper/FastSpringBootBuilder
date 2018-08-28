package com.example.builder.writer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.builder.entity.DataEntity;
import com.example.builder.line.DataLine;
import com.example.tools.CamelHumpUnderLineSwitcher;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年3月2日 上午11:38:44 类说明
 */

public class JavaRepoFileWriter implements FileWriter {

	@Override
	public void writeFile(DataEntity entity) {

		String entityPackagePath = (entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".repository")
				.replaceAll("\\.", "/");

		String fullPath = FileWriterFactory.EXPORT_PATH + "/" + entityPackagePath;
		File file = new File(fullPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		String name = CamelHumpUnderLineSwitcher.toCamelHump(entity.getEntityName());
		String className = entity.getClassName();

		String javaRepoFilePath = FileWriterFactory.EXPORT_PATH + "/" + entityPackagePath + "/" + className
				+ "Repo.java";

//		String homePath = "";
//		String[] packagePaths = entity.getBasePackage().split(".");
//		if (packagePaths.length <= 2) {
//			homePath = entity.getBasePackage();
//		} else {
//			homePath = packagePaths[0] + "." + packagePaths[1];
//		}

		StringBuilder builder = new StringBuilder();
		builder.append(
				"package " + entity.getBasePackage() + "." + entity.getModule().toLowerCase() + ".repository;\n");
		builder.append("\n");
		builder.append("import java.util.Collection;\n");
		builder.append("\n");
		builder.append("import org.apache.ibatis.annotations.Insert;\n");
		builder.append("import org.apache.ibatis.annotations.Options;\n");
		builder.append("import org.apache.ibatis.annotations.Param;\n");
		builder.append("import org.apache.ibatis.annotations.Update;\n");
		builder.append("import org.apache.ibatis.annotations.Select;\n");
		builder.append("\n");
		builder.append("import " + entity.getBasePackage() + "." + entity.getModule() + ".entity." + className + ";\n");
		builder.append("\n");
		builder.append("public interface " + className + "Repo {\n");
		builder.append("\n");

		List<DataLine> dataLines = entity.getDataLines();

		// create method
		builder.append("	@Insert(\"INSERT INTO " + entity.getTableName() + "(");
		for (int i = 0; i < dataLines.size(); i++) {
			DataLine line = dataLines.get(i);
			builder.append("`" + CamelHumpUnderLineSwitcher.toUnderLine(line.getName()) + "`");
			if (i < dataLines.size() - 1) {
				builder.append(",");
			}
		}
		builder.append(",`create_at`) \"\n");
		builder.append("	+\"VALUES(");
		for (int i = 0; i < dataLines.size(); i++) {
			DataLine line = dataLines.get(i);
			builder.append("#{" + CamelHumpUnderLineSwitcher.toCamelHump(line.getName()) + "}");
			if (i < dataLines.size() - 1) {
				builder.append(",");
			}
		}
		builder.append(",NOW())\")\n");
		builder.append("	@Options(keyProperty = \"id\", keyColumn = \"id\", useGeneratedKeys = true)\n");
		builder.append("	Integer create(" + className + " " + name + ");\n");
		builder.append("\n");

		// update method
		builder.append("	@Update(\"<script>UPDATE " + entity.getTableName() + " SET \"\n");
		for (int i = 0; i < dataLines.size(); i++) {
			DataLine line = dataLines.get(i);
			String keyCamelHump = CamelHumpUnderLineSwitcher.toCamelHump(line.getName());
			String keyUnderline = CamelHumpUnderLineSwitcher.toUnderLine(line.getName());
			String l = "	+\"<if test='" + keyCamelHump + " != null'>" + keyUnderline + "=#{" + keyCamelHump
					+ "},</if>\"\n";
			builder.append(l);
		}
		builder.append("	+\"update_at=NOW() WHERE id=#{id}</script>\")\n");
		builder.append("	Integer update(" + className + " " + name + ");\n");
		builder.append("\n");

		// delete method
		builder.append("	@Update(\"UPDATE " + entity.getTableName() + " SET status=0 WHERE id=#{id}\")\n");
		builder.append("	Integer delete(@Param(\"id\") Long id);\n");
		builder.append("\n");

		// count method
		String param = buildFunctionParam(entity);
		builder.append("	@Select(\"<script>SELECT COUNT(*) FROM " + entity.getTableName() + " WHERE status=1 "
				+ this.buildSqlString(entity) + "</script>\")\n");
		builder.append("	Integer countByCondition(" + param + ");\n");
		builder.append("\n");

		// query list method
		builder.append("	@Select(\"<script>SELECT * FROM " + entity.getTableName() + " WHERE status=1 "
				+ buildSqlString(entity) + " ORDER BY id DESC LIMIT #{skip}, #{size}</script>\")\n");
		builder.append("	Collection<" + className + "> listByCondition(" + param + (param.equals("") ? "" : ", ")
				+ "@Param(\"skip\") int skip, @Param(\"size\") int size);\n");
		builder.append("\n");

		if (entity.hasPrimaryKey()) {
			DataLine line = entity.getPrimaryDataLine();
			String keyCamelHump = CamelHumpUnderLineSwitcher.toCamelHump(line.getName());
			String keyUnderline = CamelHumpUnderLineSwitcher.toUnderLine(line.getName());
			String type = line.getType();
			String functionParam = "@Param(\"" + keyCamelHump + "\") " + type + " " + keyCamelHump;
			// get one method
			builder.append("	@Select(\"SELECT * FROM " + entity.getTableName() + " WHERE status=1 AND "
					+ keyUnderline + " = #{" + keyCamelHump + "}\")\n");
			builder.append("	" + className + " getOne(" + functionParam + ");\n");
			builder.append("\n");

			// 'in' query
			functionParam = "@Param(\"" + keyCamelHump + "s\") Collection<" + type + "> " + keyCamelHump + "s";
			builder.append("	@Select(\"<script>SELECT * FROM " + entity.getTableName() + " WHERE status=1 AND "
					+ keyUnderline + " IN <foreach collection='" + keyCamelHump
					+ "s' item='item' open='(' separator=',' close=')'>#{item}</foreach></script>\")\n");
			builder.append("	Collection<" + className + "> getList(" + functionParam + ");\n");
			builder.append("\n");
		}

		// query method
		builder.append("	@Select(\"SELECT * FROM " + entity.getTableName() + " WHERE id=#{id}\")\n");
		builder.append("	" + className + " get(@Param(\"id\") Long id);\n");
		builder.append("\n");

		builder.append("}");
		FileWriterFactory.writeDataIntoFile(javaRepoFilePath, builder.toString().getBytes());
	}

	private String buildFunctionParam(DataEntity entity) {

		List<DataLine> dataLines = new ArrayList<>();
		dataLines.addAll(entity.getAllNeedKeys());
		dataLines.addAll(entity.getAllConditionKeys());
		String param = "";
		for (int i = 0; i < dataLines.size(); i++) {
			DataLine line = dataLines.get(i);
			String keyCamelHump = CamelHumpUnderLineSwitcher.toCamelHump(line.getName());
			String type = line.getType();
			param += "@Param(\"" + keyCamelHump + "\") " + type + " " + keyCamelHump;
			if (i < dataLines.size() - 1) {
				param += ", ";
			}
		}
		return param;
	}

	private String buildSqlString(DataEntity entity) {

		List<DataLine> dataLines = entity.getAllNeedKeys();
		String param = "";
		for (int i = 0; i < dataLines.size(); i++) {
			DataLine line = dataLines.get(i);
			String keyCamelHump = CamelHumpUnderLineSwitcher.toCamelHump(line.getName());
			String keyUnderline = CamelHumpUnderLineSwitcher.toUnderLine(line.getName());
			param += " AND " + keyUnderline + " = #{" + keyCamelHump + "}";
		}

		dataLines = entity.getAllConditionKeys();
		for (int i = 0; i < dataLines.size(); i++) {
			DataLine line = dataLines.get(i);
			String keyCamelHump = CamelHumpUnderLineSwitcher.toCamelHump(line.getName());
			String keyUnderline = CamelHumpUnderLineSwitcher.toUnderLine(line.getName());
			param += "<if test='" + keyCamelHump + " != \\\"\\\"'> AND " + keyUnderline + " LIKE CONCAT('%', #{"
					+ keyCamelHump + "}, '%')</if>";
		}
		return param;
	}

}
