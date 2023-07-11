package org.biobrief.services;

import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;
import org.biobrief.util.StringHelper;
import org.springframework.stereotype.Service;

@Service
public class FileServiceWindowsImpl extends AbstractFileService
{	
	protected String windowsDir;
	
	public FileServiceWindowsImpl(String linuxDir, String windowsDir)
	{
		super(linuxDir);
		if (!StringHelper.hasContent(windowsDir))
			throw new CException("windowsDir is not set");
		this.windowsDir=windowsDir;
	}
	
	@Override
	public String convertPath(String filename)
	{
		//System.out.println("FileServiceWindowsImpl.convertPath: filename="+filename);
		return FileHelper.convertPath(filename, outDir, windowsDir);
	}
	
	@Override
	public String unconvertPath(String filename)
	{
		//System.out.println("FileServiceWindowsImpl.convertPath: filename="+filename);
		return FileHelper.unconvertPath(filename, outDir, windowsDir);
	}
}
