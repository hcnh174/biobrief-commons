package org.biobrief.pipelines.util;

import java.util.List;
import java.util.Map;

import org.biobrief.pipelines.PipelineConstants.FastqMode;
import org.biobrief.pipelines.PipelineConstants.PairedEndReadType;
import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.pipelines.commands.Output.FastqFile;
import org.biobrief.pipelines.entities.Task;
import org.biobrief.pipelines.entities.Task.AbstractTaskParams;
import org.biobrief.util.BeanHelper;
import org.biobrief.util.CException;
import org.biobrief.util.FileHelper;
import org.biobrief.util.StringHelper;
import org.biobrief.util.YamlHelper;

import com.google.common.collect.Lists;

public class PipelineUtil
{
	private static final String READ_PAIR_PREFIX="_R";
	
	@SuppressWarnings("unchecked")
	public static <T extends Task> T readJobConfig(String dir)
	{
		String basedir=FileHelper.normalize(dir);
		List<String> parts=StringHelper.split(basedir, FileHelper.SEPARATOR);
		int num=parts.size();
		TaskType type=TaskType.get(parts.get(num-2));
		String filename=basedir+"/task.yaml";
		return (T)readJobConfig(filename, type);
	}
		
	@SuppressWarnings({ "rawtypes" })
	public static <T extends Task> T readJobConfig(String filename, TaskType type)
	{
		Class cls=getTaskClass(type);
		return readJobConfig(filename, cls);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends Task> T readJobConfig(String filename, Class cls)
	{
		return (T)YamlHelper.readFile(filename, cls);
	}

	//////////////////////////////////////////////////////////////////
	
	public static Class<?> getTaskClass(TaskType type)
	{
		String pckg=Task.class.getPackage().getName();
		String clsname=pckg+"."+type.getPrefix()+"Task";
		return BeanHelper.lookupClass(clsname);
	}
	
	public static Class<?> getTaskParamsClass(TaskType type)
	{
		String pckg=Task.class.getPackage().getName();
		String clsname=pckg+"."+type.getPrefix()+"Task$Params";
		return BeanHelper.lookupClass(clsname);
	}
	
	@SuppressWarnings("unchecked")
	public static <P extends AbstractTaskParams> P createTaskParamsClass(TaskType type, 
			Map<String, String> map)
	{
		Class<?> cls=PipelineUtil.getTaskParamsClass(type);
		System.out.println("createTaskParamsClass: "+cls.getName()+" map="+StringHelper.toString(map));//+" parent="+parent+");
		P params=(P)BeanHelper.instantiateClass(cls);//, parent);
		BeanHelper.getInstance().setPropertiesFromStrings(params, map, true);
		return params;
	}
	
	@SuppressWarnings("unchecked")
	public static <B extends Task, P extends AbstractTaskParams> B createTaskClass(TaskType type, P params)
	{
		Class<?> cls=PipelineUtil.getTaskClass(type);
		return (B)BeanHelper.instantiateClass(cls, params);
	}
	
	////////////////////////////////////////////
	
	public static boolean isPairedEnd(String filename)
	{
		return filename.contains(READ_PAIR_PREFIX);
	}
	
	// assumes paired end files have an _R1 and _R2 extension
	public static PairedEndReadType getPairedEndReadType(String filename)
	{
		if (!isPairedEnd(filename))
			return PairedEndReadType.NONE;
		int index=filename.indexOf(".");
		String root=filename.substring(0, index);
		index=root.lastIndexOf("_");
		String direction=root.substring(index+1);
		return PairedEndReadType.valueOf(direction);
	}
	
	public static boolean isCompressed(String filename)
	{
		return filename.endsWith(".gz");
	}
	
	// C68RKACXX_PR0188_05A25_H3_L003_R2.fastq.gz --> C68RKACXX_PR0188_05A25_H3_L003_R2
	public static String getRootName(String filename)
	{
		String root=StringHelper.remove(filename, ".gz");
		return StringHelper.remove(root, ".fastq");
	}
	
	// C68RKACXX_PR0188_05A25_H3_L003_R2.fastq.gz --> C68RKACXX_PR0188_05A25_H3_L003
	public static String getBaseName(String filename)
	{
		String rootname=getRootName(filename);
		if (!isPairedEnd(filename))
			return rootname;
		int index=rootname.lastIndexOf("_R");
		return rootname.substring(0, index);
	}
	
	public static String getExtension(String filename)
	{
		return isCompressed(filename) ? ".fastq.gz" : ".fastq";
	}
	
	public static List<String> findSampleDirectories(String dir)
	{
		List<String> subdirs=Lists.newArrayList();
		for (String subdir : FileHelper.listDirectories(dir, false))
		{
			if (subdir.startsWith("@"))
				continue;
			if (subdir.startsWith("."))
				continue;
			subdirs.add(subdir);
		}
		return subdirs;
	}
	
	public static List<String> findSamplesInBaseDir(String dir)
	{
		return findSampleDirectories(dir);
	}
	
	public static FastqMode findFastqTypeInBaseDir(String dir)
	{
		for (String subdir : findSampleDirectories(dir))
		{
			List<String> filenames=FileHelper.listFiles(dir+"/"+subdir);
			for (String filename : filenames)
			{
				if (!filename.contains(".fastq"))
					continue;
				return FastqMode.findFromFilename(filename);
			}
		}
		throw new CException("cannot determine fastq file type for dir: "+dir);
	}
	
	public interface UsesRef
	{
		String getRef();
	}
	
	public interface UsesFastq
	{
		FastqFile getInFastqFile();
		FastqFile getInFastqFile1();
		FastqFile getInFastqFile2();
	}
}
