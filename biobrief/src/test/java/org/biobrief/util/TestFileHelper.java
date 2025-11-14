package org.biobrief.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.biobrief.util.FileHelper.FileInfo;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Multimap;

//gradle --stacktrace --info test --tests *TestFileHelper
public class TestFileHelper
{    
	// Directories to skip (by name, case-insensitive)
    private static final Set<String> SKIP_DIRECTORIES = Set.of("#recycle", "node_modules", "build", ".git");

    // File extensions to skip (lowercase, without dot)
    private static final Set<String> SKIP_EXTENSIONS = Set.of("tmp", "log", "bak", "db");
    
    @Test
	public void listAllFilesRecursively3()
	{    	
		String dir="x:/";
    	Path startPath = Paths.get(dir);
        try (Stream<Path> walk = Files.walk(startPath))
        {
        	Date start=new Date();
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() 
            {
            	@Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
            	{
            		if (dir.getFileName()==null)
            			 return FileVisitResult.CONTINUE;
                    String name = dir.getFileName().toString().toLowerCase();
                    if (SKIP_DIRECTORIES.contains(name))
                    {
                        // Skip entire directory subtree
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    try
                    {
                        if (Files.isHidden(dir))
                        	return FileVisitResult.SKIP_SUBTREE;
                    }
                    catch (IOException ignored)
                    {}
                    return FileVisitResult.CONTINUE;
                }
            	
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                {
//                	 try {
//                         if (Files.isHidden(file)) return FileVisitResult.CONTINUE;
//                     } catch (IOException ignored) {}

                     String name = file.getFileName().toString().toLowerCase();
                     int dot = name.lastIndexOf('.');
                     if (dot != -1) {
                         String ext = name.substring(dot + 1);
                         if (SKIP_EXTENSIONS.contains(ext)) return FileVisitResult.CONTINUE;
                     }
                    //System.out.printf("%s%n", file.toAbsolutePath());
                    System.out.printf("%s | Size: %d bytes | Modified: %s%n",
                            file.toAbsolutePath(),
                            attrs.size(),
                            attrs.lastModifiedTime());
                    return FileVisitResult.CONTINUE;
                }
	        });
            Date end=new Date();
            long elapsed=end.getTime()-start.getTime();
            System.out.println("tree list: elapsed="+elapsed);
        }
        catch (IOException e) {
            System.err.println("Error listing files: " + e.getMessage());
        }
	}
	
	//@Test
	public void listAllFilesRecursively2()
	{
		String dir="x:/";
    	Path startPath = Paths.get(dir);
        try (Stream<Path> walk = Files.walk(startPath))
        {
        	Date start=new Date();
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() 
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                {
                    System.out.printf("%s | Size: %d bytes | Modified: %s%n",
                            file.toAbsolutePath(),
                            attrs.size(),
                            attrs.lastModifiedTime());
                    return FileVisitResult.CONTINUE;
                }
	        });
            Date end=new Date();
            long elapsed=end.getTime()-start.getTime();
            System.out.println("tree list: elapsed="+elapsed);
        }
        catch (IOException e) {
            System.err.println("Error listing files: " + e.getMessage());
        }
	}
	
	//@Test
	public void listAllFilesRecursively()
	{
		Date start=new Date();
		String dir="x:/";
		Path startPath = Paths.get(dir);
        try (Stream<Path> walk = Files.walk(startPath))
        {
            List<Path> files=walk.filter(Files::isRegularFile) // Filter to include only regular files, exclude directories
                    .collect(Collectors.toList());
            
            System.out.println("Files found in " + dir + " and its subfolders:");
            for (Path file : files)
            {
                System.out.println(file.toAbsolutePath());
            }
            Date end=new Date();
            long elapsed=end.getTime()-start.getTime();
            System.out.println("tree list: elapsed="+elapsed);
        }
        catch (IOException e) {
            System.err.println("Error listing files: " + e.getMessage());
        }
	}
	
	//@Test
	public void getBaseDirectory()
	{
		System.out.println("current dir="+FileHelper.getCurrentDirectory());
		System.out.println("base dir="+FileHelper.getBaseDirectory());
		//assertThat(FileHelper.getBaseDirectory()).endsWith("/workspace/biobrief");
	}
	
	//@Test
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
	
	//@Test
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