package org.biobrief.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.List;

import org.biobrief.services.NotificationService;
import org.biobrief.util.VirtualFileSystem.IFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;

public class SyncFusionHelper
{
	public enum Action
	{
		read,
		create,
		rename,
		delete,
		details,
		search,
		copy,
		move,
		download,
		upload,
		getimage
	}
	
	@Data
	public static class FileManager
	{
		protected VirtualFileSystem vfs;
		protected NotificationService notificationService;
		
		public FileManager(VirtualFileSystem vfs, NotificationService notificationService)
		{
			this.vfs=vfs;
			this.notificationService=notificationService;
		}
		
		///////////////////////////////////
		
		public ActionResponse action(ActionRequest request, UserDetails user)
		{
			if (request.getAction()==Action.read)
				return read((IReadRequest)request, user);
			else if (request.getAction()==Action.create)
				return create((ICreateRequest)request, user);
			else if (request.getAction()==Action.rename)
				return rename((IRenameRequest)request, user);
			else if (request.getAction()==Action.delete)
				return delete((IDeleteRequest)request, user);
			else if (request.getAction()==Action.details)
				return details((IDetailsRequest)request, user);
			else if (request.getAction()==Action.search)
				return search((ISearchRequest)request, user);
			else if (request.getAction()==Action.copy)
				return copy((ICopyRequest)request, user);
			else if (request.getAction()==Action.move)
				return move((IMoveRequest)request, user);
			else throw new UnhandledCaseException(request.getAction());
		}
		
		/////////////////////////////////
		
		public ReadResponse read(IReadRequest request, UserDetails user)
		{
			//System.out.println("read request="+JsonHelper.toJson(request));
			log(request, user);
			VirtualFileSystem.IFolder folder=vfs.read(request.getPath());
			ReadResponse response=new ReadResponse(request, folder.getPath());
			for (VirtualFileSystem.INode item : folder.getNodes())
			{	
				response.add(item);
			}
			return response;
		}
		
		public CreateResponse create(ICreateRequest request, UserDetails user)
		{
			log(request, user);
			vfs.createDirectory(request.getPath(), request.getName());
			return new CreateResponse(request);
		}
		
		public DeleteResponse delete(IDeleteRequest request, UserDetails user)
		{
			log(request, user);
			vfs.delete(request.getPathList());
			return new DeleteResponse(request);
		}
		
		public RenameResponse rename(IRenameRequest request, UserDetails user)
		{
			log(request, user);
			String newfilename=vfs.rename(request.getPath(), request.getName(), request.getNewName());
			return new RenameResponse(request, newfilename);
		}
		
		public SearchResponse search(ISearchRequest request, UserDetails user)
		{
			log(request, user);
			List<IFile> files=vfs.search(request.getPath(), request.getSearchString(), request.getCaseSensitive());
			return new SearchResponse(request, files);
		}
		
		public DetailsResponse details(IDetailsRequest request, UserDetails user)
		{
			log(request, user);
			List<IFile> files=vfs.details(request.getPath(), request.getNames());
			return new DetailsResponse(request, files);
		}
		
		public CopyResponse copy(ICopyRequest request, UserDetails user)
		{
			log(request, user);
			for (int index=0; index<request.getNames().size(); index++)
			{
				String fromname=request.getNames().get(index);
				String toname=request.getRenameFiles().get(index);
				String from=request.getPath()+fromname;
				String to=request.getTargetPath()+toname;
				vfs.copy(from, to);
			}
			return new CopyResponse(request);
		}
		
		public MoveResponse move(IMoveRequest request, UserDetails user)
		{
			log(request, user);
			for (int index=0; index<request.getNames().size(); index++)
			{
				String fromname=request.getNames().get(index);
				String toname=request.getRenameFiles().get(index);
				String from=request.getPath()+fromname;
				String to=request.getTargetPath()+toname;
				vfs.move(from, to);
			}
			return new MoveResponse(request);
		}
		
		//https://github.com/spring-guides/gs-uploading-files/blob/main/complete/src/main/java/com/example/uploadingfiles/storage/FileSystemStorageService.java
		public void upload(UploadRequest request, UserDetails user)
		{
			log(request, user);
			for (MultipartFile file : request.getFiles())
			{
				vfs.upload(request.getPath(), file);
			}
		}
	
		//https://stackoverflow.com/questions/35680932/download-a-file-from-spring-boot-rest-service
		public DownloadResponse download(DownloadRequest request, UserDetails user)
		{
			log(request, user);
			String filename=vfs.download(request.getPath(), request.getNames());
			return new DownloadResponse(request, filename);
		}
		
		public GetImageResponse getImage(GetImageRequest request, UserDetails user)
		{
			log(request, user);
			BufferedImage image=vfs.getImage(request.getPath());
			return new GetImageResponse(request, image);
		}
		
		///////////////////////////		
		
		private void log(IRequest request, UserDetails user)
		{
			String filename=LogUtil.getBaseLogDir()+"/log-filemanager.txt";
			LogEntry entry=new LogEntry(request, user);
			String message=entry.getMessage();
			FileHelper.appendFile(filename, message);
			
			if (!entry.isNotified())
				return;
			String subject=entry.getSubject();
			notificationService.notify(subject, message, new MessageWriter());
		}
		
		//////////////////////////////////////////////////////
		
		public interface IRequest
		{
			Action getAction();
		}
		
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
			//String getAction();
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
			String getNewName();
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
			IReadRequest, ICreateRequest, IDeleteRequest, IRenameRequest,
			IDetailsRequest, ISearchRequest, ICopyRequest, IMoveRequest
		{
			private Action action;
			private String path;
			private List<String> names=Lists.newArrayList(); //used by: delete
			private Boolean showHiddenItems;
			private List<FileManagerDirectoryContent> data;
			private String name; //used by: rename
			private String newName; //used by: name
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
				//System.out.println("dir="+dir);
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
			private String cwd;
			private List<FileManagerDirectoryContent> files=Lists.newArrayList();
			private ErrorDetails error;
			private String details;
			
			public CreateResponse(ICreateRequest request)
			{
				super(request);
			}
		}
		
		////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class DeleteResponse extends ActionResponse
		{
			private String cwd;
			private List<FileManagerDirectoryContent> files=Lists.newArrayList();
			private ErrorDetails error;
			private String details;
			
			public DeleteResponse(IDeleteRequest request)
			{
				super(request);
			}
		}
		
		////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class RenameResponse extends ActionResponse
		{
			private String cwd;
			private List<FileManagerDirectoryContent> files=Lists.newArrayList();
			private ErrorDetails error;
			private String details;
			
			public RenameResponse(IRenameRequest request, String newfilename)
			{
				super(request);
				files.add(new FileManagerDirectoryContent(newfilename));
			}
		}
		
		///////////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class SearchResponse extends ActionResponse
		{
			private List<FileManagerDirectoryContent> files=Lists.newArrayList();
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
			private List<FileManagerDirectoryContent> files=Lists.newArrayList();
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
			private String path;
			@JsonIgnore private List<MultipartFile> files=Lists.newArrayList();
			
			public UploadRequest(String path, MultipartFile[] files)
			{
				this.path=path;
				for (MultipartFile file : files)
				{
					add(file);
				}
			}
			
			public void add(MultipartFile file)
			{
				this.files.add(file);
			}
			
			public List<String> getFilenames()
			{
				List<String> filenames=Lists.newArrayList();
				for (MultipartFile file : files)
				{
					filenames.add(file.getOriginalFilename());
				}
				return filenames;
			}
			
			@Override
			public Action getAction() {return Action.upload;}
		}
		
		///////////////////////////////////////////////////
		
		@Data @EqualsAndHashCode(callSuper=true)
		public static class DownloadRequest extends AbstractRequest
		{
			protected Action action;
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
			
			@Override
			public Action getAction() {return Action.getimage;}
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
		
		@Data
		public static class LogEntry
		{
			protected Date date;
			protected String username;
			protected String type;
			protected Action action;
			protected String request;
			protected String info;
			
			public LogEntry(IRequest request, UserDetails user)
			{
				this.date=new Date();
				this.username=user.getUsername();
				this.type=request.getClass().getSimpleName();
				this.action=request.getAction();
				this.request=getRequestJson(request);
				this.info=getInfo(request);
			}
			
			private String getRequestJson(IRequest request)
			{
				try
				{
					ObjectMapper mapper = new ObjectMapper();
					mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
					mapper.setSerializationInclusion(Include.NON_NULL);
					String json = mapper.writeValueAsString(request);
					return json;
				}
				catch(Exception e)
				{
					throw new CException(e);
				}
			}
			
			public boolean isNotified()
			{
				return action!=Action.read;
			}
			
			private String getInfo(IRequest request)
			{
				if (request.getAction()==Action.upload)
				{
					UploadRequest upload=(UploadRequest)request;
					return " path="+upload.getPath()+" files="+StringHelper.join(upload.getFilenames(), ", ");
				}
				
				if (request.getAction()==Action.download)
				{
					DownloadRequest download=(DownloadRequest)request;
					return " path="+download.getPath()+" files="+StringHelper.join(download.getNames(), ", ");
				}
				return "";
			}
			
			public String getMessage()
			{
				String line=DateHelper.format(date, DateHelper.DATETIME_PATTERN);
				line+="\t"+username;
				line+="\t"+type;
				line+="\t"+action;
				line+="\t"+request;
				return line;
			}
			
			public String getSubject()
			{
				return "["+username+"] file manager: action="+action+info;
			}
		}
	}
}