package com.example.builder.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.builder.line.DataLine;
import com.example.builder.line.DatetimeLine;
import com.example.tools.CamelHumpUnderLineSwitcher;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年2月14日 下午6:11:22 类说明
 */

public class DataEntity {

	private String basePackage;
	private String module;
	private String abstractEntity;
	private String exceptionEntity;

	// 实体的名称
	private String entityName;

	public String getBasePackage() {
		return basePackage;
	}

	public String getModule() {
		return module;
	}

	public String getAbstractEntity() {
		return abstractEntity;
	}

	public String getExceptionEntity() {
		return exceptionEntity;
	}

	public String getEntityName() {
		return entityName;
	}

	public List<DataLine> getDataLines() {
		return dataLines;
	}

	private List<DataLine> dataLines = new ArrayList<>();

	public DataEntity(String entityName) {
		super();
		this.entityName = entityName;
	}

	public DataEntity setBasePackage(String basePackage) {
		this.basePackage = basePackage;
		return this;
	}

	public DataEntity setExceptionEntity(String exceptionEntity) {
		this.exceptionEntity = exceptionEntity;
		return this;
	}

	public DataEntity setModule(String module) {
		this.module = module;
		return this;
	}

	public DataEntity setAbstractEntity(String abstractEntity) {
		this.abstractEntity = abstractEntity;
		return this;
	}

	public void setDataLine(DataLine dataLine) {
		System.out.println(dataLine.toString());
		this.dataLines.add(dataLine);
	}

	public String getImportString() {
		String imports = "";
		imports += "import java.util.Date;\n";
		for (DataLine line : dataLines) {
			if (line.getClass().getName().equals(DatetimeLine.class.getName())) {
				// TODO ，其他类型，如果需要引入在这里判断
			}
		}
		return imports;
	}

	public boolean hasPrimaryKey() {
		for (DataLine line : dataLines) {
			if (line.isPrimary())
				return true;
		}
		return false;
	}

	public boolean hasDataKey() {
		for (DataLine line : dataLines) {
			if (DatetimeLine.class.isAssignableFrom(line.getClass()))
				return true;
		}
		return false;
	}

	public DataLine getPrimaryDataLine() {
		for (DataLine line : dataLines) {
			if (line.isPrimary())
				return line;
		}
		return null;
	}

	public List<DataLine> getAllNeedKeys() {
		List<DataLine> lines = new ArrayList<>();
		for (DataLine line : dataLines) {
			if (line.isNeedKey())
				lines.add(line);
		}
		return lines;
	}

	public List<DataLine> getAllConditionKeys() {
		List<DataLine> lines = new ArrayList<>();
		for (DataLine line : dataLines) {
			if (line.isConditionKey())
				lines.add(line);
		}
		return lines;
	}

	public String getTableName() {
		String tableName = "t_" + module.toLowerCase() + "_" + CamelHumpUnderLineSwitcher.toUnderLine(entityName);
		return tableName;
	}

	public String getPojoClassName() {
		String n = CamelHumpUnderLineSwitcher.toCamelHump("_" + this.entityName);
		return n;
	}

	public String getParamsName() {
		String n = CamelHumpUnderLineSwitcher.toCamelHump(this.entityName);
		return n;
	}

	public String getClassName() {
		return CamelHumpUnderLineSwitcher.toCamelHump("_" + this.getModule() + "_" + this.getEntityName());
	}
}
