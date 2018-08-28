package com.example.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.example.builder.line.AttributeLine;
import com.example.builder.line.BaseLine;
import com.example.builder.line.DataLine;
import com.example.builder.line.EmptyLine;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年2月13日 下午4:02:12 类说明
 */

public class ExcelReader {

	private FileInputStream input;
	private Workbook wb;
	private Sheet sheet;
	private int rowNumber;
	private int index = 0;

	ExcelReader(String filepath) {
		try {
			input = new FileInputStream(new File(filepath));
			wb = new HSSFWorkbook(input);
			sheet = wb.getSheetAt(0);
			rowNumber = sheet.getLastRowNum();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ExcelReader read(String filepath) throws IOException {
		return new ExcelReader(filepath);
	}

	public BaseLine getNextLine() {
		if (sheet == null) {
			return null;
		}
		if (index > rowNumber) {
			try {
				wb.close();
				input.close();
			} catch (IOException e) {
			}
			return null;
		}

		Row row = sheet.getRow(index);
		index++;
		if (row == null) {
			return new EmptyLine();
		}
		short length = row.getLastCellNum();
		if (length >= 3) {
			if (row.getCell(0) == null || row.getCell(1) == null) {
				return new EmptyLine();
			}
			String name = row.getCell(0).getStringCellValue();
			String type = row.getCell(1).getStringCellValue();
			String comment = null;
			if (length > 4) {
				if (row.getCell(4) != null)
					comment = row.getCell(4).getStringCellValue();
			}
			int len = 0;
			boolean isPrimary = false;
			boolean isNeedKey = false;
			boolean isConditionKey = false;
			try {
				len = (int) row.getCell(2).getNumericCellValue();
			} catch (Exception e) {
				e.printStackTrace();
				System.err
						.println("error in row : name = " + name + "; type = " + type + "; row index = " + (index - 1));
			}
			if (length >= 4) {
				if (row.getCell(3) != null) {
					String condition = row.getCell(3).getStringCellValue();
					if (condition.indexOf("primary") >= 0) {
						isPrimary = true;
					}
					if (condition.indexOf("condition") >= 0) {
						isConditionKey = true;
					}
					if (condition.indexOf("need") >= 0) {
						isNeedKey = true;
					}
				}
			}
			return DataLine.initial(name, type, len, comment, isPrimary, isNeedKey, isConditionKey);
		}
		if (length == 2) {
			String name = row.getCell(0).getStringCellValue();
			String type = row.getCell(1).getStringCellValue();
			return new AttributeLine(name, type);
		}
		return new EmptyLine();
	}

	// 考虑在处理excel的时候，有一些列尾部的cell是空白格，所以通过这个方法处理一下
	private int queryCellLength(Row row) {
		short length = row.getLastCellNum();
		short index = (short) (length - 1);
		for (index = length; index >= 0; index--) {
			if (row.getCell(index) == null) {
				continue;
			}
			try {
				if ("".equals(row.getCell(index).getStringCellValue())) {
					continue;
				} else {
					return index;
				}
			} catch (Exception e) {
				return index;
			}
		}
		return 0;
	}
}
