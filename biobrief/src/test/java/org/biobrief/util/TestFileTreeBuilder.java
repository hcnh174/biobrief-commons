package org.biobrief.util;

import java.util.List;

import org.junit.jupiter.api.Test;

//gradle --rerun-tasks --info :hucgc-expertpanel:test --tests *TestFileTreeBuilder
@SuppressWarnings("unused")
public class TestFileTreeBuilder
{
	@Test
	public void execute()
	{
		Context context=new Context();
		FileTreeBuilder builder=new FileTreeBuilder("x:/");
		List<FileHelper.FileInfo> files=builder.build(context);
		System.out.println("builder\n"+JsonHelper.toJson(files));
	}
	
}
