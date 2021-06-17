package org.biobrief.util;

import org.biobrief.util.DockerHelper.DockerCommand;
import org.biobrief.util.DockerHelper.Volumes;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

//gradle --stacktrace --info test --tests *TestDockerHelper
public class TestDockerHelper
{	
	@Test
	public void testCommand()
	{
		//docker run -v /mnt/refdata/ref/hg19:/anno -v /mnt/refdata/ref/hg19:/ref zhouwanding/transvar:latest transvar panno -i 'AATK:p.P1331_A1332insTP' --ccds --reference /ref/hg19.fa
		Volumes volumes=new Volumes();
		volumes.addMapping("/anno", "/mnt/refdata/ref/hg19");
		volumes.addMapping("/ref", "/mnt/refdata/ref/hg19");
		
		DockerCommand docker=new DockerCommand("zhouwanding/transvar:latest", volumes, "transvar panno -i 'AATK:p.P1331_A1332insTP' --ccds --reference /ref/hg19.fa");
		System.out.println("docker command="+docker.format());
		
		assertThat(docker.format()).isEqualTo("docker run -v /mnt/refdata/ref/hg19:/anno -v /mnt/refdata/ref/hg19:/ref zhouwanding/transvar:latest transvar panno -i 'AATK:p.P1331_A1332insTP' --ccds --reference /ref/hg19.fa");
	}
}