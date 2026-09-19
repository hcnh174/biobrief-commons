package org.biobrief.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Set;
import java.util.stream.Stream;

import org.biobrief.util.FileHelper.FileInfo;
import org.biobrief.util.FileHelper.FileInfoList;

public class FileTreeBuilder
{	
	// Directories to skip (by name, case-insensitive)
	private static final Set<String> SKIP_DIRECTORIES = Set.of("#recycle", "node_modules", "build", ".git", "@eaDir");

	// File extensions to skip (lowercase, without dot)
	private static final Set<String> SKIP_EXTENSIONS = Set.of("tmp", "log", "bak", "db");
	
	private final String dir;
	
	public FileTreeBuilder(String dir)
	{
		this.dir=dir;
	}
	
	public FileInfoList build(Context context)
	{
		FileInfoList items=new FileInfoList();
		Path startPath = Paths.get(dir);
		try (Stream<Path> walk = Files.walk(startPath))
		{
//			Date start=new Date();
			Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() 
			{
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
				{
					if (dir.getFileName()==null)
						 return FileVisitResult.CONTINUE;
					String name = dir.getFileName().toString().toLowerCase();
					if (SKIP_DIRECTORIES.contains(name)) // Skip entire directory subtree
						return FileVisitResult.SKIP_SUBTREE;
					try
					{
						if (Files.isHidden(dir))
							return FileVisitResult.SKIP_SUBTREE;
					}
					catch (IOException ignored) {}
					return FileVisitResult.CONTINUE;
				}
				
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				{
					 try {
						 if (Files.isHidden(file)) return FileVisitResult.CONTINUE;
					 } catch (IOException ignored) {}
					 String name = file.getFileName().toString().toLowerCase();
					 int dot = name.lastIndexOf('.');
					 if (dot != -1) {
						 String ext = name.substring(dot + 1);
						 if (SKIP_EXTENSIONS.contains(ext)) return FileVisitResult.CONTINUE;
					 }
					items.add(createFileInfo(file, attrs));
					return FileVisitResult.CONTINUE;
				}
			});
//			Date end=new Date();
//			long elapsed=end.getTime()-start.getTime();
//			System.out.println("tree list: elapsed="+elapsed);
		}
		catch (IOException e)
		{
			throw new CException("Error listing files: ", e);
		}
		return items;
	}
	
	private FileInfo createFileInfo(Path path, BasicFileAttributes attrs)
	{
		String filename=FileHelper.normalize(path.toAbsolutePath().toString());
		Long size=attrs.size();
		Date date=new Date(attrs.lastModifiedTime().toMillis());
		FileInfo.Type type=Files.isDirectory(path) ? FileInfo.Type.Folder : FileInfo.Type.File;
		return new FileInfo(filename, size.toString(), date, type);
	}
}