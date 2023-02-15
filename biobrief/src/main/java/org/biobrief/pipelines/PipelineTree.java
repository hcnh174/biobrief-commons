package org.biobrief.pipelines;

import java.util.List;
import java.util.Map;

import org.biobrief.pipelines.PipelineConstants.JobStatus;
import org.biobrief.pipelines.PipelineConstants.TaskType;
import org.biobrief.util.CTable;
import org.biobrief.util.FileHelper;
import org.biobrief.util.StringHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Data;

public class PipelineTree
{
	//private static final String SPACE=" ";
	private static final String TAB="\t";
	private static final String NEWLINE="\n";
	
	protected final Pipeline pipeline;
	protected final Node root;
	protected final List<String> files;
	
	public PipelineTree(Pipeline pipeline)
	{		
		this.pipeline=pipeline;
		this.root=new PipelineTree.Node(pipeline);
		pipeline.createTreeChildren(root);
		this.files=parseFileList();
		this.update();
	}
	
	public Node getRoot() {return root;}
	
	@JsonIgnore
	public CTable getTable()
	{
		CTable table=new CTable();
		table.setCellpadding(2);
		table.getHeader().add(bold("Task"));
		table.getHeader().add(bold("Status"));
		for (String sample : pipeline.getSamples())
		{
			table.getHeader().add(bold(sample));
		}
		addRow(root, table);
		return table;
	}
 
	private void addRow(Node node, CTable table)
	{
		if (node.getType()!=TaskType.PIPELINE)
		{
			CTable.Row row=table.addRow();
			row.add(getLabel(node)).setStyle("white-space: nowrap;");
			row.add(getUrl(format(node.getStatus()), node.getDir())).setStyle(getStyle(node.getStatus()));
			for (String sample : pipeline.getSamples())
			{
				String dir=node.getDir(sample);
				JobStatus status=getJobStatus(dir);
				row.add(getUrl(format(status), dir)).setStyle(getStyle(status));
			}
		}
		for (Node child : node.getNodes())
		{
			addRow(child, table);
		}
	}
	
	@JsonIgnore
	public String getHtml()
	{
		CTable table=getTable();
		return table.toHtml();
	}
	
	public String getHtmlFile()
	{
		String snakefile=pipeline.getSnakefile();
		String dir=FileHelper.stripFilename(snakefile);
		String name=FileHelper.stripExtension(FileHelper.stripPath(snakefile));
		return dir+name+".html";
	}

	@JsonIgnore
	public String getText()
	{
		StringBuilder buffer=new StringBuilder();
		buffer.append(pad("Task")+TAB+"Status");
		for (String sample : pipeline.getSamples())
		{
			buffer.append(TAB+sample);
		}
		buffer.append(NEWLINE);
		addRow(root, buffer);
		return buffer.toString();
	}
	
	private void addRow(Node node, StringBuilder buffer)
	{
		if (node.getType()!=TaskType.PIPELINE)
		{
			buffer.append(pad(StringHelper.repeatString("-", node.getDepth())+node.getName()));////getLabel(node)).append(TAB); //StringHelper.repeatString("--", node.getDepth()*2)
			buffer.append(TAB+format(node.getStatus()));
			for (String sample : pipeline.getSamples())
			{
				String dir=node.getDir(sample);
				JobStatus status=getJobStatus(dir);
				buffer.append(TAB+format(status));
			}
			buffer.append(NEWLINE);
		}
		for (Node child : node.getNodes())
		{
			addRow(child, buffer);
		}
	}
	
	private String getLabel(Node node)
	{
		return getIndent(node)+node.getName();
		//return getIndent(node)+"<a href=\""+node.getName()+"\">"+node.getName()+"</a>";
	}
	
	private String getIndent(Node node)
	{
		return "<span style=\"color: lightgrey\">"+StringHelper.repeatString("--", node.getDepth()*2)+"</span>";
	}
	
	private String getUrl(String text, String url)
	{
		return "<a href=\""+convertPath(url)+"\">"+text+"</a>";
	}
	
	private String bold(String value)
	{
		return "<span style=\"font-weight: bold\">"+value+"</span>";
	}
	
	private String pad(String value)
	{
		return StringHelper.padRight(value, ' ', 25);
	}
	
	private String format(JobStatus status)
	{
		switch(status)
		{
		case PENDING:
			return "P";
		case RUNNING:
			return "R";
		case FAILED:
			return "X";
		case COMPLETED:
			return "C";
		default:
			return "";
		}
	}
	
	private String getStyle(JobStatus status)
	{
		String style="text-align: center; ";
		switch(status)
		{
		case PENDING:
			return style+"background-color: #dfe4ff";//white";
		case RUNNING:
			return style+"background-color: #f7f780";//yellow";
		case FAILED:
			return style+"background-color: #fbcbc7";//red";
		case COMPLETED:
			return style+"background-color: #a1f7a1";//green";
		default:
			return style+"background-color: whitesmoke";//lightgrey";
		}
	}
	
	public void update()
	{
		update(root);
	}
	
	private List<String> parseFileList()
	{
		String filename=convertPath(root.getDir()+"/files.txt");
		List<String> lines=FileHelper.readLines(filename);
		List<String> files=Lists.newArrayList();
		for (String line : lines)
		{
			List<String> tabs=StringHelper.splitTabs(line);
			String file=tabs.get(1);
			files.add(file);
		}
		System.out.println("files: "+StringHelper.join(files, "\n"));
		return files;
	}
	
	private void update(Node node)
	{
		node.status=getJobStatus(node.getDir());
		for (String sample : pipeline.getSamples())
		{
			node.samples.put(sample, getJobStatus(node.getDir(sample)));
		}
		for (Node child : node.getNodes())
		{
			update(child);
		}
	}
	
	private boolean fileExists(String filename)
	{
		//filename=StringHelper.replace(filename, pipeline.getDir(), "");
		System.out.println("checking file: "+filename);
		return files.contains(filename);
	}
	
	private JobStatus getJobStatus(String dir)
	{
		//dir=convertPath(dir);
		JobStatus status=JobStatus.PENDING;
		if (fileExists(dir+"/.log"))
			status=JobStatus.RUNNING;
		if (fileExists(dir+"/.done"))
			status=JobStatus.COMPLETED;
		System.out.println("status for dir="+dir+": "+status);
		return status;
	}
	
	private static String convertPath(String dir)
	{
		return StringHelper.replace(dir, "/mnt/out", "o:");
	}
	
	@Data
	public static class Node
	{
		protected final TaskType type;
		protected final String name;
		protected final Integer depth;
		protected final String dir;
		protected final List<Node> nodes=Lists.newArrayList();
		protected final Map<String, JobStatus> samples=Maps.newLinkedHashMap();
		protected JobStatus status=JobStatus.PENDING;
		
		public Node(Pipeline pipeline)
		{
			this.type=TaskType.PIPELINE;
			this.name=pipeline.getName();
			this.depth=0;
			this.dir=pipeline.getDir();
		}
		
		public Node(PipelineNode task)
		{
			this.type=task.getType();
			this.name=task.getName();
			this.depth=task.getLevel();
			this.dir=task.getDir();
		}
		
		public void add(Node node)
		{
			nodes.add(node);
		}
		
		public JobStatus getStatus(String sample)
		{
			if (samples.containsKey(sample))
				return samples.get(sample);
			else return JobStatus.NULL;
		}
		
		public String getDir(String sample)
		{
			return dir+"/"+sample;
		}
		
//		public Optional<AbstractResult> findResult(String sample)
//		{
//			for (AbstractResult result : results)
//			{
//				if (result.getSample().equals(sample))
//					return Optional.of(result);
//			}
//			return Optional.empty();
//		}
	}
}
