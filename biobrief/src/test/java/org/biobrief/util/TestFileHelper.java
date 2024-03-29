package org.biobrief.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.biobrief.util.FileHelper.FileInfo;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Multimap;

//gradle --stacktrace --info test --tests *TestFileHelper
public class TestFileHelper
{	
	@Test
	public void getBaseDirectory()
	{
		System.out.println("current dir="+FileHelper.getCurrentDirectory());
		System.out.println("base dir="+FileHelper.getBaseDirectory());
		//assertThat(FileHelper.getBaseDirectory()).endsWith("/workspace/biobrief");
	}
	
	@Test
	public void getWorkspaceDirectory()
	{
		String dir=FileHelper.getWorkspaceDirectory();
		System.out.println("workspace dir="+dir);
		assertThat(dir).isEqualTo("C:/workspace");
	}
	
	//@Test
	public void stripPath()
	{
		String filename="d:/workspace/biobrief/.temp/generated/mappings/patients.json";
		assertThat(FileHelper.stripPath(filename)).isEqualTo("patients.json");
	}
	
	//@Test
	public void downloadHttpsFile()
	{
		String filename="https://dcc.icgc.org/api/v1/download?fn=/current/Projects/BRCA-US/donor.BRCA-US.tsv.gz";
		//String filename="https://dcc.icgc.org/releases/current/Projects/BRCA-US/donor.BRCA-US.tsv.gz";
		String outdir="c:/temp";
		String outfile=outdir+"/donor.BRCA-US.tsv.gz";
		assertThat(FileHelper.downloadHttpsFile(filename, outdir)).isEqualTo(outfile);
	}
	
	//@Test
	public void listFilesRecursively()
	{
		String dir="D:/projects/expertpanel/エキスパートパネル関係/HU20190055";
		String suffix="*.xml";
		List<String> filenames=FileHelper.listFilesRecursively(dir, suffix);
		assertThat(filenames.size()==2);
		for (String filename : filenames)
		{
			System.out.println("filename="+filename);
		}
		//assertThat(filenames.get(0).equals(""));
	}
	
	//@Test
	public void unzip()
	{
		String filename="c:/temp/tmp/report_A000610333797.zip";
		String dir=FileHelper.unzip(filename, "A0006");
		assertThat(dir).isEqualTo("c:/temp/tmp/report_A000610333797");
		FileHelper.checkExists(dir);
	}
	
	//@Test
	public void getDirectoryMap()
	{
		String dir="x:";
		Multimap<String, FileInfo> map=FileHelper.getDirectoryMap(dir);
		System.out.println("map="+StringHelper.toString(map));	
	}
	
	@Test
	public void findFilesByWildcard()
	{
		String dir="X:\\B301006627782_NCC";
		//String pattern="PP_*.pptx";
		String pattern="RG_Report_*.xml";
		List<String> actual = FileHelper.findFilesByWildcard(dir, pattern);
		System.out.println("files: ["+StringHelper.join(actual, "|")+"]");		
	}
	
	/*
	//@Test
	public void findFilesRecursivelyByWildcard()
	{
		String dir="X:\\B301006627782_NCC";
		String pattern="RG_Report_*.xml";
		List<String> actual = FileHelper.findFilesRecursivelyByWildcard(dir, pattern);
		System.out.println("files: ["+StringHelper.join(actual, "\n")+"]");
		//assertEquals(new HashSet<>(Arrays.asList("six.txt", "three.txt", "two.docx", "one.txt")), 
		//  new HashSet<>(actual));
		
	}
	*/
}