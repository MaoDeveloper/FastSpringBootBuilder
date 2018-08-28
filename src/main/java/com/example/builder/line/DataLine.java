package com.example.builder.line;

import com.example.builder.entity.DataEntity;
import com.example.tools.CamelHumpUnderLineSwitcher;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年2月13日 下午3:50:04 类说明
 */

public abstract class DataLine implements BaseLine {

	protected String name;
	protected String type;
	protected int length;
	protected String comment;
	protected boolean primary = false;
	protected boolean needKey = false;// 查询条件，通过这个参数进行数据隔离
	protected boolean conditionKey = false;// 搜索时用于模糊搜索

	void init(String name, String type, int length, String comment, boolean isPrimary, boolean isNeedKey,
			boolean isConditionKey) {
		this.name = name;
		this.type = type;
		this.length = length;
		this.primary = isPrimary;
		this.needKey = isNeedKey;
		this.conditionKey = isConditionKey;
		this.comment = comment;
	}

	public String getName() {
		return name;
	}

	public String getQuota() {
		return getCamelHumpName();
	}

	public String getCamelHumpName() {
		return CamelHumpUnderLineSwitcher.toCamelHump(name);
	}

	public static DataLine initial(String name, String type, int length, String comment, boolean isPrimary,
			boolean isNeedKey, boolean isConditionKey) {
		DataLine dataLine;
		type = type.toLowerCase();
		if (type.equals("int") || type.equals("integer")) {
			type = "Integer";
			dataLine = new IntegerDataLine();
		} else if (type.equals("long")) {
			type = "Long";
			dataLine = new LongDataLine();
		} else if (type.equals("short") || type.equals("tinyint")) {
			type = "Integer";
			dataLine = new ShortDataLine();
		} else if (type.equals("date") || type.equals("datetime")) {
			type = "Date";
			dataLine = new DatetimeLine();
		} else if (type.equals("double")) {
			type = "Double";
			dataLine = new DoubleDataLine();
		} else if (type.equals("char")) {
			type = "String";
			dataLine = new CharDataLine();
		} else if (type.equals("text")) {
			type = "String";
			dataLine = new TextDataLine();
		} else {
			type = "String";
			dataLine = new StringDataLine();
		}
		dataLine.init(name, type, length, comment, isPrimary, isNeedKey, isConditionKey);
		return dataLine;
	}

	public String getType() {
		return type;
	}

	public int getLength() {
		return length;
	}

	public boolean isPrimary() {
		return primary;
	}

	public boolean isNeedKey() {
		return needKey;
	}

	public boolean isConditionKey() {
		return conditionKey;
	}

	public String getPojoAttriture() {
		return "    private " + type + " " + CamelHumpUnderLineSwitcher.toCamelHump(this.name) + ";\n";
	}

	/**
	 * 类型加字段，如：String name
	 * 
	 * @return
	 */
	public String getJavaDefine() {
		return type + " " + CamelHumpUnderLineSwitcher.toCamelHump(this.name);
	}

	public String getAttributeKeyDefine() {
		return "    public final static String KEY_" + CamelHumpUnderLineSwitcher.toUnderLine(this.name).toUpperCase()
				+ " = \"" + CamelHumpUnderLineSwitcher.toCamelHump(this.name) + "\";\n";
	}

	public static String getIdAttribute() {
		return "    private Long id;\n";
	}

	public static String getIdGetSet() {
		return DataLine.buildPojoGetSetLine("Long", "id");
	}

	public static String getStatusAttribute() {
		return "    private Integer status;\n";
	}

	public static String getStatusGetSet() {
		return DataLine.buildPojoGetSetLine("Integer", "status");
	}

	public static String getCreateAtAttribute() {
		return "    private Date createAt;\n";
	}

	public static String getCreateAtGetSet() {
		return DataLine.buildPojoGetSetLine("Date", "createAt");
	}

	public static String getUpdateAtAttribute() {
		return "    private Date updateAt;\n";
	}

	public static String getUpdateAtGetSet() {
		return DataLine.buildPojoGetSetLine("Date", "updateAt");
	}

	public String getPojoGetSetLine() {
		return DataLine.buildPojoGetSetLine(this.type, this.name);
	}

	public String getPojoGetLine() {
		return DataLine.buildPojoGetLine(this.type, this.name);
	}

	public String setMethod() {
		return CamelHumpUnderLineSwitcher.toCamelHump("set_" + getName());
	}

	public String getMethod() {
		String getMethod = CamelHumpUnderLineSwitcher.toCamelHump("get_" + name) + "()";
		return getMethod;
	}

	private static String buildPojoGetLine(String type, String name) {
		String key = CamelHumpUnderLineSwitcher.toCamelHump(name);
		String getMethod = CamelHumpUnderLineSwitcher.toCamelHump("get_" + name);
		String s = "    public " + type + " " + getMethod + "() {\n" + "        return " + key + ";\n" + "    }\n"
				+ "\n";
		return s;
	}

	private static String buildPojoGetSetLine(String type, String name) {
		String key = CamelHumpUnderLineSwitcher.toCamelHump(name);
		String getMethod = CamelHumpUnderLineSwitcher.toCamelHump("get_" + name);
		String setMethod = CamelHumpUnderLineSwitcher.toCamelHump("set_" + name);
		String s = "    public " + type + " " + getMethod + "() {\n" + "        return " + key + ";\n" + "    }\n"
				+ "\n" + "    public void " + setMethod + "(" + type + " " + key + ") {\n" + "        this." + key
				+ " = " + key + ";\n" + "    }\n\n";
		return s;
	}

	protected String getSqlComment() {
		if (this.comment != null && !this.comment.trim().equals("")) {
			return "COMMENT '" + this.comment + "'";
		}
		return "";
	}

	public abstract String getSqlLine();

	public abstract String fromJsonGetDataString(DataEntity entity);

	@Override
	public String toString() {
		return "DataLine [name=" + name + ", type=" + type + ", length=" + length + "]";
	}
}
