package org.biobrief.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.apache.commons.compress.utils.Lists;
import org.springframework.core.io.ByteArrayResource;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

public class SyncFusionHelper
{
	@Data
	public static class FileManager
	{
		protected VirtualFileSystem vfs;
		
		public FileManager(VirtualFileSystem vfs)
		{
			this.vfs=vfs;
		}
		
		///////////////////////////////////
		
		public ActionResponse action(ActionRequest request)
		{
			if (request.getAction().equals("read"))
				return read(request);
			else if (request.getAction().equals("details"))
				return details(request);
			else throw new UnhandledCaseException(request.getAction());
		}
		
		/////////////////////////////////
		
		public ReadResponse read(ActionRequest request)
		{
			//System.out.println("read request="+JsonHelper.toJson(request));
			VirtualFileSystem.IFolder folder=vfs.findDir(request.getPath());
			ReadResponse response=new ReadResponse(request, folder.getPath());
			for (VirtualFileSystem.INode item : folder.getNodes())
			{	
				response.add(item);
			}
			return response;
		}
		
		public CreateResponse create(ActionRequest request)
		{
			CreateResponse response=new CreateResponse(request);
			return response;
		}
		
		public DeleteResponse delete(ActionRequest request)
		{
			DeleteResponse response=new DeleteResponse(request);
			return response;
		}
		
		public RenameResponse rename(ActionRequest request)
		{
			RenameResponse response=new RenameResponse(request);
			return response;
		}
		
		public SearchResponse search(ActionRequest request)
		{
			SearchResponse response=new SearchResponse(request);
			return response;
		}
		
		public DetailsResponse details(ActionRequest request)
		{
			DetailsResponse response=new DetailsResponse(request);
			return response;
		}
		
		public CopyResponse copy(ActionRequest request)
		{
			CopyResponse response=new CopyResponse(request);
			return response;
		}
		
		public MoveResponse move(ActionRequest request)
		{
			MoveResponse response=new MoveResponse(request);
			return response;
		}
		
		public UploadResponse upload(UploadRequest request)
		{
			UploadResponse response=new UploadResponse(request);
			return response;
		}
		
		//https://stackoverflow.com/questions/35680932/download-a-file-from-spring-boot-rest-service
		public DownloadResponse download(DownloadRequest request)
		{
			try
			{
				//System.out.println("download request: "+JsonHelper.toJson(request));
				if (request.getNames().isEmpty())
					throw new CException("no file found in download list");
				if (request.getNames().size()>1)
					throw new CException("not yet supported. please download one file at a time");
				String filename=vfs.getRealPath(request.getPath()+"/"+request.getNames().get(0));
				DownloadResponse response=new DownloadResponse(request, filename);
				return response;
			}
			catch(Exception e)
			{
				throw new CException("failed to download file: "+JsonHelper.toJson(request));
			}
		}
		
		public GetImageResponse getImage(GetImageRequest request)
		{
			String filename=vfs.getRealPath(request.getPath());
			BufferedImage image=ImageHelper.readImage(filename);
			GetImageResponse response=new GetImageResponse(request, image);
			return response;
		}
		
		//////////////////////////////////////////////////////
		
		@Data
		private static abstract class AbstractRequest
		{
			
		}
		
		@Data
		private static abstract class AbstractResponse
		{
			public AbstractResponse(AbstractRequest request)
			{
				//System.out.println("request="+JsonHelper.toJson(request));
			}
		}
		
		//////////////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class ActionRequest extends AbstractRequest
		{
			private String action;
			private String path;
			private Boolean showHiddenItems;
			private List<FileManagerDirectoryContent> data;
		}

		@Data @EqualsAndHashCode(callSuper=true)
		public static class ActionResponse extends AbstractResponse
		{
			public ActionResponse(ActionRequest request)
			{
				super(request);
			}
		}
		
		//////////////////////////////////////////////////////
		
//		@Data @EqualsAndHashCode(callSuper=true)
//		public static class ReadRequest extends AbstractRequest
//		{
//			private String path;
//			private Boolean showHiddenItems=false; // false
//			private List<FileManagerDirectoryContent> data;
//			
//			public ReadRequest()
//			{
//				super("read");
//			}
//			
//			//public String getPath(){return this.path;}
//			//public void setPath(final String path){this.path=path;}
//		}
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class ReadResponse extends ActionResponse
		{
			private FileManagerDirectoryContent cwd=new FileManagerDirectoryContent();
			private List<FileManagerDirectoryContent> files=Lists.newArrayList();
			private ErrorDetails error;
			private Details details;
			@JsonIgnore private String filterPath;
			
			public ReadResponse(ActionRequest request, String dir)
			{
				super(request);
//				System.out.println("dir="+dir);
				this.cwd=new FileManagerDirectoryContent(dir);
				this.filterPath=getFilterPath(request);
				this.cwd.filterPath=this.filterPath;
				//System.out.println("dir="+dir+" filterPath="+filterPath);
			}
			
			public FileManagerDirectoryContent add(VirtualFileSystem.INode item)
			{
				return add(new FileManagerDirectoryContent(item));
			}
			
			public FileManagerDirectoryContent addFile(String filename)
			{
				return add(new File(filename));
			}
			
			public FileManagerDirectoryContent addDir(String dir)
			{
				return add(new File(dir));
			}
			
			public FileManagerDirectoryContent add(File file)
			{
				return add(new FileManagerDirectoryContent(file));
			}
			
			public FileManagerDirectoryContent add(FileManagerDirectoryContent file)
			{
				this.files.add(file);
				file.setFilterPath(filterPath);
				return file;
			}
			
			private String getFilterPath(ActionRequest request)
			{
				String path=request.getPath();
				return StringHelper.replace(path, "/", "\\");
			}
		}
		
		///////////////////////////////////////////////////
//		@Data @EqualsAndHashCode(callSuper=true)
//		public static class CreateRequest extends AbstractRequest
//		{
//			private String action;
//			private String path;
//			private String name; 
//			private List<FileManagerDirectoryContent> data=Lists.newArrayList();
//			
//			public CreateRequest()
//			{
//				super("create");
//			}
//		}
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class CreateResponse extends ActionResponse
		{
			private List<FileManagerDirectoryContent> files;
			private ErrorDetails error;
			
			public CreateResponse(ActionRequest request)
			{
				super(request);
			}
		}
		
		////////////////////////////////////////////
		
//		@Data @EqualsAndHashCode(callSuper=true)
//		public static class DeleteRequest extends AbstractRequest
//		{
//			private String action;
//			private String path;
//			private String name; 
//			private List<FileManagerDirectoryContent> data=Lists.newArrayList();
//			
//			public DeleteRequest()
//			{
//				super("delete");
//			}
//		}
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class DeleteResponse extends ActionResponse
		{
			private List<FileManagerDirectoryContent> files;
			private ErrorDetails error;
			
			public DeleteResponse(ActionRequest request)
			{
				super(request);
			}
		}
		
		////////////////////////////////////////////
		
//		@Data @EqualsAndHashCode(callSuper=true)
//		public static class RenameRequest extends AbstractRequest
//		{
//			private String action;
//			private String path;
//			private String name;
//			private String newname; 
//			private List<FileManagerDirectoryContent> data=Lists.newArrayList();
//			
//			public RenameRequest()
//			{
//				super("rename");
//			}
//		}
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class RenameResponse extends ActionResponse
		{
			private List<FileManagerDirectoryContent> data=Lists.newArrayList();
			private ErrorDetails error;
			
			public RenameResponse(ActionRequest request)
			{
				super(request);
			}
		}
		
		///////////////////////////////////////////////////
		
//		@Data @EqualsAndHashCode(callSuper=true)
//		public static class SearchRequest extends AbstractRequest
//		{
//			private String action;
//			private String path;
//			private List<String> names=Lists.newArrayList();
//			private List<FileManagerDirectoryContent> data=Lists.newArrayList();
//			
//			public SearchRequest()
//			{
//				super("search");
//			}
//		}
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class SearchResponse extends ActionResponse
		{
			private List<FileManagerDirectoryContent> files;
			private ErrorDetails error;
			
			public SearchResponse(ActionRequest request)
			{
				super(request);
			}
		}
		
		///////////////////////////////////////////////////
		
//		@Data @EqualsAndHashCode(callSuper=true)
//		public static class DetailsRequest extends AbstractRequest
//		{
//			private String action;
//			private String path;
//			private List<String> names=Lists.newArrayList();
//			private List<FileManagerDirectoryContent> data=Lists.newArrayList();
//			
//			public DetailsRequest()
//			{
//				super("details");
//			}
//		}
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class DetailsResponse extends ActionResponse
		{
			private List<FileManagerDirectoryContent> files;
			private ErrorDetails error;
			
			public DetailsResponse(ActionRequest request)
			{
				super(request);
			}
		}
		
		///////////////////////////////////////////////////
		
//		@Data @EqualsAndHashCode(callSuper=true)
//		public static class CopyRequest extends AbstractRequest
//		{
//			public CopyRequest()
//			{
//				super("copy");
//			}
//		}
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class CopyResponse extends ActionResponse
		{
			public CopyResponse(ActionRequest request)
			{
				super(request);
			}
		}
		
		///////////////////////////////////////////////////
		
//		@Data @EqualsAndHashCode(callSuper=true)
//		public static class MoveRequest extends AbstractRequest
//		{
//			public MoveRequest()
//			{
//				super("move");
//			}
//		}
//		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class MoveResponse extends ActionResponse
		{
			public MoveResponse(ActionRequest request)
			{
				super(request);
			}
		}
		
		///////////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class UploadRequest extends AbstractRequest
		{
			private String action;
		}
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class UploadResponse extends AbstractResponse
		{
			public UploadResponse(UploadRequest request)
			{
				super(request);
			}
		}
		
		///////////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class DownloadRequest extends AbstractRequest
		{
			protected String action;
			protected String path;
			protected List<String> names;
			protected List<FileManagerDirectoryContent> data;
		}
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class DownloadResponse extends AbstractResponse
		{
			protected String filename;
			
			public DownloadResponse(DownloadRequest request, String filename)
			{
				super(request);
				this.filename=filename;
			}
		}
		
		///////////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class GetImageRequest extends AbstractRequest
		{
			protected String path;
			
			public GetImageRequest(String path)
			{
				this.path=path;
			}
		}
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class GetImageResponse extends AbstractResponse
		{
			protected BufferedImage image;
			
			public GetImageResponse(GetImageRequest request, BufferedImage image)
			{
				super(request);
				this.image=image;
			}
		}
		
		///////////////////////////////////////////////////
		@Data
		public static class FileManagerDirectoryContent
		{
			protected String name;
			protected Long size;
			protected String dateCreated;
			protected String dateModified;
			protected Boolean hasChild;
			protected Boolean isFile;
			protected String type; // ".xlsx"
			protected String filterPath;
			
			public FileManagerDirectoryContent() {}
			
			public FileManagerDirectoryContent(VirtualFileSystem.INode file)
			{
				//System.out.println("FileManagerDirectoryContent INode: "+file.getName());
				this.name=file.getName();
				this.size=file.getLength();
				this.dateCreated=formatDate(file.getCreatedDate());
				this.dateModified=formatDate(file.getLastModified());
				this.hasChild=file.isDirectory();
				this.isFile=file.isFile();
				if (file.isFile())
					this.type=FileHelper.getSuffix(file.getName());
				else this.type="";
				this.filterPath=null;// todo - what?
			}
			
			public FileManagerDirectoryContent(File file)
			{
				this.name=file.getName();
				this.size=file.length();
				this.dateCreated=formatDate(FileHelper.getCreatedDate(file));
				this.dateModified=formatDate(file.lastModified());
				this.hasChild=file.isDirectory();
				this.isFile=file.isFile();
				if (file.isFile())
					this.type=FileHelper.getSuffix(file.getName());
				else this.type="";
				this.filterPath=null;// todo - what?
			}
			
			public FileManagerDirectoryContent(String filename)
			{
				this(new File(filename));
			}
			
			private String formatDate(long timestamp)
			{
				return formatDate(new Date(timestamp));
			}
			
			private String formatDate(Date date)
			{
				return DateHelper.format(date, DateHelper.ISO_PATTERN);
			}
		}
		
		@Data
		public static class ErrorDetails
		{
			protected String code;//	String	-	Error code
			protected String message;//	String	-	Error message
			protected List<String> fileExists=Lists.newArrayList();//	-	List of duplicate file names
		}
		
		@Data
		public static class Details
		{
			protected String name;//	String	-	File name
			protected String dateDeleted;//	String	-	Date in which file was created (UTC Date string).
			protected String dateModified;//	String	-	Date in which file was last modified (UTC Date string).
			protected String filterPath;//	String	-	Relative path to the file or folder.
			protected Boolean hasChild;//	Boolean	-	Defines this folder has any child folder or not.
			protected Boolean isFile;//	Boolean	-	Say whether the item is file or folder.
			protected Integer size;//	Number	-	File size
			protected String type;//	String	-	File extension
			protected List<String> multipleFiles=Lists.newArrayList();
		}
	}
}