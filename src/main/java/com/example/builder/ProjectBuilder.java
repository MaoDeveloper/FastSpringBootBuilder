package com.example.builder;

import java.util.ArrayList;
import java.util.List;

import com.example.builder.entity.DataEntity;
import com.example.builder.line.AttributeLine;
import com.example.builder.line.BaseLine;
import com.example.builder.line.DataLine;
import com.example.builder.line.EmptyLine;
import com.example.builder.writer.FileWriterFactory;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年2月13日 下午3:57:12 类说明
 */

public class ProjectBuilder {
	private String filepath;

	private FileWriterFactory fileWriterFactory;

	public ProjectBuilder(String filepath, String exportPath) throws Exception {
		this.filepath = filepath;
		fileWriterFactory = new FileWriterFactory(exportPath);
	}

	public void create() {
		// 读取信息
		String basePackage = "";
		String module = "";
		String abstractEntity = "";
		String exceptionEntity = "";

		// 出示化数据
		List<DataEntity> entities = new ArrayList<>();
		ExcelReader reader = new ExcelReader(filepath);
		DataEntity entity = null;
		do {
			BaseLine line = reader.getNextLine();
			if (line == null) {
				break;
			}
			if (line.getClass().getName().equals(AttributeLine.class.getName())) {
				AttributeLine attributeLine = (AttributeLine) line;
				if ("basePackage".equals(attributeLine.getName())) {
					basePackage = attributeLine.getValue();
				}
				if ("module".equals(attributeLine.getName())) {
					module = attributeLine.getValue();
				}
				if ("abstractEntity".equals(attributeLine.getName())) {
					abstractEntity = attributeLine.getValue();
				}
				if ("exceptionEntity".equals(attributeLine.getName())) {
					exceptionEntity = attributeLine.getValue();
				}
				if ("entity".equals(attributeLine.getName())) {
					entity = new DataEntity(attributeLine.getValue());
					entity.setBasePackage(basePackage).setAbstractEntity(abstractEntity).setModule(module)
							.setExceptionEntity(exceptionEntity);
					entities.add(entity);
				}
			} else {
				if (entity != null) {
					// 空白行跳过
					if (line.getClass().getName().equals(EmptyLine.class.getName())) {
						continue;
					}
					entity.setDataLine((DataLine) line);
				}
			}
		} while (true);
		// 写文件
		for (DataEntity e : entities) {
			// System.out.println(new String(e.getJavaRepoFileContent()));
			fileWriterFactory.write(e);
		}
	}
}
