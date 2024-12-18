package org.biobrief.services;

import org.biobrief.util.FileHelper;
import org.biobrief.util.JsonHelper;
import org.biobrief.util.MessageWriter;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Service @Data @EqualsAndHashCode(callSuper=false)
public class FileCacheService<T> extends AbstractFileCacheService
{
	protected Class<T> cls;
	
	public FileCacheService(String cacheDir, Integer maxAge, Class<T> cls)
	{
		super(cacheDir, 0l, maxAge, ".json");
		this.cls=cls;
	}

	public T read(String key, MessageWriter out)
	{
		String json=super.getValue(key, out);
		return parse(json);
	}
	
	public void write(String key, T value, MessageWriter out)
	{
		String filename=getFilename(key);
		FileHelper.writeFile(filename, format(value), true);
	}

	///////////////////////////////////
	
	@SuppressWarnings("unchecked")
	private T parse(String json)
	{
		return (T)JsonHelper.parse(json, cls);
	}
	
	private String format(T obj)
	{
		return JsonHelper.toJson(obj);
	}
}
