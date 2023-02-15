package org.biobrief.pipelines;

import java.util.List;
import java.util.Optional;

import org.biobrief.util.CException;
import org.biobrief.util.DataFrame.StringDataFrame;
import org.biobrief.util.ExportedEnum;
import org.biobrief.util.ExportedField;
import org.biobrief.util.StringHelper;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;

public interface PipelineConstants
{
	public static final Integer TAXID_HBV=10407;
	public static final String PIPELINE_DIR="c:/workspace/hlsg/data/ngs/pipelines";
	public static final StringDataFrame TASK_TYPES=StringDataFrame.parseTabFile("c:/workspace/hlsg/data/pipelines/tasktypes.txt");
	
	@ExportedEnum public enum TaskGroup
	{
		INIT,
		PIPELINE,
		UTILITIES,
		TRIMMING,
		QUALITY_CONTROL,
		MAPPING,
		VARIANT_CALLING,
		VARIANT_FILTER,
		DENOVO_ASSEMBLY,
		FEATURE_COUNTS,
		DIFFERENTIAL_EXPRESSION,
		BLAST
	}

	@ExportedEnum public enum TaskType
	{	
		BLAST,
		BOWTIE,
		BRIDGER,
		BWA,
		CANU,
		CLAIR,
		CLAIRVOYANTE,
		CONSENSUS,
		CUFFLINKS,
		DE_SEQ2,
		DEEP_VARIANT,
		EDGE_R,
		FASTP,
		FASTQ_TO_FASTA,
		FASTQC,
		FEATURE_COUNTS,
		FILT_LONG,
		FILTER_BAM,
		FREE_BAYES,
		HISAT,
		IMPORT_FASTQ,
		INIT,
		KRAKEN,
		KRAKEN_FILTER,
		LAST,
		LAST_FILTER,
		LIMMA,
		LONGSHOT,
		MARS,
		MINIMAP,
		NANO_FILT,
		NANO_PLOT,
		NANO_Q_C,
		NANO_STAT,
		NANO_SV,
		NANOPOLISH,
		NANO_VAR,
		NGMLR,
		PICKY,
		PIPELINE,
		PORECHOP,
		SALMON,
		SAMPLE_FASTQ,
		SAMPLE_PAIRED_FASTQ,
		SEAL,
		SELECT_FASTQ,
		SELECT_SAMPLE,
		SNIFFLES,
		STAR,
		STRINGTIE,
		SUBREAD,
		SVIM,
		TEST,
		TOPHAT,
		TRIMMOMATIC,
		VCF_FILTER;
		
		@ExportedField private final String display;
		@ExportedField private final TaskGroup group;
		@ExportedField private final List<OutputType> requires;
		@ExportedField private final List<OutputType> produces;
		@ExportedField private final NgsPlatform platform;
		@ExportedField private final String folder;
		@ExportedField private final Integer memory;
		
		TaskType()
		{
			this.display=getRequiredStringValue(TASK_TYPES, this, "display");
			this.group=TaskGroup.valueOf(getRequiredStringValue(TASK_TYPES, this, "group"));
			this.requires=getOutputTypes(TASK_TYPES, this, "requires");
			this.produces=getOutputTypes(TASK_TYPES, this, "produces");
			this.platform=NgsPlatform.valueOf(getStringValue(TASK_TYPES, this, "platform"));
			this.memory=getIntValue(TASK_TYPES, this, "memory");
			this.folder=StringHelper.replace(this.name().toLowerCase(), "_", "");
		}
		
		public static TaskType get(String value)
		{
			Optional<TaskType> opt=find(value);
			if (opt.isPresent())
				return opt.get();
			throw new CException("cannot find task type: "+value);
		}
		
		public static Optional<TaskType> find(String value)
		{
			for (TaskType type : values())
			{
				String name=StringHelper.replace(type.name(), "_", "");
				if (value.equalsIgnoreCase(name))
					return Optional.of(type);
			}
			return Optional.empty();
		}
		
		public String getPrefix()
		{
			return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
		}
		
		public String getJobName()
		{
			return name().toLowerCase();
		}
		
//		public boolean accepts(TaskType parent)
//		{
//			for (OutputType type : parent.getProduces())
//			{
//				if ()
//			}
//			return parent.getProduces()==getAccepts();
//		}
		
//		public List<TaskType> getAvailable()
//		{
//			List<TaskType> tasks=Lists.newArrayList();
//			for (TaskType task : values())
//			{
//				if (accepts(task))
//					tasks.add(task);
//			}
//			return tasks;
//		}
		
		public String getDisplay() {return display;}
		public TaskGroup getGroup() {return group;}
		public Integer getMemory(){return memory;}
		public String getLabel(){return display;}
		public String getFolder(){return folder;}
		public List<OutputType> getRequires(){return this.requires;}
		public List<OutputType> getProduces(){return this.produces;}
		public NgsPlatform getPlatform(){return this.platform;}
		
		private static String getStringValue(StringDataFrame df, TaskType type, String field)
		{
			return df.getStringValue(field, type.name(), "");
		}
		
		private static String getRequiredStringValue(StringDataFrame df, TaskType type, String field)
		{
			String value=df.getStringValue(field, type.name());
			if (value==null)
				throw new CException("cannot find value for field="+field+" in task type="+type);
			return value;
		}
		
		private static Integer getIntValue(StringDataFrame df, TaskType type, String field)
		{
			return df.getIntValue(field, type.name()); 
		}
		
		private static List<OutputType> getOutputTypes(StringDataFrame df, TaskType type, String field)
		{
			List<OutputType> types=Lists.newArrayList();
			for (String value : StringHelper.split(df.getStringValue(field, type.name()), ","))
			{
				types.add(OutputType.valueOf(value));
			}
			return types;
		}
	}
	
	@ExportedEnum public enum FastqType
	{
		PairedEnd,
		Unpaired;
	}
	
	@ExportedEnum public enum FastqMode
	{
		SE(FastqType.Unpaired, false),
		SE_GZ(FastqType.Unpaired, true),
		PE(FastqType.PairedEnd, false),
		PE_GZ(FastqType.PairedEnd, true);
		
		private FastqType type;
		private boolean gzipped;
		
		FastqMode(FastqType type, boolean gzipped)
		{
			this.type=type;
			this.gzipped=gzipped;
		}
		
		public boolean matches(FastqType type, boolean gzipped)
		{
			return this.type==type && this.gzipped==gzipped;
		}
		
		public FastqType getType() {return type;}
		public boolean isGzipped() {return gzipped;}
		public boolean isPaired() {return type==FastqType.PairedEnd;}
		
		public static FastqMode findFromFilename(String filename)
		{
			FastqType type=FastqType.Unpaired;
			boolean gzipped=false;
			if (filename.contains("_R1.fastq") || filename.contains("_R2.fastq"))
				type=FastqType.PairedEnd;
			if (filename.endsWith(".gz"))
				gzipped=true;
			for (FastqMode mode : values())
			{
				if (mode.matches(type, gzipped))
					return mode;
			}
			throw new CException("could not determine fastq mode for: "+filename);
		}
	}

	@ExportedEnum public enum FastqFileType
	{
		R1_GZ(true, PairedEndReadType.R1),
		R2_GZ(true, PairedEndReadType.R2),
		R1(false, PairedEndReadType.R1),
		R2(false, PairedEndReadType.R2),
		GZ(true),
		FQ(false);
		
		private boolean gzipped;
		private FastqType fastqType;
		private PairedEndReadType pairedEndReadType;
				
		FastqFileType(boolean gzipped)
		{
			this.gzipped=gzipped;
			this.fastqType=FastqType.Unpaired;
			this.pairedEndReadType=PairedEndReadType.NONE;
		}
		
		FastqFileType(boolean gzipped, PairedEndReadType pairedEndReadType)
		{
			this.gzipped=gzipped;
			this.fastqType=FastqType.PairedEnd;
			this.pairedEndReadType=pairedEndReadType;
		}
		
		public String getSuffix()
		{
			String suffix=pairedEndReadType.getSuffix()+".fastq";
			if (gzipped)
				suffix+=".gz";
			return suffix;
		}
		
		public boolean isPairedEnd()
		{
			return fastqType==FastqType.PairedEnd;
		}
		
		public boolean isGzipped() {return gzipped;}
		public FastqType getFastqType() {return fastqType;}
		public PairedEndReadType getPairedEndReadType() {return pairedEndReadType;}
	}
	
	@ExportedEnum public enum PairedEndReadType
	{
		R1("_R1"),
		R2("_R2"),
		NONE("");
		
		private String suffix;
		
		PairedEndReadType(String suffix)
		{
			this.suffix=suffix;
		}
		
		public String getSuffix() {return suffix;}
	}
	
	@ExportedEnum public enum OutputType
	{
		NULL,
		FASTQ,
		FASTQ_SE,
		FASTQ_PE,
		FASTA,
		FAST5,
		BAM,
		BAI,
		VCF,
		MAF,
		DIR,
		COUNTS,
		QUANT,
		MICROARRAY,
		FC,
		BLASTTAB,
		GTF,
		TSV,
		CSV,
		LOG,
		FLAGSTAT,
		TEXT,
		HTML
	}
	
	@ExportedEnum public enum NgsPlatform
	{
		ANY,
		HISEQ,
		MISEQ,
		IONTORRENT,
		NANOPORE
	}
	
	@ExportedEnum public enum JobStatus
	{
		NULL("", true),
		PENDING("Pending", false),
		RUNNING("Running", false),
		FAILED("Failed", true),
		COMPLETED("Completed", true);
		
		private String display;
		private boolean terminal;
		private Character prefix;
		
		JobStatus(String display, boolean terminal)
		{
			this.display=display;
			this.terminal=terminal;
			this.prefix=name().charAt(0);
		}
		
		public String getDisplay() {return display;}
		public boolean isTerminal() {return terminal;}
		
		public boolean isFailed() {return this==JobStatus.FAILED;}
		
		public static JobStatus find(char ch)
		{
			for (JobStatus state : values())
			{
				if (state.prefix.equals(ch))
					return state;
			}
			throw new CException("cannot find job state: "+ch);
		}
	}
	
	@ExportedEnum public enum TrimmomaticAdapters
	{
		NULL(" ", null),
		NexteraPE("NexteraPE-PE"),
		TruSeq2PE("TruSeq2-PE"),
		TruSeq2SE("TruSeq2-SE"),
		TruSeq3PE("TruSeq3-PE"),
		TruSeq3PE2("TruSeq3-PE-2"),
		TruSeq3SE("TruSeq3-SE");
		
		private String label;
		private String filename;
		
		TrimmomaticAdapters(String label, String filename)
		{
			this.label=label;
			this.filename=filename;
		}
		
		TrimmomaticAdapters(String label)
		{
			this(label, label+".fa");
		}
		
		public String getLabel() {return label;}
		public String getFilename() {return filename;}
	}
	
	@ExportedEnum public enum MarsMethod
	{
		hCED(0, "Heuristic Cyclic Edit Distance"),
		Exact(1, "Branch and bound");
		
		private Integer value;
		private String description;
		
		MarsMethod(Integer value, String description)
		{
			this.value=value;
			this.description=description;
		}
		
		public Integer getValue() {return value;}
		public String getDescription() {return description;}
	}
	
	@ExportedEnum public enum ClairModel
	{
		//R94("r94"),
		//R94FLIPFLOP("r94-flipflop");
		ont("ont"),
		pacbio("pacbio"),
		illumina("illumina");
		
		private String value;
		
		ClairModel(String value)
		{
			this.value=value;
		}
		
		public String getValue() {return value;}
	}

	@ExportedEnum public enum LastMode
	{
		DEFAULT("default"),
		WRAP("wrap");
		
		private String value;
		
		LastMode(String value)
		{
			this.value=value;
		}
		
		public String getValue() {return value;}
	}
}