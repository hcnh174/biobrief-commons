package org.biobrief.pipelines;

public class TestUtils
{
	public static final String RESOURCES_DIR="c:/workspace/hlsg/hlsg-pipelines/src/test/resources";
	
//	public static ShirokaneService getShirokaneService()
//	{
//		return new ShirokaneServiceMock(getShirokaneProperties());
//	}

//	public static SshHelper.SshCredentials getCredentials()
//	{
//		String username=RuntimeHelper.getEnvironmentVariable("SLURM_USERNAME");
//		String password=RuntimeHelper.getEnvironmentVariable("SLURM_PASSWORD");
//		String host="10.37.17.153";
//		return SshHelper.getCredentials(username, password, host);
//	}
//	
//	public static NgsProperties getNgsProperties()
//	{
//		NgsProperties properties=new NgsProperties();
//		return properties;
//	}
//
//	public static SlurmService getSlurmService()
//	{
//		return new SlurmServiceWindowsImpl();
//	}
	
//	public static SshHelper.SshCredentials getCredentials()
//	{
//		ShirokaneProperties props=getShirokaneProperties();
//		return SshHelper.getCredentials(props.getUsername(), props.getPassword(), props.getKeyfile(), props.getHost());
//	}
//	
//	public static ShirokaneProperties getShirokaneProperties()
//	{
//		ShirokaneProperties properties=new ShirokaneProperties();
//		properties.setUsername("nhayes");
//		properties.setPassword("R9gyJDeC");
//		properties.setKeyfile("c:/users/nelson/.ssh/shirokane.ppk");
//		properties.setHost("sutil.hgc.jp");
//		return properties;
//	}
	
	public static Pipeline loadPipeline(String name)
	{
		String filename=TestUtils.RESOURCES_DIR+"/pipelines/"+name+".txt";
		String subdir="hbv-nanopore/@pipeline_"+name;
		String dir="/mnt/work/"+subdir;
		String winDir="w:/"+subdir;
		Pipeline pipeline=PipelineParser.parseFile(filename, dir, winDir);
		System.out.println(pipeline.toString());
		return pipeline;
	}
}
