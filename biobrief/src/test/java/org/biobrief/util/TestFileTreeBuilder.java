package org.biobrief.util;

import java.util.List;

import org.junit.jupiter.api.Test;

//gradle --rerun-tasks --info test --tests *TestFileTreeBuilder
public class TestFileTreeBuilder
{
	@Test
	public void execute()
	{
		String dir="x:/A610714338024_GenMineTOP";
		//String dir="x:/";
		Context context=new Context();
		FileTreeBuilder builder=new FileTreeBuilder(dir);
		List<FileHelper.FileInfo> files=builder.build(context);
		System.out.println("builder\n"+JsonHelper.toJson(files));
	}
	
}
