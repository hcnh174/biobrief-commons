package org.biobrief.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

//gradle  --stacktrace --info :biobrief-util:test --tests *TestKafkaHelper
public class TestKafkaHelper
{
	@Test
	public void getPayload()
	{
		String message="{\"schema\":{\"type\":\"string\",\"optional\":false},\"payload\":\"type=JOB;id=5c19bc20c1c6891ed0d3c52f;status=started;code=0;statusfile=/mnt/out/slurm/4208.started\"}";
		String payload="type=JOB;id=5c19bc20c1c6891ed0d3c52f;status=started;code=0;statusfile=/mnt/out/slurm/4208.started";
		String payload2=KafkaHelper.getPayload(message);
		assertThat(payload2).isEqualTo(payload);
	}
}