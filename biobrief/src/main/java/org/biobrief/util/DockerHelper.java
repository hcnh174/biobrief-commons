package org.biobrief.util;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

public class DockerHelper
{
	public static DockerCommand createDockerCommand(String container, Volumes volumes, String command)
	{
		//docker run -v /mnt/refdata/ref/hg19:/anno -v /mnt/refdata/ref/hg19:/ref zhouwanding/transvar:latest ...
		DockerCommand docker=new DockerCommand(container, volumes, command);
		return docker;
	}
	
	@Data
	public static class DockerCommand
	{
		protected String container;
		protected Volumes volumes;
		protected String command;
		
		public DockerCommand(String container, Volumes volumes, String command)
		{
			this.container=container;
			this.volumes=volumes;
			this.command=command;
			
			if (!StringHelper.hasContent(container))
				throw new CException("container is not set");
			if (!StringHelper.hasContent(command))
				throw new CException("command is not set");
		}
		
		public String format()
		{
			return "docker run"+volumes.format()+" "+container+" "+command;
		}
	}
	
	@Data
	public static class Volumes
	{
		protected List<Volume> volumes=Lists.newArrayList();		

		public void addMapping(String dockerPath, String localPath)
		{
			for (Volume volume : volumes)
			{
				if (volume.getDockerPath().equals(dockerPath))
					throw new CException("docker volume already registered: "+dockerPath +" local="+localPath);
			}
			volumes.add(new Volume(dockerPath, localPath));
		}
		
		public String format()
		{
			List<String> list=Lists.newArrayList();
			for (Volume volume : volumes)
			{
				list.add(volume.format());
			}
			return " "+StringHelper.join(list, " ");
		}
	}
	
	@Data
	public static class Volume
	{
		protected String dockerPath;
		protected String localPath;
		
		public Volume(String dockerPath, String localPath)
		{
			this.dockerPath=dockerPath;
			this.localPath=localPath;
		}
		
		public String format()
		{
			return "-v "+localPath+":"+dockerPath;
		}
	}
}