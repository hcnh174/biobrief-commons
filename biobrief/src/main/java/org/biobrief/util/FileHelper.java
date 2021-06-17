package org.biobrief.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
//import org.apache.pdfbox.io.RandomAccessFile;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;

import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.jcraft.jzlib.GZIPInputStream;

import lombok.Data;
import net.lingala.zip4j.ZipFile;

public final class FileHelper
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(FileHelper.class);
	
	public static final char SEP='/';
	public static final String SEPARATOR="/";
	public static final String NEWLINE="\n";
	public static final Charset ENCODING=Charsets.UTF_8;
	public static final String TXT=".txt";
	public static final String PDF=".pdf";
	public static final String CSV=".csv";
	public static final String CSVJ=".csvj";
	public static final String TSV=".tsv";
	public static final String ZIP=".zip";
	
	private FileHelper(){}
	
	public static boolean exists(String filename)
	{
		File file=new File(filename);
		return file.exists();
	}
	
	public static void checkExists(String filename)
	{
		if (!exists(filename))
			throw new CException("File does not exist: "+filename);
	}
	
	public static Date getLastModifiedDate(String filename)
	{
		File file=new File(filename);
		return new Date(file.lastModified());
	}
	
	public static String readFile(String filename)
	{
		return readFile(filename, Charsets.UTF_8);
	}
	
	public static String readFile(String filename, Charset charset)
	{
		checkPath(filename);
		File file=new File(filename);
		return readFile(file, charset);
	}
	
	public static String readFile(File file, Charset charset)
	{
		try
		{
			//http://google.github.io/guava/releases/snapshot/api/docs/
			return Files.asCharSource(file, charset).read();
			//return Files.toString(file, charset);	
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static List<String> readLines(String filename)
	{
		return readLines(filename, Encoding.UTF8.getCharset());
	}
	
	public static List<String> readLines(String filename, Charset charset)
	{
		//checkPath(filename);
		//checkExists(filename);
		try
		{
			return Files.readLines(new File(filename), charset);
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static List<String> listDirectories(String dir, boolean fullpath)
	{
		try
		{
			checkExists(dir);
			List<String> directories=Lists.newArrayList();
			File directory=new File(dir);
			if (directory.isFile())
				return null;
			String[] filenames=directory.list();
			for (int index=0;index<filenames.length;index++)
			{
				String filename=filenames[index];
				String path=dir+"/"+filename;
				File file=new File(path);
				if (file.isDirectory())
					directories.add(fullpath ? path : filename);
			}
			return directories;
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}

	public static List<String> listFiles(String dirname)
	{
		return listFiles(dirname, null);
	}
	
	public static List<String> listFiles(String dirname, String suffix)
	{
		return listFiles(dirname, suffix, false);
	}
	
	public static List<String> listFiles(String dirname, boolean appendDir)
	{
		return listFiles(dirname, null, appendDir);
	}
	
	public static List<String> listFiles(String dirname, String suffix, boolean appendDir)
	{
		List<String> filenames=new ArrayList<String>();
		dirname=normalizeDirectory(dirname);
		checkExists(dirname);
		File dir=new File(dirname);
		if (!dir.isDirectory())
			throw new CException("not a directory: ["+dirname+"]");
		try
		{
			File[] files=dir.listFiles();
			for (int index=0;index<files.length;index++)
			{
				File file=files[index];
				if (file.isDirectory())
					continue; //TODO - breaking change!
				String filename=file.getName();
				if (suffix!=null && !filename.endsWith(suffix))
					continue;
				//logger.debug("filename="+filename);
				if (appendDir)
					filename=dirname+filename;
				filenames.add(filename);
			}
		}
		catch(Exception e)
		{
			throw new CException(dirname, e);
		}
		return filenames;
	}

	public static List<String> findFiles(String dir, String prefix, String suffix, boolean appendDir)
	{
		List<String> filenames=Lists.newArrayList();
		for (String filename : FileHelper.listFiles(dir, suffix, true))
		{
			if (FileHelper.stripPath(filename).startsWith(prefix))
				filenames.add(filename);
		}
		return filenames;
	}
	
	/*
	public static List<String> listFiles(String dirname, String suffix, boolean recursively)
	{
		if (recursively)
			return listFilesRecursively(dirname, suffix);
		else return listFiles(dirname, suffix);
	}
	*/
	
	public static List<String> listFilesRecursively(String dirname, String suffix, boolean recursively)
	{
		if (recursively)
			return listFilesRecursively(dirname, suffix);
		else return listFiles(dirname, suffix, true);
	}
	
	public static List<String> listFilesRecursively(String dirname)
	{
		List<String> filenames=new ArrayList<String>();
		listFilesRecursively(dirname, filenames, null, null);
		return filenames;
	}
	
	public static List<String> listFilesRecursively(String dirname, String suffix)
	{
		List<String> filenames=new ArrayList<String>();
		listFilesRecursively(dirname, filenames, suffix, null);
		return filenames;
	}
	
	public static List<String> listFilesRecursively(String dirname, String suffix, Date date)
	{
		List<String> filenames=new ArrayList<String>();
		listFilesRecursively(dirname, filenames, suffix, date);
		return filenames;
	}
	
	private static void listFilesRecursively(String dirname, List<String> filenames, String suffix, Date date)
	{
		try
		{
			dirname=normalizeDirectory(dirname);
			//logger.debug("dirname="+dirname);
			File dir=new File(dirname);
			if (!dir.isDirectory())
				throw new CException("not a directory");
			for (File file : dir.listFiles())
			{
				if (file.isDirectory())
					listFilesRecursively(file.getAbsolutePath(), filenames, suffix, date);
				String filename=file.getAbsolutePath();
				if (suffix!=null && !filename.endsWith(suffix))
					continue;
				//logger.debug("full filename="+filename+", lastModified="+file.lastModified()+" ("+new LocalDate(file.lastModified())+")");
				if (date==null || file.lastModified()>=date.getTime())
				{
					//logger.debug("adding file filename="+filename+", lastModified="+file.lastModified()+" ("+new LocalDate(file.lastModified())+")");
					//logger.debug("adding file filename="+filename+", lastModified="+CDateHelper.format(new LocalDate(file.lastModified()))+")");
					filenames.add(filename);
				}
			}
		}
		catch(Exception e)
		{
			throw new CException(dirname, e);
		}
	}
	
	////////////////////////////////////////////////////////
	
	public static List<FileInfo> getDirectoryInfo(String dir)
	{
		List<FileInfo> files=Lists.newArrayList();
		if (!FileHelper.exists(dir))
			return files;
		for (String filename : FileHelper.listFilesRecursively(dir))
		{
			if (!FileHelper.isFile(filename))
				continue;
			filename=FileHelper.normalize(filename);
			String mb=FileHelper.getFileSize(filename, FileSizeType.MEGABYTES);
			Date date=FileHelper.getLastModifiedDate(filename);
			String path=filename.substring(dir.length()+1);
			files.add(new FileInfo(path, mb, date));
		}
		return files;
	}
	
	public static Multimap<String, FileInfo> getDirectoryMap(String dir)
	{
		Multimap<String, FileInfo> files=ArrayListMultimap.create();
		if (!FileHelper.exists(dir))
			return files;
		for (String filename : FileHelper.listFilesRecursively(dir))
		{
			if (!FileHelper.isFile(filename))
				continue;
			filename=FileHelper.normalize(filename);
			String mb=FileHelper.getFileSize(filename, FileSizeType.MEGABYTES);
			Date date=FileHelper.getLastModifiedDate(filename);
			String path=filename.substring(dir.length()+1);
			if (path.indexOf(FileHelper.SEPARATOR)==-1) // ignores root directory
			{
				//System.out.println("cannot find file separator in path: "+path+" filename="+filename+" dir="+dir);
				continue;
			}
			String name=path.substring(0, path.indexOf(FileHelper.SEPARATOR));
			String file=path.substring(path.indexOf(FileHelper.SEPARATOR)+1);
			files.put(name, new FileInfo(file, mb, date));
		}
		return files;
	}
	
	@Data
	public static class FileInfo
	{
		protected String filename;
		protected String size;
		protected Date date;
		
		public FileInfo(String filename, String size, Date date)
		{
			this.filename=filename;
			this.size=size;
			this.date=date;
		}
	}
	
	//////////////////////////////////////////////////////////
	
	// returns false if any files could not be deleted
	public static boolean deleteFiles(String directoryname)
	{
		List<String> files=listFiles(directoryname);
		try
		{
			boolean result=true;
			for (int index=0;index<files.size();index++)
			{
				String filename=(String)files.get(index);
				File file=new File(filename);
				if (!file.delete())
				{
					StringHelper.println("File could not be deleted: "+filename);
					result=false;
				}
			}
			return result;
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	// returns true if the file was created
	public static boolean writeFileIfNotExists(String filename, String text)
	{
		if (exists(filename))
			return false;
		writeFile(filename, text);
		return true;
	}

	// creates an empty text file, useful for appending to
	public static String writeFile(String filename)
	{
		return writeFile(filename, "");
	}
	
	public static String writeFile(String filename, String text)
	{
		return writeFile(filename, text, ENCODING);
	}
	
	public static String writeFile(String filename, String text, Charset charset)
	{
		try
		{
			checkPath(filename);
			Files.createParentDirs(new File(filename));
			Files.asCharSink(new File(filename), charset).write(text);
			//Files.write(text, new File(filename), charset);
			return filename;
		}
		catch(IOException e)
		{
			throw new CException("failed to write file: "+filename, e);
		}
	}
	
	public static void appendFile(String filename, String str)
	{
		appendFile(filename, str, ENCODING, true);
	}
	
	public static void appendFile(String filename, String str, boolean newline)
	{
		appendFile(filename, str, ENCODING, newline);
	}
	
	public static void appendFile(String filename, String str, Charset charset)
	{
		appendFile(filename, str, charset, true);
	}
	
	public static void appendFile(String filename, String str, Charset charset, boolean newline)
	{
		try
		{
			checkPath(filename);
			Files.createParentDirs(new File(filename));
			String end=newline ? "\n" : "";
			Files.asCharSink(new File(filename), charset, FileWriteMode.APPEND).write(str+end);
			//Files.append(str+end, new File(filename), charset);
		}
		catch(IOException e)
		{
			throw new CException("failed to append file: "+filename, e);
		}
	}

	public static void moveFile(String from, String to)
	{
		try
		{
			Files.move(new File(from), new File(to));
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static void copyFile(String from, String to)
	{
		try
		{
			Files.copy(new File(from), new File(to));
		}
		catch(Exception e)
		{
			throw new CException("failed to copy file: "+from, e);
		}
	}


	public static boolean deleteFile(String filename)
	{
		File file=new File(filename);
		
		boolean result=false;

		try
		{
			if (!file.isFile())
				return false;
			result=file.delete();
		}
		catch(Exception e)
		{
			throw new CException("failed to delete file: "+filename, e);
		}
		
		return result;
	}

	public static boolean deleteDirectory(String dirname)
	{
		try
		{
			File directory=new File(dirname);
			if (!directory.isDirectory())
				return false;
			return directory.delete();
		}
		catch(Exception e)
		{
			throw new CException("failed to delete directory: "+dirname, e);
		}
		
		
	}
	
	public static void deleteDirectoryRecursively(String dirname)
	{
		try
		{
			FileUtils.deleteDirectory(new File(dirname));
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static void createParentDirs(String filename)
	{
		try
		{
			Files.createParentDirs(new File(filename));
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
	/**
	Checks to see if the requested directory already exists - if not, creates it
	*/
	public static void createDirectory(String dir)
	{
		try
		{
			File path=new File(dir);
			if (path.exists())
				return;
			path.mkdirs();
		}
		catch(SecurityException e)
		{
			throw new CException("failed to create directory: "+dir, e);
		}
	}

	public static String getResource(String path, Class<?> cls)
	{
		try
		{
			URL url=Resources.getResource(cls, path);
			return Resources.toString(url, ENCODING);
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
	// call without a leading slash, e.g. org/vardb/file.txt
	public static String getResource(String path)
	{
		try
		{
			URL url=Resources.getResource(path);
			return Resources.toString(url, ENCODING);
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
	// call without a leading slash, e.g. org/vardb/file.txt
	public static InputSource getResourceAsInputSource(String path)
	{
		InputStream stream=Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		InputStreamReader reader=new InputStreamReader(stream);
		return new InputSource(reader);
	}
	
	public static Properties getProperties(String path)
	{
		FileInputStream stream=null;
		try
		{
			Properties props=new Properties();
			stream=new FileInputStream(path);
			props.load(stream);
			return props;
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
		finally
		{
			closeStream(stream);
		}
	}
	
	public static String getTempDirectory()
	{
		return normalizeDirectory(System.getProperty("java.io.tmpdir"));
	}
	
	//https://alvinalexander.com/java/java-temporary-files-create-delete
	public static String createTempFile(String prefix, String suffix)
	{
		try
		{
			File file=File.createTempFile(prefix, suffix);
			file.deleteOnExit();
			return file.getAbsolutePath();
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}	
	
	public static String createTempFile(String prefix, String suffix, String text)
	{
		String filename=createTempFile(prefix, suffix);
		writeFile(filename, text);
		return filename;
	}
	
	public static String stripPath(String filepath)
	{
		filepath=normalize(filepath);
		int start=filepath.lastIndexOf(SEPARATOR);
		if (start==-1)
			start=0;
		else start+=1;
		return filepath.substring(start);
	}
	
	public static String stripFilename(String filepath)
	{
		filepath=normalize(filepath);
		int start=0;
		int end=filepath.lastIndexOf(SEPARATOR);
		if (end==-1)
			end=filepath.length();
		end+=1;
		return filepath.substring(start, end);
	}
	
	public static String stripExtension(String filename)
	{
		int index=filename.lastIndexOf('.');
		return filename.substring(0, index);
	}
	
	public static String stripFiletype(String filename)
	{
		return stripExtension(filename);
	}
	
	public static String getRoot(String filename)
	{
		return stripPath(stripFiletype(filename));
	}
	
	
	// gets the name of the directory in which the current file is found, without the full path
	public static String getParentDir(String filename)
	{
		String dir=stripFilename(filename);
		//logger.debug("dir="+dir);
		if (dir.endsWith(SEPARATOR))
			dir=dir.substring(0, dir.length()-1);
		String parent=stripPath(dir);
		//logger.debug("parent="+parent);
		return parent;
	}
	
	public static int countLines(String filename)
	{
		Scanner scanner=createScanner(filename, "\n");
		int lines=0;
		while (scanner.hasNext())
		{
			scanner.next();
			lines++;
		}
		scanner.close();
		return lines;
	}
	
	public static int countLinesRecursively(String folder, String suffix, String outfile)
	{
		if (outfile!=null)
			writeFile(outfile);
		int total_files=0;
		int total_lines=0;
		List<String> filenames=listFilesRecursively(folder, suffix);
		for (String filename : filenames)
		{
			total_files++;
			int count=countLines(filename);			
			total_lines+=count;
			//logger.debug(filename+": "+count+" lines");
			if (outfile!=null)
				appendFile(outfile, filename+"\t"+count);
		}
		appendFile(outfile, "total *"+suffix+" files: "+total_files);
		appendFile(outfile, "total *"+suffix+" lines: "+total_lines);
		return total_lines;
	}
	
//	public static void main(String[] argv)
//	{
//		String folder=argv[0];
//		String suffix=argv[1];
//		String outfile=argv.length==3 ? argv[2] : null;
//		countLinesRecursively(folder, suffix, outfile);
//	}
	
	public static String getDirFromFilename(String filename)
	{
		filename=normalize(filename);
		int end=filename.lastIndexOf(SEPARATOR);
		if (end==-1)
			end=0;
		else end+=1;
		String dir=filename.substring(0, end);
		return dir;
	}

	public static String getIdentifierFromFilename(String filename, String suffix)
	{
		filename=normalize(filename);
		int start=filename.lastIndexOf(SEPARATOR);
		if (start==-1)
			start=0;
		else start+=1;
		int end=filename.indexOf(suffix);
		if (end==-1)
			throw new CException("can't find identifier in filename: "+suffix);
		return filename.substring(start, end);
	}
	
	public static String getIdentifierFromFilename(String filename)
	{
		return getIdentifierFromFilename(filename, getSuffix(filename));
	}
	
	public static boolean hasSuffix(String filename)
	{
		filename=stripPath(normalize(filename));
		return filename.contains(".");
	}
	
	public static String getSuffix(String filename)
	{
		filename=normalize(filename);
		int start=filename.lastIndexOf('.');
		if (start==-1)
			throw new CException("can't determine suffix from filename: "+filename);
		return filename.substring(start);
	}
	
	public static String changeSuffix(String filename, String oldsuffix, String newsuffix)
	{
		filename=normalize(filename);
		int start=filename.lastIndexOf(oldsuffix);
		if (start==-1)
			throw new CException("can't find suffix "+oldsuffix+" in filename: "+filename);
		return filename.substring(0, start)+newsuffix;
	}

	public static String getIdentifierFromDirname(String dir)
	{
		dir=normalize(dir);
		if (dir.charAt(dir.length()-1)==SEP)
			dir=dir.substring(0, dir.length()-1);
		int start=dir.lastIndexOf(SEPARATOR);
		if (start==-1)
			throw new CException("can't find path separator in dir: "+dir);
		start+=1;
		return dir.substring(start);
	}
	
	public static String join(String...parts)
	{
		if (parts.length==0)
			throw new CException("no path elements specified");
		if (parts.length==1)
		{
			String path=normalize(parts[0].trim());
			if (!StringHelper.hasContent(path))
				throw new CException("no path elements specified");
			return path;
		}
		StringBuilder buffer=new StringBuilder();
		for (int index=0; index<parts.length-1; index++)
		{
			String part=parts[index].trim();
			buffer.append(normalizeDirectory(part));
		}
		buffer.append(parts[parts.length-1]);
		String path=buffer.toString();
		return normalize(path);
	}
	
	public static String normalize(String path)
	{
		if (!StringHelper.hasContent(path))
			throw new CException("path is empty or null");
		path=path.replace("\\", SEPARATOR); // use back-slashes only
		path=path.replace("//", SEPARATOR); // remove double slashes￥
		path=path.replace("￥", SEPARATOR); // use back-slashes only
		return path;
	}
	
	public static String normalizeDirectory(String path)
	{
		String dir=normalize(path);
		if (dir.endsWith(SEPARATOR))
			return dir;
		return dir+SEPARATOR;
	}
	
	public static String getFullPath(String dir)
	{
		dir=normalizeDirectory(dir);
		File file=new File(dir);
		dir=file.getAbsolutePath();
		if (dir.endsWith("."))
			dir=dir.substring(0, dir.length()-1);
		return normalizeDirectory(dir);
	}
	
	private static void checkPath(String filename)
	{
		if ((filename.indexOf("c:")==0 || filename.indexOf("d:")==0) && !PlatformType.find().isWindows())
			throw new CException("Windows path on a Unix platform: "+filename);
	}
	
	public static boolean isFolder(String path)
	{
		return normalize(path).endsWith(SEPARATOR);
	}
	
//	public static String getTimestamp()
//	{
//		LocalDate date=new LocalDate();
//		return String.valueOf(date.getTime());
//	}
	
	public static void writeToOutputStream(OutputStream stream, String stdin)
	{
		try
		{
			if (stdin==null)
				return;
			//logger.debug("writing stdin: "+stdin);
			BufferedReader reader=new BufferedReader(new StringReader(stdin));
			//String line;
			//while ((line=reader.readLine())!=null)
			for (String line=reader.readLine(); line!=null; line=reader.readLine())
			{
				stream.write(line.getBytes());
				stream.write("\n".getBytes());
			}
			stream.flush();
			stream.close();
		}
		catch (IOException t)
		{
			throw new CException(t);
		}
	}
	
	public static void closeReader(Reader reader)
	{
		if (reader==null)
			return;
		try
		{
			reader.close();
		}
		catch(IOException e)
		{
			//logger.debug("error closing stream"+e.getMessage());
		}
	}
	
	public static void closeReader(RandomAccessFile reader)
	{
		if (reader==null)
			return;
		try
		{
			reader.close();
		}
		catch(IOException e)
		{
			//logger.debug("error closing stream"+e.getMessage());
		}
	}
	
	public static void closeStream(InputStream stream)
	{
		if (stream==null)
			return;
		try
		{
			stream.close();
		}
		catch(IOException e)
		{
			//logger.debug("error closing stream"+e.getMessage());
		}
	}
	
	public static void closeStream(OutputStream stream)
	{
		if (stream==null)
			return;
		try
		{
			stream.close();
		}
		catch(IOException e)
		{
			//logger.debug("error closing stream"+e.getMessage());
		}
	}
	
	
	public static void closeWriter(Writer writer)
	{
		if (writer==null)
			return;
		try
		{
			writer.close();
		}
		catch(IOException e)
		{
			//logger.debug("error closing writer"+e.getMessage());
		}
	}

	public static void unGzipFiles(String folder, List<String> filenames)
	{
		for (String filename : filenames)
		{
			unGzipFile(folder+filename);
		}
	}
	
	public static String unGzipFile(String infile)
	{
		GZIPInputStream in=null;
		OutputStream out=null;
		try
		{
			// Open the compressed file
			in = new GZIPInputStream(new FileInputStream(infile));
			// Open the output file
			String outfile=infile.substring(0, infile.lastIndexOf('.'));
			out = new FileOutputStream(outfile);
			// Transfer bytes from the compressed file to the output file
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
			return outfile;
		}
		catch (IOException e)
		{
			throw new CException("failed unGzipping file: "+infile, e);
		}
		finally
		{
			// Close the file and stream
			 closeStream(in);
			 closeStream(out);
		}
	}
	
	public static FileReader createFileReader(String filename)
	{
		try
		{
			return new FileReader(filename);
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static StringReader createStringReader(String filename)
	{
		try
		{
			return new StringReader(filename);
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static Scanner createScanner(String filename)
	{
		return createScanner(filename, "\n");
	}
	
	public static Scanner createScanner(String filename, String delimiter)
	{
		try
		{
			FileReader reader=new FileReader(filename);
			return createScanner(reader, delimiter);
			//return new Scanner(reader).useDelimiter(delimiter);
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static void closeScanner(Scanner scanner)
	{
		if (scanner==null)
			return;
		scanner.close();
	}
	
	public static Scanner createScanner(String filename, String delimiter, Charset charset)
	{
		try
		{
			InputStreamReader reader=new InputStreamReader(new FileInputStream(filename), charset.toString());
			return createScanner(reader, delimiter);
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	@SuppressWarnings("resource")
	public static Scanner createScanner(Reader in, String delimiter)
	{
		try
		{
			BufferedReader reader=new BufferedReader(in);
			return new Scanner(reader).useDelimiter(delimiter);
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static String getFilenameAsUrl(String filename)
	{
		if (PlatformType.find().isWindows())
			return "file:///"+filename;
		else return "file://"+filename;
	}
	
   public static String prompt(String message)
	{
		try
		{
			//logger.debug(message);
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			return br.readLine();
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static boolean confirm(String message)
	{
		String response=prompt(message);
		return (response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes"));
	}
	
	public static FileInputStream openFileInputStream(String path)
	{
		try
		{
			checkExists(path);
			return new FileInputStream(path);
		}
		catch (FileNotFoundException e)
		{
			throw new CException(e);
		}
	}
	
//	public static InputStream getInputStream(MultipartFile file)
//	{
//		try
//		{
//			return file.getInputStream();
//		}
//		catch (IOException e)
//		{
//			throw new CException(e);
//		}
//	}
	
	//http://stackoverflow.com/questions/4964640/reading-inputstream-as-utf-8
	//https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
	public static String readInputStream(InputStream stream) throws IOException
	{
		try (BufferedReader reader=new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)))
		{
			String line;
			StringBuilder buffer=new StringBuilder();
			while ((line=reader.readLine())!=null)
			{
				buffer.append(line).append("\n");
			}
			return buffer.toString();
		}
	}

	public static boolean isCsv(String filename)
	{
		String suffix=FileHelper.getSuffix(filename);
		return suffix.equals(FileHelper.CSV) || suffix.equals(CSVJ);
	}
	
	public static boolean isTsv(String filename)
	{
		String suffix=FileHelper.getSuffix(filename);
		return suffix.equals(FileHelper.TSV);
	}
	
	public static boolean isTxt(String filename)
	{
		String suffix=FileHelper.getSuffix(filename);
		return suffix.equals(FileHelper.TXT);
	}
	
	public static boolean isFile(String filename)
	{
		File file=new File(filename);
		return file.isFile();
	}
	
	public static boolean isDirectory(String filename)
	{
		File file=new File(filename);
		return file.isDirectory();
	}
	
	public enum Encoding
	{		
		UTF8("UTF-8", Charsets.UTF_8), 
		SHIFT_JIS("Shift_JIS"), 
		US_ASCII("US_ASCII", Charsets.US_ASCII);
		
		Encoding(String encoding, Charset charset)
		{
			this.encoding=encoding;
			this.charset=charset;
		}
		
		Encoding(String encoding)
		{
			this.encoding=encoding;
			this.charset=Charset.forName(encoding);
		}
		
		private final String encoding;
		private final Charset charset;
		
		public String getEncoding()
		{
			return encoding;
		}
		
		public Charset getCharset()
		{
			return charset;
		}
		
		@Override
		public String toString()
		{
			return this.encoding;
		}
	}
	

	public static void convertEncoding(String oldfilename, Encoding oldencoding, String newfilename, Encoding newencoding)
	{
		String delimiter=FileHelper.NEWLINE;
		checkPath(oldfilename);
		checkPath(newfilename);
		Scanner scanner=null;
		BufferedWriter writer=null;
		//int counter=0;
		try
		{
			Charset charset = Charset.forName(newencoding.toString());//Encoding.UTF8.toString());
			CharsetEncoder encoder = charset.newEncoder();
			encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
			encoder.replaceWith("?".getBytes());
			
			//writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newfilename), newencoding.toString()));
			writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newfilename), encoder));
			scanner=createScanner(oldfilename, delimiter, oldencoding);
			while (scanner.hasNext())
			{
				String line=scanner.nextLine();
				//line=StringHelper.replaceUnreadableChars(line, newencoding);
				writer.write(line);
				writer.newLine();
				//counter++;
				//if (counter>3)
				//	break;
			}
			writer.flush();
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
		finally
		{
			closeWriter(writer);
			closeScanner(scanner);
		}		
	}

	public static Scanner createScanner(String filename, String delimiter, Encoding encoding)
	{
		try
		{
			InputStreamReader reader=new InputStreamReader(new FileInputStream(filename), encoding.toString());
			return createScanner(reader, delimiter);
		}
		catch(Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static String detectCharset(String filename)
	{
		FileInputStream fis=null;
		try
		{
			byte[] buf = new byte[4096];
			fis = new FileInputStream(filename);
			UniversalDetector detector = new UniversalDetector(null);
			int nread;
			while ((nread = fis.read(buf)) > 0 && !detector.isDone())
			{
				detector.handleData(buf, 0, nread);
			}
			detector.dataEnd();
			String encoding = detector.getDetectedCharset();
			if (encoding == null)
				throw new CException("No encoding detected for file: "+filename);
			//logger.debug("Detected encoding = " + encoding);
			detector.reset();
			return encoding;
		}
		catch (FileNotFoundException e)
		{
			throw new CException(e);
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
		finally
		{
			closeStream(fis);
		}
	}
	
//	public static String writeFile(MultipartFile file, String dir)
//	{
//		if (file.isEmpty())
//			throw new CException("Uploaded multipart file was empty");
//		BufferedOutputStream stream=null;
//		try
//		{
//			String filename=dir+"/"+file.getOriginalFilename();
//			stream=new BufferedOutputStream(new FileOutputStream(new File(filename)));
//			FileCopyUtils.copy(file.getInputStream(), stream);
//			return filename;
//		}
//		catch (Exception e)
//		{
//			throw new CException("You failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
//		}
//		finally
//		{
//			FileHelper.closeStream(stream);
//		}
//	}
	
	//https://stackoverflow.com/questions/24666805/java-only-read-first-line-of-a-file
	//https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
	//http://www.jroller.com/ethdsy/entry/filereader_filewriter_and_character_encoding
	public static String readLine(String filename, String encoding)
	{
		try (BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(filename), encoding)))
		{
			return reader.readLine();
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static String getCurrentDirectory()
	{
		return normalize(Paths.get(".").toAbsolutePath().normalize().toString());
	}
	
	public static String getWorkspaceDirectory()
	{
		String pwd=getCurrentDirectory();
		String target="/workspace/";
		int index=pwd.indexOf(target);
		if (index==-1)
			throw new CException("could not find base directory name in current path: "+pwd);
		return pwd.substring(0, index+target.length()-1);
	}
	
	public static String getBaseDirectory()
	{
		String pwd=getCurrentDirectory();
		if (!pwd.endsWith("/"))
			pwd+="/";
		//System.out.println("FileHelper.getBaseDir: pwd="+pwd);
		String target="/workspace/";
		int index=pwd.indexOf(target);
		if (index==-1)
			throw new CException("could not find base directory name in current path: "+pwd);
		//System.out.println("index="+index);
		index=index+target.length()+1;
		//System.out.println("new index="+index);
		index=pwd.indexOf("/", index);
		//System.out.println("final index="+index+" (pwd="+pwd+")");
		return pwd.substring(0, index);
	}
	
	public static Date getLastModifiedDate(File file)
	{
		return new Date(file.lastModified());
	}
	
	//https://stackoverflow.com/questions/2723838/determine-file-creation-date-in-java
	public static Date getCreatedDate(File file)
	{
		Path path = file.toPath();
		BasicFileAttributes attributes;
		try
		{
			attributes = java.nio.file.Files.readAttributes(path, BasicFileAttributes.class);
//			System.out.println("Creation date: " + attributes.creationTime());
//			System.out.println("Last access date: " + attributes.lastAccessTime());
//			System.out.println("Last modified date: " + attributes.lastModifiedTime());
			return new Date(attributes.creationTime().to(TimeUnit.MILLISECONDS));
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	
	public enum FileSizeType {MEGABYTES, KILOBYTES, BYTES}
	
	public static String getFileSize(String filename, FileSizeType type)
	{
		return getFileSize(new File(filename), type);
	}
	
	public static String getFileSize(File file, FileSizeType type)
	{
		switch(type)
		{
		case MEGABYTES:
			return getFileSizeMegaBytes(file);
		case KILOBYTES:
			return getFileSizeMegaBytes(file);
		case BYTES:
			return getFileSizeBytes(file);
		default:
			throw new CException("no handler for type: "+type);
		}
	}
	
	public static String getFileSizeMegaBytes(File file)
	{
		return format((double) file.length() / (1024 * 1024)) + " mb";
	}
	
	public static String getFileSizeKiloBytes(File file)
	{
		return format((double) file.length() / 1024) + "  kb";
	}

	public static String getFileSizeBytes(File file)
	{
		return format((double)file.length()) + " bytes";
	}
	
	private static String format(Double value)
	{
		return StringHelper.format(value, 2);
	}
	
	//https://stackoverflow.com/questions/10135074/download-file-from-https-server-using-java
	//https://www.baeldung.com/convert-input-stream-to-a-file
	public static String downloadHttpsFile(String filename, String dir)
	{
		try
		{
			URL url = new URL(filename);
			URLConnection connection = url.openConnection();
			InputStream inStream = connection.getInputStream();
			String newfilename=dir+"/"+FileHelper.stripPath(filename);
			File file = new File(newfilename);
			java.nio.file.Files.copy(inStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);			
//			byte[] buffer = new byte[inStream.available()];
//			inStream.read(buffer);
//			String newfilename=dir+"/"+FileHelper.stripPath(filename);
//			File file = new File(newfilename);
//			OutputStream outStream = new FileOutputStream(file);
//			outStream.write(buffer);
			closeStream(inStream);
//			closeStream(outStream);
			return newfilename;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	public static String unzip(String filename, String password)
	{
		String dir=FileHelper.stripFilename(filename)+FileHelper.stripPath(FileHelper.stripExtension(filename));
		return unzip(filename, dir, password);
	}
	
	//https://github.com/srikanth-lingala/zip4j
	public static String unzip(String filename, String outdir, String password)
	{
		try
		{
			System.out.println("zipfile="+filename);
			FileHelper.checkExists(filename);
			//String dir=FileHelper.stripFilename(filename)+FileHelper.stripPath(FileHelper.stripExtension(filename));
			System.out.println("dir="+outdir);
			new ZipFile(filename, password.toCharArray()).extractAll(outdir);
			return outdir;
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	// "/mnt/out" "o:/out"
	public static String convertPath(String filename, String linuxDir, String windowsDir)
	{
		System.out.println("convertPath: filename="+filename+", linuxDir="+linuxDir+", windowsDir="+windowsDir);
		if (RuntimeHelper.isWindows())
		{
			String newfilename=StringHelper.replace(filename, linuxDir, windowsDir);
			System.out.println("converted filename: "+newfilename);
			return FileHelper.normalize(newfilename);
		}
		return FileHelper.normalize(filename);
	}

	// escapes illegal characters in stem to create a valid filename 
	//https://stackoverflow.com/questions/1184176/how-can-i-safely-encode-a-string-in-java-to-use-as-a-filename
	public static String getFileSafeName(String stem)
	{
		if (stem==null)
			throw new CException("stem is null");
		char fileSep = '/'; // ... or do this portably.
		char escape = '%'; // ... or some other legal char.
		String s = stem;
		int len = s.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
		{
		    char ch = s.charAt(i);
		    if (ch < ' ' || ch >= 0x7F || ch == fileSep || (ch == '.' && i == 0) || ch == escape)
		    {
		        sb.append(escape);
		        if (ch < 0x10)
		        	sb.append('0');
		        sb.append(Integer.toHexString(ch));
		    }
		    else
		    {
		        sb.append(ch);
		    }
		}
		return sb.toString();
	}
	
	public static void waitForFile(String filename, int milliseconds, int tries)
	{
		waitForFile(filename, milliseconds, tries, true);
	}
	
	public static void waitForFile(String filename, int milliseconds, int tries, boolean strict)
	{
		int counter=0;
		while (!FileHelper.exists(filename))
		{
			System.out.println("waiting for file: "+filename+" try="+counter);
			ThreadHelper.sleep(milliseconds);
			if (counter>tries)
			{
				if (strict)
					throw new CException("waited for "+tries*milliseconds+" ms for file: "+filename+". giving up");
				else return;
			}
			counter++;
		}
	}
	
	public static void waitForFile(String filename)
	{
		waitForFile(filename, 1000, 10);
	}
	
	
	/*
	public static String unzip(String zipfile)
	{
		try
		{
			System.out.println("zipfile="+zipfile);
			String dir=FileHelper.stripFilename(zipfile)+"/"+FileHelper.stripPath(FileHelper.stripExtension(zipfile));
			System.out.println("dir="+dir);
			File destDir = new File(dir);
			byte[] buffer = new byte[1024];
			
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipfile));
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null)
			{
				File newFile = unzip(destDir, zipEntry);
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
			return dir;
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
	*/
	

	/*
	public class UnzipFile {
		public static void main(String[] args) throws IOException {
			String fileZip = "src/main/resources/unzipTest/compressed.zip";
			File destDir = new File("src/main/resources/unzipTest");
			byte[] buffer = new byte[1024];
			ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				File newFile = newFile(destDir, zipEntry);
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		}
		 
		public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
			File destFile = new File(destinationDir, zipEntry.getName());
			 
			String destDirPath = destinationDir.getCanonicalPath();
			String destFilePath = destFile.getCanonicalPath();
			 
			if (!destFilePath.startsWith(destDirPath + File.separator)) {
				throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
			}
			 
			return destFile;
		}
	}
	*/
	
	/*
	public abstract static class ZipFileReader
	{		
		protected String encoding;
		public ZipFileReader(String encoding)
		{
			this.encoding=encoding;
		}
		
		public void loadFile(String filename)
		{
			try
			{
				ZipFile zipFile = new ZipFile(filename);
				Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
				while (enumeration.hasMoreElements())
				{
					ZipEntry zipEntry = enumeration.nextElement();
					String str=read(zipFile, zipEntry);
					handle(zipFile, zipEntry, str);
				}
			}
			catch (FileNotFoundException e)
			{
				throw new CException(e);
			}
			catch (IOException e)
			{
				throw new CException(e);
			}
		}
		
		private String read(ZipFile zipFile, ZipEntry zipEntry) throws IOException
		{
			//logger.debug("Unzipping: " + zipEntry.getName());
			BufferedInputStream in = new BufferedInputStream(zipFile.getInputStream(zipEntry));
			int size;
			byte[] buffer = new byte[2048];
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			while ((size = in.read(buffer, 0, buffer.length)) != -1)
			{
				out.write(buffer, 0, size);
			}
			out.flush();
			String str=out.toString(encoding);
			out.close();
			in.close();
			return str;
		}
		
		protected abstract void handle(ZipFile zipFile, ZipEntry zipEntry, String str);
	}
	*/
	
	/*
	//https://www.baeldung.com/java-nio2-watchservice
	//https://www.thecoderscorner.com/team-blog/java-and-jvm/java-nio/36-watching-files-in-java-7-with-watchservice/
	//https://stackoverflow.com/questions/18701242/how-to-watch-a-folder-and-subfolders-for-changes
	//https://gist.github.com/fabriziofortino/83eb36c7b48e9b900c1da1d8508245cd
	//https://fabriziofortino.github.io/articles/recursive-watchservice-java8/
	//https://howtodoinjava.com/java8/java-8-watchservice-api-tutorial/
	//https://fabriziofortino.github.io/articles/recursive-watchservice-java8/
	public static void watch(String dir, WatchQueueCallback callback)
	{
		try
		{
			Path path = Paths.get(dir);
			if(path == null)
				throw new UnsupportedOperationException("Directory not found: "+dir);
			
			// make a new watch service that we can register interest in directories and files with.
			WatchService watchService = path.getFileSystem().newWatchService();
			
			// start the file watcher thread below
			WatchQueueReader fileWatcher = new WatchQueueReader(watchService, callback);
			
			System.out.println("watching for events in dir: "+dir);
			Thread th = new Thread(fileWatcher, "FileWatcher");
			th.start();
			
			// register a file
			path.register(watchService,
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE, 
					StandardWatchEventKinds.ENTRY_MODIFY);
			th.join();
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	/**
	 * This Runnable is used to constantly attempt to take from the watch
	 * queue, and will receive all events that are registered with the
	 * fileWatcher it is associated. In this sample for simplicity we
	 * just output the kind of event and name of the file affected to
	 * standard out.
	 */
//	public static class WatchQueueReader implements Runnable
//	{
//		private final WatchService watcher;
//		private final WatchQueueCallback callback;
//		
//		public WatchQueueReader(WatchService watcher, WatchQueueCallback callback)
//		{
//			this.watcher = watcher;
//			this.callback=callback;
//		}
//
//		/**
//		 * In order to implement a file watcher, we loop forever
//		 * ensuring requesting to take the next item from the file
//		 * watchers queue.
//		 */
//		@SuppressWarnings("rawtypes")
//		@Override
//		public void run()
//		{
//			try
//			{
//				// get the first event before looping
//				WatchKey key = watcher.take();
//				while(key != null)
//				{
//					// we have a polled event, now we traverse it and
//					// receive all the states from it
//					for (WatchEvent event : key.pollEvents())
//					{
//						System.out.printf("Received %s event for file: %s\n", event.kind(), event.context());
//						callback.onEvent(event);
//					}
//					System.out.println("resetting");
//					key.reset();
//					key = watcher.take();
//				}
//			}
//			catch (InterruptedException e)
//			{
//				throw new CException(e);
//			}
//			System.out.println("Stopping thread");
//		}
//	}
}