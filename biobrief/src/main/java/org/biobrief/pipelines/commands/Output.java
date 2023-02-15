package org.biobrief.pipelines.commands;

import java.util.Date;
import java.util.List;

import org.biobrief.pipelines.PipelineConstants.FastqFileType;
import org.biobrief.pipelines.PipelineConstants.FastqType;
import org.biobrief.pipelines.PipelineConstants.OutputType;
import org.biobrief.pipelines.util.NgsUtil;
import org.biobrief.util.BashUtil;
import org.biobrief.util.CException;
import org.biobrief.util.StringHelper;

import com.google.common.collect.Lists;

public class Output
{	
	private List<OutputFile> files=Lists.newArrayList();
	
	public List<OutputFile> getFiles(){return files;}
	
	public <O extends AbstractOutputFile> void add(O file)
	{
		files.add(file);
	}
	
	public <O extends AbstractOutputFile> void addAll(List<O> files)
	{
		this.files.addAll(files);
	}
	
	@SuppressWarnings("unchecked")
	public <O extends AbstractOutputFile> List<O> getOutputFiles(OutputType type)
	{
		List<O> list=Lists.newArrayList();
		for (OutputFile file : files)
		{
			if (file.getType()==type)
				list.add((O)file);
		}
		return list;
	}
	
	public List<FastqFile> getFastqFiles()
	{
		return getOutputFiles(OutputType.FASTQ);
	}
	
	public FastqFile getFastqFile()
	{
		return StringHelper.getOne(getOutputFiles(OutputType.FASTQ));
	}
	
	public BamFile getBamFile()
	{
		return StringHelper.getOne(getOutputFiles(OutputType.BAM));
	}
	
	public static <O extends AbstractOutputFile> List<String> getFilenames(List<O> files)
	{
		List<String> filenames=Lists.newArrayList();
		for (OutputFile file : files)
		{
			if (filenames.contains(file.getFilename()))
				throw new CException("found duplicate file: "+file.getFilename());
			filenames.add(file.getFilename());
		}
		return filenames;
	}
	
	public static <O extends AbstractOutputFile> String joinFiles(List<O> files)
	{
		return StringHelper.join(getFilenames(files), ",");
	}
	
	//////////////////////////////////////////////////////////////
	
//	public static FastqFile getFastqFile(String dir, Sample sample, FastqType fastqType, boolean gzipped)
//	{
//		String stem=dir+"/"+sample.getName();
//		return new FastqFile(NgsUtil.getFilename(stem, PairedEndReadType.NONE, gzipped));
//	}
//	
//	public static FastqFile getFastqFile1(String dir, Sample sample, FastqType fastqType, boolean gzipped)
//	{
//		String stem=dir+"/"+sample.getName();
//		return new FastqFile(NgsUtil.getFilename(stem, PairedEndReadType.R1, gzipped));
//	}
//	
//	public static FastqFile getFastqFile2(String dir, Sample sample, FastqType fastqType, boolean gzipped)
//	{		String stem=dir+"/"+sample.getName();
//		return new FastqFile(NgsUtil.getFilename(stem, PairedEndReadType.R2, gzipped));
//	}
	

	/////////////////////////////////////////////////////////////////////
	
	public interface OutputItem
	{
		OutputType getType();
		String getFilename();
	}
	
	public interface OutputFile extends OutputItem
	{	
		boolean isBinary();
		boolean isText();
	}

	private static abstract class AbstractOutputItem implements OutputItem
	{
		protected OutputType type;
		protected String filename;
		
		public AbstractOutputItem(OutputType type, String filename)
		{
			if (type==null)
				throw new CException("output type is null");
			if (!StringHelper.hasContent(filename))
				throw new CException("filename has no content: "+filename);
			this.type=type;
			this.filename=filename;
		}

		public OutputType getType(){return this.type;}
		public void setType(final OutputType type){this.type=type;}
		
		public String getFilename(){return this.filename;}
		public void setFilename(final String filename){this.filename=filename;}
	}
	
	private static abstract class AbstractOutputFile extends AbstractOutputItem implements OutputFile
	{
		protected Date date;
		protected Integer size;
		protected String note;
		
		public AbstractOutputFile(OutputType type, String filename)
		{
			super(type, filename);
		}

		public Date getDate(){return this.date;}
		public void setDate(final Date date){this.date=date;}

		public Integer getSize(){return this.size;}
		public void setSize(final Integer size){this.size=size;}

		public String getNote(){return this.note;}
		public void setNote(final String note){this.note=note;}
	}
	
	private static abstract class AbstractTextOutputFile extends AbstractOutputFile
	{
		public AbstractTextOutputFile(OutputType type, String filename)
		{
			super(type, filename);
		}
		
		@Override public boolean isBinary() { return false; }
		@Override public boolean isText() { return true; }
	}
	
	private static abstract class AbstractTabDelimitedOutputFile extends AbstractTextOutputFile
	{
		public AbstractTabDelimitedOutputFile(OutputType type, String filename)
		{
			super(type, filename);
		}
	}
	
	private static abstract class AbstractCommaDelimitedOutputFile extends AbstractTextOutputFile
	{
		public AbstractCommaDelimitedOutputFile(OutputType type, String filename)
		{
			super(type, filename);
		}
	}
	
	private static abstract class AbstractBinaryOutputFile extends AbstractOutputFile
	{
		public AbstractBinaryOutputFile(OutputType type, String filename)
		{
			super(type, filename);
		}
		
		@Override public boolean isBinary() { return true; }
		@Override public boolean isText() { return false; }
	}
	
	//////////////////////////////////////////////////
	
	public static class FastqFile extends AbstractTextOutputFile
	{
		public FastqFile(String filename)
		{
			super(OutputType.FASTQ, filename);
		}
		
		public boolean isGzipped()
		{
			return BashUtil.isGzipped(filename);
		}
		
		public boolean isPairedEnd()
		{
			return getFastqFileType().isPairedEnd();
		}
		
		public FastqType getFastqType()
		{
			return NgsUtil.getFastqType(filename);
		}
		
		public FastqFileType getFastqFileType()
		{
			return NgsUtil.getFastqFileType(filename);
		} 
	}
	
	public static class BamFile extends AbstractBinaryOutputFile
	{
		public BamFile(String filename)
		{
			super(OutputType.BAM, filename);
		}
	}
	
	public static class BaiFile extends AbstractBinaryOutputFile
	{
		public BaiFile(String filename)
		{
			super(OutputType.BAI, filename);
		}
	}
	
	public static class Fast5File extends AbstractBinaryOutputFile
	{
		public Fast5File(String filename)
		{
			super(OutputType.FAST5, filename);
		}
	}
	
	public static class TextFile extends AbstractTextOutputFile
	{
		public TextFile(String filename)
		{
			super(OutputType.TEXT, filename);
		}
	}
	
	public static class HtmlFile extends AbstractTextOutputFile
	{
		public HtmlFile(String filename)
		{
			super(OutputType.HTML, filename);
		}
	}	
	
	public static class MafFile extends AbstractTextOutputFile
	{
		public MafFile(String filename)
		{
			super(OutputType.MAF, filename);
		}
	}
	
	public static class TsvFile extends AbstractTabDelimitedOutputFile
	{
		public TsvFile(String filename)
		{
			super(OutputType.TSV, filename);
		}
	}
	
	public static class CsvFile extends AbstractCommaDelimitedOutputFile
	{
		public CsvFile(String filename)
		{
			super(OutputType.CSV, filename);
		}
	}
	
	public static class BlastTabFile extends AbstractTabDelimitedOutputFile
	{
		public BlastTabFile(String filename)
		{
			super(OutputType.BLASTTAB, filename);
		}
	}
	
	public static class LogFile extends AbstractTextOutputFile
	{
		public LogFile(String filename)
		{
			super(OutputType.LOG, filename);
		}
	}
	
	public static class VcfFile extends AbstractTextOutputFile
	{
		public VcfFile(String filename)
		{
			super(OutputType.VCF, filename);
		}
	}
	
	public static class FastaFile extends AbstractTextOutputFile
	{
		public FastaFile(String filename)
		{
			super(OutputType.FASTA, filename);
		}
	}
	
	public static class GtfFile extends AbstractTextOutputFile
	{
		public GtfFile(String filename)
		{
			super(OutputType.GTF, filename);
		}
	}
	
	public static class CountFile extends AbstractTextOutputFile
	{
		public CountFile(String filename)
		{
			super(OutputType.COUNTS, filename);
		}
	}
	
	public static class FlagStatFile extends AbstractTextOutputFile
	{
		public FlagStatFile(String filename)
		{
			super(OutputType.FLAGSTAT, filename);
		}
	}
	
	public static class Dir extends AbstractOutputItem
	{
		public Dir(String dir)
		{
			super(OutputType.DIR, dir);
		}
	}
	
//	@Data
//	public static class VirtualDirectory
//	{
//		protected String dir;
//		protected List<OutputItem> items=Lists.newArrayList();
//		
//		public VirtualDirectory(String dir)
//		{
//			this.dir=dir;
//		}
//		
//		public void add(OutputItem item)
//		{
//			this.items.add(item);
//		}
//	}
}
