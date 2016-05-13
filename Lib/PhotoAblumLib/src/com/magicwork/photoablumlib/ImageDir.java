package com.magicwork.photoablumlib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ImageDir {
	public enum Type {
		IMAGE, VEDIO, AUDIO
	}

	String dirName;
	String dirPath;
	List<String> files = new ArrayList<String>();// Ŀ¼���е��ļ�
	HashSet<String> selectedFiles = new HashSet<String>();// ��ѡ�е��ļ�
	//List<String> ids=new ArrayList<String>();
	
	String firstImagePath;
	Type type=Type.IMAGE;

	public ImageDir(String dirPath) {
		super();
		this.dirPath = dirPath;
	}

	public void addFile(String file) {
		files.add(file);
	}

	public String getDirName() {
		return dirName;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	/*public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}*/

	
}
