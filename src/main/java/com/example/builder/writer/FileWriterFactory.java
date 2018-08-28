package com.example.builder.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.builder.entity.DataEntity;

/**
 * @author 作者 joe:
 * @version 创建时间：2018年2月14日 下午9:04:46 类说明
 */

public class FileWriterFactory {

	public static String EXPORT_PATH = "";

	boolean initPath = false;

	private List<FileWriter> writers = new ArrayList<>();

	private void initFileWriter() throws Exception {
		String packageName = getClass().getPackage().getName();
		File file = new File(
				Class.class.getClass().getResource("/").getPath() + "/" + packageName.replaceAll("\\.", "/"));
		System.out.println(file.getAbsolutePath());
		String[] list = file.list();
		for (String name : list) {
			name = name.replace(".class", "");
			Class<?> c = Class.forName(packageName + "." + name);
			if (FileWriter.class.isAssignableFrom(c) && !c.isInterface()) {
				writers.add((FileWriter) c.newInstance());
			}
		}
	}

	public FileWriterFactory(String path) throws Exception {
		initFileWriter();
		File file = new File(path);
		if (!initPath) {
			if (file.isDirectory()) {
				deleteAllFile(file);
			}
			initPath = true;
		}
		FileWriterFactory.EXPORT_PATH = path;
	}
	
	private void deleteAllFile(File file) {
		if(file.isDirectory()) {
			String[] subFileStrs = file.list();
			if(subFileStrs != null && subFileStrs.length > 0) {
				for(String subFileStr : subFileStrs) {
					deleteAllFile(new File(file.getPath() + File.separator + subFileStr));
				}
			}
		}
		file.delete();
	}

	public void write(DataEntity entity) {
		for (FileWriter writer : writers) {
			writer.writeFile(entity);
		}
	}

	public static void writeDataIntoFile(String filename, byte[] datas) {
		try {
			FileOutputStream output = new FileOutputStream(filename, true);
			output.write(datas);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
