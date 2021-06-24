package org.biobrief.util;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;

import org.biobrief.web.LoginHelper;
import org.biobrief.web.WebHelper;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class VirtualFileSystem
{	
	protected IFolder root;
	@JsonIgnore protected List<String> skipDirs=Lists.newArrayList();
	@JsonIgnore protected List<String> skipPrefixes=Lists.newArrayList();
	@JsonIgnore protected List<String> skipSuffixes=Lists.newArrayList();
	
	public VirtualFileSystem(String dir)
	{
		this.root=new VirtualFileSystem.VirtualFolder(dir, "root");
	}
	
	public VirtualFileSystem.IFolder read(String path)
	{
		log("reading path: "+path);
		VirtualFileSystem.IFolder folder=findDir(path);
		return folder;
	}
	
	public void move(String from, String to)
	{
		String filename=getRealPath(from);
		String newfilename=getRealPath(to);
		log("moving file from "+filename+" to "+newfilename);
		//FileHelper.moveFile(filename, newfilename);
	}
	
	public List<IFile> search(String path, String searchString, Boolean caseSensitive)
	{
		log("searching path="+path+" searchString="+searchString+" caseSensitive="+caseSensitive);
		List<IFile> list=Lists.newArrayList();
		if (!caseSensitive)
			searchString=searchString.toLowerCase();
		IFolder folder=findDir(FileHelper.getDirFromFilename(path));
		for (INode node : folder.getNodes())
		{
			if (!node.isFile())
				continue;
			IFile file=(IFile)node;
			String name=file.getName();
			if (!caseSensitive)
				name=name.toLowerCase();
			if (name.contains(searchString))
				list.add(file);
		}
		return list;
	}
	
	public void createDirectory(String path, String name)
	{
		IFolder folder=findDir(path);
		String dir=folder.getPath()+name;
		log("creating directory: path="+path+" name="+name+" dir="+dir);
		//FileHelper.createDirectory(name);
	}
	
	public void copy(String from, String to)
	{
		String filename=getRealPath(from);
		String newfilename=getRealPath(to);
		log("copy file from "+filename+" to "+newfilename);
		//FileHelper.copyFile(filename, newfilename);
	}
	
	public void rename(String path, String oldname, String newname)
	{
		String dir=getRealPath(path);
		String oldfilename=dir+oldname;
		String newfilename=dir+newname;
		log("renaming file from "+oldfilename+" to "+newfilename);
		//FileHelper.moveFile(filename, newfilename);
	}
	
	public List<IFile> details(String dir, List<String> names)
	{
		log("details file: dir="+dir+" names="+StringHelper.join(names));
		List<IFile> list=Lists.newArrayList();
		IFolder folder=findDir(dir);
		for (INode node : folder.getNodes())
		{
			if (!node.isFile())
				continue;
			IFile file=(IFile)node;
			for (String name : names)
			{
				if (file.getName().equals(name))
					list.add(file);
			}
		}
		return list;
	}
	
	public void delete(List<String> paths)
	{
		for (String path : paths)
		{
			delete(path);
		}
	}
	
	public void delete(String path)
	{
		String filename=getRealPath(path);
		log("deleting file: "+filename);
		//FileHelper.deleteFile(filename);
	}
	
	public void upload(String path, MultipartFile file)
	{
		String dir=findDir(path).getPath();
		dir="c:/temp/upload";// todo hack!	
		log("uploading file: "+dir+"/"+file.getOriginalFilename());
		WebHelper.writeFile(dir, file);
	}
	
	public String download(String path, List<String> names)
	{	
		try
		{
			log("downloading: path="+path+" names="+StringHelper.join(names));
			if (names.isEmpty())
				throw new CException("no file found in download list");
			if (names.size()>1)
				throw new CException("not yet supported. please download one file at a time");
			String filename=getRealPath(path+"/"+names.get(0));
			FileHelper.checkExists(filename);
			return filename;
		}
		catch(Exception e)
		{
			throw new CException("failed to download file: path="+path+" names="+StringHelper.join(names));
		}
	}
	
	public BufferedImage getImage(String path)
	{
		log("getImage: path="+path);
		String filename=getRealPath(path);
		return ImageHelper.readImage(filename);
	}
	
	////////////////////////////////////////
	
	private String getRealPath(String path)
	{
		IFolder folder=findDir(FileHelper.getDirFromFilename(path));
		String filename=folder.getPath()+"/"+FileHelper.stripPath(path);
		//System.out.println("path="+path+" real path="+filename);
		return filename;
	}
	
	// "/" or "/schedule/meeting-2021-06-08/A01234567/"
	private IFolder findDir(String path)
	{
		//System.out.println("findDir path="+path);
		if (path.equals("/"))
			return root;
		path=cleanPath(path);
		//System.out.println("findDir path="+path+" simplified path="+path);
		List<String> arr=StringHelper.split(path, "/");
		return root.findDir(arr);
	}
	
	private String cleanPath(String path)
	{
		if (!path.startsWith("/"))
			throw new CException("path should start with /: "+path);
		if (!path.endsWith("/"))
			throw new CException("path should end with /: "+path);
		return path.substring(1, path.length()-1);
	}
	
	public void skipDir(String dir)
	{
		skipDirs.add(dir);
	}
	
	public void skipPrefix(String suffix)
	{
		skipSuffixes.add(suffix);
	}
	
	public void skipSuffix(String suffix)
	{
		skipSuffixes.add(suffix);
	}
	
	private boolean isDirSkipped(String path)
	{
//		if (!isAfter(path))
//			return true;
		String dir=FileHelper.stripPath(path);
		for (String dirname : skipDirs)
		{
			if (dir.equals(dirname))
				return true;
		}
		for (String prefix : skipPrefixes)
		{
			if (dir.startsWith(prefix))
				return true;
		}
		return false;
	}
	
	private boolean isFileSkipped(String path)
	{
//		if (!isAfter(path))
//			return true;
		String filename=FileHelper.stripPath(path);
		for (String prefix : skipPrefixes)
		{
			if (filename.startsWith(prefix))
				return true;
		}
		for (String suffix : skipSuffixes)
		{
			if (filename.endsWith(suffix))
				return true;
		}
		return false;
	}
	
	public static void log(String message)
	{
		String logfile="filemanager.txt";
		String username=LoginHelper.getUsername().orElse("none");
		String line=username+"\t"+message;
		System.out.println(line);
		LogUtil.logMessage(logfile, line);
	}
	
	////////////////////////////////
	
	public interface INode
	{
		String getName();
		String getPath();
		Long getLength();
		Date getCreatedDate();
		Date getLastModified();
		boolean isFile();
		boolean isDirectory();
	}
	
	public interface IFolder extends INode
	{
		void add(INode node);
		VirtualFolder addVirtualFolder(String path, String name);
		Folder addFolder(String path);
		IFile add(IFile file);
		IFolder findDir(List<String> arr);
		List<INode> getNodes();
	}
	
	public interface IFile extends INode
	{
		
	}
	
	///////////////////////////////////////////
	
	@Data
	public abstract class Node implements INode
	{
		protected String path;
		protected String name;
		protected Long length;
		protected Date createdDate;
		protected Date lastModified;
		
		public Node(java.io.File file)
		{
			this.path=file.getPath();
			this.name=FileHelper.stripPath(this.path);
			this.length=file.length();
			this.createdDate=FileHelper.getCreatedDate(file);
			this.lastModified=new Date(file.lastModified());
		}
		
		public Node(String path)
		{
			this(new java.io.File(path));
		}

		public abstract boolean isFile();
		
		public abstract boolean isDirectory();
	}
	
	///////////////////////////////////////////
	
	@Data @EqualsAndHashCode(callSuper=true)
	public abstract class AbstractFolder extends Node implements IFolder
	{
		protected List<INode> nodes=Lists.newArrayList();
		
		public AbstractFolder(String path)
		{
			super(path);
		}
	
		public void add(INode node)
		{
			this.nodes.add(node);
		}
		
		public IFolder add(IFolder folder)
		{
			this.nodes.add(folder);
			return folder;
		}
		
		public IFile add(IFile file)
		{
			this.nodes.add(file);
			return file;
		}
		
		@Override
		public VirtualFolder addVirtualFolder(String path, String name)
		{
			VirtualFolder folder=new VirtualFolder(path, name);
			add(folder);
			return folder;
		}
		
		@Override
		public Folder addFolder(String path)
		{
			Folder folder=new Folder(path);
			add(folder);
			return folder;
		}
		
		@Override
		public IFolder findDir(List<String> arr)
		{
			String subdir=arr.get(0);
			for (INode node : nodes)
			{
				if (!node.isDirectory())
					continue;
				if (node.getName().equals(subdir))
				{
					IFolder folder=(IFolder)node;
					if (arr.size()==1)
						return folder;
					return folder.findDir(StringHelper.subList(arr, 1));
				}
			}
			throw new CException("cannot find subfolder with name: "+subdir);
		}
		
		@Override public boolean isFile() {return false;}
		
		@Override public boolean isDirectory() {return true;}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public class Folder extends AbstractFolder implements IFolder
	{
		public Folder(String path)
		{
			super(path);//, FileHelper.stripPath(path));
			//System.out.println("Folder: path="+path);
			for (String dir : FileHelper.listDirectories(path, true))
			{
				if (!isDirSkipped(dir))
					add(new Folder(dir));
			}			
			for (String filename : FileHelper.listFiles(path, true))
			{
				if (!isFileSkipped(filename))
					add(new File(filename));
			}
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public class VirtualFolder extends AbstractFolder implements IFolder
	{
		public VirtualFolder(String path, String name)
		{
			super(path);
			this.name=name;
			//System.out.println("VirtualFolder: path="+path+" name="+name);
		}
	}
	
	@Data @EqualsAndHashCode(callSuper=true)
	public class File extends Node implements IFile
	{
		protected String suffix;
		
		public File(String filename)
		{
			super(filename);
			//System.out.println("File: filename="+filename);
			this.suffix=FileHelper.getSuffix(filename);
		}
		
		@Override public boolean isFile() {return true;}
		
		@Override public boolean isDirectory() {return false;}
	}
}