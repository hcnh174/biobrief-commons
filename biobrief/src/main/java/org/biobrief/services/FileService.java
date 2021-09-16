package org.biobrief.services;

import java.awt.image.BufferedImage;
import java.io.File;

import org.biobrief.util.RuntimeHelper;

public interface FileService
{	
	String getOutDir();
	void createDirectory(String dir);
	boolean fileExists(String filename);
	void checkFileExists(String filename);
	String writeFile(String filename, String value);
	String writeImage(String filename, BufferedImage image);
	String readFile(String filename);
	String getFileSize(String filename);
	String convertPath(String filename);
	File getFile(String filename);
	
	static FileService create(String linuxDir, String windowsDir)
	{
		if (RuntimeHelper.isWindows())
			return new FileServiceWindowsImpl(linuxDir, windowsDir);
		else return new FileServiceLinuxImpl(linuxDir);
	}
}
