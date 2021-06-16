package org.biobrief.mongo;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.bson.Document;

import com.google.common.collect.Maps;
import com.mongodb.client.gridfs.model.GridFSFile;

import org.biobrief.util.CTable;
import org.biobrief.util.MathHelper;
import org.biobrief.util.StringHelper;

public class FileInfo
{	
	private final String id;
	private final Map<String,String> metadata=Maps.newLinkedHashMap();

	// DECLARATIONS_START
	//protected Integer dbno; //Dbno
	protected String filename; //Filename
	protected String contentType; //Content-type
	protected Integer size; //Size
	protected Date date; //Date
	protected String note; //Note
	protected String username; //Username
	// DECLARATIONS_END
	
	public FileInfo(GridFSFile file)
	{
		System.out.println("GridFSFile()="+StringHelper.toString(file));
		Document meta=file.getMetadata();
		id=file.getId().asObjectId().getValue().toString();
		//dbno=MathHelper.parseInt(meta.get("dbno"));
		note=StringHelper.dflt(meta.get("note"));
		username=StringHelper.dflt(meta.get("username"));
		filename=file.getFilename();
		contentType=StringHelper.dflt(meta.get("type"));//file.getContentType();
		size=(int)file.getLength();// hack! switch to Long
		date=file.getUploadDate();
		for (String name : meta.keySet())
		{
			metadata.put(name, StringHelper.dflt(meta.get(name)));
		}
	}

	public static CTable toTable(Collection<FileInfo> files)
	{
		CTable table=new CTable();
		table.addHeader("id");
		//table.addHeader("dbno");
		table.addHeader("filename");
		table.addHeader("contentType");
		table.addHeader("size");
		table.addHeader("metadata");
		for (FileInfo file : files)
		{
			CTable.Row row=table.addRow();
			row.add(file.getId());
			//row.add(file.getDbno());
			row.add(file.getFilename());
			row.add(file.getContentType());
			row.add(file.getSize());
			row.add(StringHelper.toString(file.getMetadata()));
		}
		return table;
	}
	
	public String getId(){return this.id;}
	//public Integer getDbno(){return this.dbno;}
	public String getFilename(){return this.filename;}
	public String getContentType(){return this.contentType;}
	public Integer getSize(){return size;}// hack! should be Long
	public Date getDate(){return date;}
	public String getNote(){return this.note;}
	public String getUsername(){return this.username;}
	public Map<String,String> getMetadata(){return this.metadata;}
}
