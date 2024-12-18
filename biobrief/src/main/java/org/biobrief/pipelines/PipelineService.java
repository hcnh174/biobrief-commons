package org.biobrief.pipelines;

import org.biobrief.pipelines.util.SnakemakeHelper;
import org.biobrief.services.FileService;
import org.biobrief.util.FileHelper;
import org.biobrief.util.JsonHelper;
import org.biobrief.util.MessageWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PipelineService
{
	@SuppressWarnings("unused") @Autowired private PipelineProperties properties;
	@Autowired private FileService fileService;
	//@Autowired private SlurmService slurmService;

	//snakemake --snakefile test-hbv-nanopore.smk -r --dry-run
	//snakemake --snakefile test-hbv-nanopore.smk -j 32 --latency-wait 60 --cluster "sbatch --ntasks-per-node=1 --exclusive"
//	public void runPipeline(String snakefile, boolean dryrun, MessageWriter out)
//	{
//		Pipeline pipeline=writeSnakemakePipeline(snakefile, out);
//		deleteDoneFile(snakefile);
//		String command=SnakemakeHelper.createCommand(snakefile, dryrun);
//		out.println("command="+command);
//		slurmService.execute(command);
//	}
//	
//	private void deleteDoneFile(String snakefile)
//	{
//		String dir=FileHelper.getDirFromFilename(snakefile);
//		String filename=dir+"/.done";
//		if (FileHelper.exists(filename))
//			FileHelper.deleteFile(filename);
//	}
	
	public Pipeline writeSnakemakePipeline(String snakefile, MessageWriter out)
	{
		Pipeline pipeline=loadPipeline(snakefile);		
		SnakemakeHelper.Script script=SnakemakePipelineBuilder.build(pipeline);
		String formatted=script.format();
		out.println(formatted);
		//String snakefile=pipeline.getWinDir()+"/"+pipeline.getName()+".smk";
		FileHelper.writeFile(snakefile, formatted, true);
		return pipeline;
	}
	
	public Pipeline writeBashPipeline(String shellfile, MessageWriter out)
	{
		Pipeline pipeline=loadPipeline(shellfile);		
		String script=ShellScriptPipelineBuilder.build(pipeline);
		out.println(script);
		FileHelper.writeFile(fileService.convertPath(shellfile), script, true);
		return pipeline;
	}
	
	public Pipeline loadPipeline(String snakefile)
	{
		String dir=FileHelper.getDirFromFilename(snakefile);
		String name=FileHelper.stripExtension(FileHelper.stripPath(snakefile));
		String filename=PipelineConstants.PIPELINE_DIR+"/ngs/pipelines/"+name+".txt";
		String winDir=fileService.convertPath(dir);
		Pipeline pipeline=PipelineParser.parseFile(filename, dir, winDir);
		System.out.println(pipeline.toString());
		return pipeline;
	}
	
	public PipelineTree writePipelineTree(String snakefile)
	{
//		String dir=FileHelper.getDirFromFilename(snakefile);
//		String command="du -a "+dir+" > "+dir+"/files.txt";
//		slurmService.execute(command);
		Pipeline pipeline=loadPipeline(snakefile);
		PipelineTree tree=pipeline.createTree();
		System.out.println("tree="+JsonHelper.toJson(tree));
		String htmlfile=fileService.convertPath(tree.getHtmlFile());
		FileHelper.writeFile(htmlfile, tree.getHtml(), true);
		return tree;
	}
}
