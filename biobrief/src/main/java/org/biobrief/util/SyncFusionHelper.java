package org.biobrief.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.compress.utils.Lists;
import org.biobrief.util.VirtualFileSystem.IFile;

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
				return read((IReadRequest)request);
			else if (request.getAction().equals("create"))
				return create((ICreateRequest)request);
			else if (request.getAction().equals("rename"))
				return rename((IRenameRequest)request);
			else if (request.getAction().equals("delete"))
				return delete((IDeleteRequest)request);
			else if (request.getAction().equals("details"))
				return details((IDetailsRequest)request);
			else if (request.getAction().equals("search"))
				return search((ISearchRequest)request);
			else if (request.getAction().equals("copy"))
				return copy((ICopyRequest)request);
			else if (request.getAction().equals("move"))
				return move((IMoveRequest)request);
			else throw new UnhandledCaseException(request.getAction());
		}
		
		/////////////////////////////////
		
		public ReadResponse read(IReadRequest request)
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
		
		public CreateResponse create(ICreateRequest request)
		{
			CreateResponse response=new CreateResponse(request);
			return response;
		}
		
		public DeleteResponse delete(IDeleteRequest request)
		{
			vfs.delete(request.getPathList());
			DeleteResponse response=new DeleteResponse(request);
			return response;
		}
		
		public RenameResponse rename(IRenameRequest request)
		{
			String oldfilename=request.getPath()+request.getName();
			String newfilename=request.getPath()+request.getNewname();
			vfs.rename(oldfilename, newfilename);
			RenameResponse response=new RenameResponse(request);
			return response;
		}
		
		public SearchResponse search(ISearchRequest request)
		{
			List<IFile> files=vfs.search(request.getPath(), request.getSearchString(), request.getCaseSensitive());
			SearchResponse response=new SearchResponse(request, files);
			return response;
		}
		
		public DetailsResponse details(IDetailsRequest request)
		{
			List<IFile> files=vfs.details(request.getPath(), request.getNames());
			DetailsResponse response=new DetailsResponse(request, files);
			return response;
		}
		
		public CopyResponse copy(ICopyRequest request)
		{
			for (int index=0; index<request.getNames().size(); index++)
			{
				String fromname=request.getNames().get(index);
				String toname=request.getRenameFiles().get(index);
				String from=request.getPath()+fromname;
				String to=request.getTargetPath()+toname;
				vfs.copy(from, to);
			}
			CopyResponse response=new CopyResponse(request);
			return response;
		}
		
		public MoveResponse move(IMoveRequest request)
		{
			for (int index=0; index<request.getNames().size(); index++)
			{
				String fromname=request.getNames().get(index);
				String toname=request.getRenameFiles().get(index);
				String from=request.getPath()+fromname;
				String to=request.getTargetPath()+toname;
				vfs.move(from, to);
			}
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
		
		public interface IRequest {}
		
		@Data
		private static abstract class AbstractRequest implements IRequest
		{
			
		}
		
		@Data
		private static abstract class AbstractResponse
		{
			public AbstractResponse(IRequest request)
			{
				//System.out.println("request="+JsonHelper.toJson(request));
			}
		}
		
		//////////////////////////////////////////////////////
		
		public interface IActionRequest extends IRequest
		{
			String getAction();
			String getPath();
			List<FileManagerDirectoryContent> getData();
		}
	
		public interface IReadRequest extends IActionRequest
		{
			Boolean getShowHiddenItems();
		}
		
		public interface ICreateRequest extends IActionRequest
		{
			String getName();
		}
		
		public interface IRenameRequest extends IActionRequest
		{
			String getName();
			String getNewname();
		}
		
		public interface IDeleteRequest extends IActionRequest
		{
			List<String> getNames();
			List<String> getPathList();
		}
		
		public interface IDetailsRequest extends IActionRequest
		{
			List<String> getNames();
		}
		
		public interface ISearchRequest extends IActionRequest
		{
			Boolean getShowHiddenItems();
			Boolean getCaseSensitive();
			String getSearchString();
		}
		
		public interface ICopyRequest extends IActionRequest
		{
			List<String> getNames();
			String getTargetPath();
			List<String> getRenameFiles();
		}

		public interface IMoveRequest extends IActionRequest
		{
			List<String> getNames();
			String getTargetPath();
			List<String> getRenameFiles();
		}
		
		/////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class ActionRequest extends AbstractRequest
			implements IActionRequest,
			IReadRequest, ICreateRequest, IDeleteRequest,
			IDetailsRequest, ISearchRequest, ICopyRequest, IMoveRequest
		{
			private String action;
			private String path;
			private List<String> names=Lists.newArrayList(); //used by: delete
			private Boolean showHiddenItems;
			private List<FileManagerDirectoryContent> data;
			private String name; //used by: rename
			private String newname; //used by: name
			private List<String> renameFiles=Lists.newArrayList(); //used by: copy, move
			private String searchString; //used by: search
			private String targetPath; //used by: copy, move
			private Boolean caseSensitive; // used by: search
			
			@JsonIgnore public List<String> getPathList()
			{
				List<String> list=Lists.newArrayList();
				for (String name : names)
				{
					list.add(path+name);
				}
				return list;
			}
		}

		@Data @EqualsAndHashCode(callSuper=true)
		public static class ActionResponse extends AbstractResponse
		{
			public ActionResponse(IActionRequest request)
			{
				super(request);
			}
		}
		
		//////////////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class ReadResponse extends ActionResponse
		{
			private FileManagerDirectoryContent cwd=new FileManagerDirectoryContent();
			private List<FileManagerDirectoryContent> files=Lists.newArrayList();
			private ErrorDetails error;
			private Details details;
			@JsonIgnore private String filterPath;
			
			public ReadResponse(IReadRequest request, String dir)
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
			
			private String getFilterPath(IReadRequest request)
			{
				String path=request.getPath();
				return StringHelper.replace(path, "/", "\\");
			}
		}
		
		///////////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class CreateResponse extends ActionResponse
		{
			private List<FileManagerDirectoryContent> files;
			private ErrorDetails error;
			
			public CreateResponse(ICreateRequest request)
			{
				super(request);
			}
		}
		
		////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class DeleteResponse extends ActionResponse
		{
			private List<FileManagerDirectoryContent> files;
			private ErrorDetails error;
			
			public DeleteResponse(IDeleteRequest request)
			{
				super(request);
			}
		}
		
		////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class RenameResponse extends ActionResponse
		{
			private List<FileManagerDirectoryContent> data=Lists.newArrayList();
			private ErrorDetails error;
			
			public RenameResponse(IRenameRequest request)
			{
				super(request);
			}
		}
		
		///////////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class SearchResponse extends ActionResponse
		{
			private List<FileManagerDirectoryContent> files;
			private ErrorDetails error;
			
			public SearchResponse(ISearchRequest request, List<IFile> files)
			{
				super(request);
				for (IFile file : files)
				{
					this.files.add(new FileManagerDirectoryContent(file));
				}
			}
		}
		
		///////////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class DetailsResponse extends ActionResponse
		{
			private List<FileManagerDirectoryContent> files;
			private ErrorDetails error;
			
			public DetailsResponse(IDetailsRequest request, List<IFile> files)
			{
				super(request);
				for (IFile file : files)
				{
					this.files.add(new FileManagerDirectoryContent(file));
				}
			}
		}
		
		///////////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class CopyResponse extends ActionResponse
		{
			public CopyResponse(ICopyRequest request)
			{
				super(request);
			}
		}
		
		///////////////////////////////////////////////////

		@Data @EqualsAndHashCode(callSuper=true)
		public static class MoveResponse extends ActionResponse
		{
			public MoveResponse(IMoveRequest request)
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