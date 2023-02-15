package org.biobrief.pipelines.util;

import java.util.Date;
import java.util.List;

import org.biobrief.pipelines.PipelineConstants.FastqFileType;
import org.biobrief.pipelines.PipelineConstants.FastqType;
import org.biobrief.pipelines.PipelineConstants.PairedEndReadType;
import org.biobrief.util.BashUtil;
import org.biobrief.util.DateHelper;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;

public class NgsUtil
{
	public static Date parseDate(String value)
	{
		List<String> patterns=Lists.newArrayList(DateHelper.MMDDYYYY_PATTERN, DateHelper.YYYYMMDD_PATTERN);
		return DateHelper.parse(value, patterns, true);
	}
	
	public static String getStem(String filename)
	{
		String stem=StringHelper.replace(filename, ".gz", "");
		return StringHelper.replace(stem, ".fastq", "");
	}
		
	public static FastqFileType getFastqFileType(String filename)
	{
		boolean gzipped=BashUtil.isGzipped(filename);
		String stem=getStem(filename);
		if (stem.endsWith("_R1") || stem.endsWith("_1") || stem.contains("_R1_"))
			return gzipped ? FastqFileType.R1_GZ : FastqFileType.R1;
		if (stem.endsWith("_R2") || stem.endsWith("_2") || stem.contains("_R2_"))
			return gzipped ? FastqFileType.R2_GZ : FastqFileType.R2;
		return gzipped ? FastqFileType.GZ : FastqFileType.FQ;
	}
	
	public static FastqType getFastqType(String filename)
	{
		return getFastqFileType(filename).getFastqType();
	}
	
	public static String getFilename(String stem, PairedEndReadType type, boolean gzipped)
	{
		String filename=stem+type.getSuffix()+".fastq";
		if (gzipped)
			return filename+".gz";
		return filename;
	}
}
