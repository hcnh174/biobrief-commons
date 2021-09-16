package org.biobrief.services;

import org.springframework.stereotype.Service;

@Service
public class FileServiceLinuxImpl extends AbstractFileService
{
	public FileServiceLinuxImpl(String outDir)
	{
		super(outDir);
	}
}
