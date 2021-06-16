package org.biobrief.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BashUtil
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(BashUtil.class);
	
	public static String joinCommands(List<String> commands)
	{
		return StringHelper.join(commands, ";\\\n");
	}
	
	public static String concatCommands(List<String> commands)
	{
		return StringHelper.join(commands, "\n");
	}
	
	public static String linkFile(String infile, String outfile)
	{
		return "ln -s "+infile+" "+outfile;
	}
	
	public static String concatFiles(List<String> files, String outfile)
	{
		return "cat "+ StringHelper.join(files, " ")+" > "+outfile;
	}
	
	public static String linkOrConcatFiles(List<String> fastqfiles, String outfile)//String fastqdir, 
	{
		if (fastqfiles.size()==1)
			return linkFile(fastqfiles.get(0), outfile);
		else return concatFiles(fastqfiles, outfile);
	}

	public static boolean isGzipped(String filename)
	{
		return filename.endsWith(".gz");
	}
	
	/*
	cat > /home/nhayes/out/projects/tsuge-hbv-gt/functions.sh << "EOF"
	source /home/nhayes/out/projects/functions.sh
	echo "functions for project tsuge-hbv-gt"
	EOF
	 */
	public static String createFile(String filename, String commands)
	{
		String str="cat > "+filename+" << \"EOF\"\n";
		str+=commands+"\n";
		str+="EOF\n";
		return str;
	}

	public static String makeDir(String dir)
	{
		return "mkdir -p "+dir;
	}
}
