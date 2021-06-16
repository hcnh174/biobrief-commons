package org.biobrief.util;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public class KafkaHelper
{
	////{"schema":{"type":"string","optional":false},"payload":"type=JOB;id=5c19bc20c1c6891ed0d3c52f;status=started;code=0;statusfile=/mnt/out/slurm/4208.started"}
	public static String getPayload(String message)
	{
		int start=message.indexOf("payload");
		if (start==-1)
			throw new CException("cannot find payload field in Kafka message: "+message);
		int end=message.lastIndexOf("\"");
		return message.substring(start+10, end);
	}
	
	public static Map<String, String> parseMessage(String message)
	{
		System.out.println("kafka message: "+message);
		Map<String, String> params=Maps.newLinkedHashMap();
		List<String> pairs=StringHelper.split(message, ";");
		for (String pair : pairs)
		{
			if (!pair.contains("="))
				throw new CException("cannot parse kafka message pair: "+pair+" message="+message);
			String[] arr=pair.split("=");
			if (arr.length!=2)
				throw new CException("cannot parse kafka message pair: "+pair+" message="+message);
			params.put(arr[0], arr[1]);
		}
		return params;
	}
}