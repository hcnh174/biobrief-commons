package org.biobrief.services;

import java.awt.image.BufferedImage;
import java.io.File;

import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;
import org.biobrief.util.ImageHelper;
import org.biobrief.util.StringHelper;

public class AbstractFileService implements FileService
{
	protected String outDir;
	
	protected AbstractFileService(String outDir)
	{
		if (!StringHelper.hasContent(outDir))
			throw new CException("outDir is not set");
		this.outDir=outDir;
	}
	
	@Override
	public String getOutDir()
	{
		return outDir;
	}

	@Override
	public void createDirectory(String dir)
	{
		String path=convertPath(dir);
		//System.out.println("creating directory: "+dir);
		FileHelper.createDirectory(path);
	}

	@Override
	public String writeFile(String filename, String value)
	{
		String path=convertPath(filename);
		//System.out.println("writing file: "+path);
		return FileHelper.writeFile(path, value);
	}
	
	@Override
	public String writeImage(String filename, BufferedImage image)
	{
		String path=convertPath(filename);
		//System.out.println("writing file: "+path);
		ImageHelper.writeImage(image, path);
		return path;
	}

	@Override
	public String readFile(String filename)
	{
		String path=convertPath(filename);
		//System.out.println("reading file: "+path);
		return FileHelper.readFile(path);
	}

	@Override
	public boolean fileExists(String filename)
	{
		String path=convertPath(filename);
		//System.out.println("checking whether file exists: "+path);
		return FileHelper.exists(path);
	}

	@Override
	public void checkFileExists(String filename)
	{
		String path=convertPath(filename);
		//System.out.println("checking whether file exists: "+path);
		FileHelper.checkExists(path);
	}

	@Override
	public String convertPath(String filename)
	{
		//System.out.println("AbstractSlurmService.convertPath: filename="+filename);
		return filename;
	}
	
	@Override
	public String unconvertPath(String filename)
	{
		//System.out.println("AbstractSlurmService.convertPath: filename="+filename);
		return filename;
	}
	
	@Override
	public String getFileSize(String filename)
	{
		return FileHelper.getFileSize(convertPath(filename), FileHelper.FileSizeType.MEGABYTES);
	}
	
	@Override
	public File getFile(String filename)
	{
		return new File(convertPath(filename));
	}
}
