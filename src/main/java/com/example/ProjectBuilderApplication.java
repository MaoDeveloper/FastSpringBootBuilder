package com.example;

import com.example.builder.ProjectBuilder;

public class ProjectBuilderApplication {

	public static void main(String[] args) throws Exception {
		//String filePath = "/Users/joe/Documents/git/project-builder/src/main/resources/module.xls";
		//String exportPath = "/Users/joe/Downloads/pojo";
		String filePath = "G://A MASS//monitor.xls";
		String exportPath = "G:/ProjectBuilderExport//monitor2";
		new ProjectBuilder(filePath, exportPath).create();
	}
	
}
