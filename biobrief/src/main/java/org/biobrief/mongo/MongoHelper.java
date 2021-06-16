package org.biobrief.mongo;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.biobrief.util.CException;
import org.biobrief.util.ContentType;
import org.biobrief.util.FileHelper;
import org.biobrief.util.ImageHelper;
import org.biobrief.util.JsonHelper;
//import org.biobrief.util.LoginHelper;
import org.biobrief.util.StringHelper;
//import org.biobrief.util.WebHelper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
//import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBList;
import com.mongodb.client.MongoCollection;

public final class MongoHelper
{	
	private MongoHelper(){}

	public static String newId()
	{
		return new ObjectId().toString();
	}
	
	public static List<String> newIds(int num)
	{
		List<String> ids=Lists.newArrayList();
		for (int index=0; index<num; index++)
		{
			ids.add(MongoHelper.newId());
		}
		return ids;
	}
	
	// wraps a collection around the item(s)
	public static Document wrap(String collection, Document...items)
	{
		BasicDBList list=new BasicDBList();
		for (Document item : items)
		{
			list.add(item);
		}
		return new Document(collection, list);
	}
	
	public static Document getSort(Pageable paging)
	{
		Document sort=new Document();//"uploadDate", 1);
		if (paging.getSort()==null)
			return sort;
		for (Sort.Order order : paging.getSort())
		{
			System.out.println("order="+order);
			Integer dir=order.getDirection()==Direction.DESC ? -1 : 1;
			sort.put(order.getProperty(), dir);
		}
		return sort;
	}
	
	public static Document getQuery(String filter)
	{
		System.out.println("filter="+filter);
		Document query=new Document();
		if (!StringHelper.hasContent(filter))
			return query;
		JsonNode root=JsonHelper.parse(filter);
		//System.out.println("root="+root.asText());
		for (Iterator<JsonNode> iter=root.iterator(); iter.hasNext();)
		{
			JsonNode node=iter.next();
			//System.out.println("node: "+node.toString());
			String field=node.get("field").asText();
			//String op=node.get("op").asText();// TODO ignores op for now
			String value=node.get("value").asText();
			query.put(field, value);
		}
		return query;
	}
	
	public static Binary convertImage(BufferedImage image)
	{
		return convertImage(image, ImageHelper.Format.png);
	}
	
	public static Binary convertImage(BufferedImage image, ImageHelper.Format format)
	{
		return new Binary(ImageHelper.createArray(image, format));
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	
	//http://mongodb.github.io/mongo-java-driver/3.0/bson/extended-json/
	public static void readJson(MongoCollection<Document> collection, String filename)
	{
		BufferedReader reader=null;
		try
		{
			reader=new BufferedReader(new FileReader(filename));
			String json;
			while ((json = reader.readLine()) != null)
			{
				collection.insertOne(Document.parse(json));
			} 
		}
		catch(Exception e)
		{
			throw new CException("problem writing json to "+filename, e);
		}
		finally
		{
			FileHelper.closeReader(reader);
		}
	}
	
	public static void writeJson(MongoCollection<Document> collection, String filename)
	{
		BufferedWriter writer=null;
		try
		{
			JsonWriterSettings settings=JsonWriterSettings.builder().outputMode(JsonMode.SHELL).build();
			writer=new BufferedWriter(new FileWriter(filename));
			for (Document doc : collection.find())
			{
				
				writer.write(doc.toJson(settings));
				//writer.write(doc.toJson(new JsonWriterSettings(JsonMode.SHELL)));
				writer.newLine();
			}
		}
		catch(Exception e)
		{
			throw new CException("problem writing json to "+filename, e);
		}
		finally
		{
			FileHelper.closeWriter(writer);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	
	public static <T extends AbstractNamedMongoEntity> Map<String, T> asNameMap(Collection<T> items)
	{
		Map<String, T> map=Maps.newLinkedHashMap();
		for (T item : items)
		{
			map.put(item.getName(), item);
		}
		return map;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	
	//https://docs.spring.io/spring-data/mongodb/docs/2.1.0.M1/reference/html/#mongo.jsonSchema
	public static void buildSchema()
	{
		
	}
//	MongoJsonSchema.builder()                                                  
//    .required("firstname", "lastname")                                     
//
//    .properties(
//                string("firstname").possibleValues("luke", "han"),         
//
//                object("address")
//                     .properties(string("postCode").minLength(4).maxLength(5)))
//
//    .build();   
	
	/////////////////////////////////////////////////////////////////////////////////
	
	//http://www.mkyong.com/mongodb/spring-data-mongodb-save-binary-file-gridfs-example/
	public static ObjectId saveFile(GridFsTemplate gridFsTemplate, GridFsParams params)
	{
		InputStream inputStream = null;
		try
		{
			inputStream=params.getInputSteam();
			if (StringHelper.hasContent(params.getContentType()))
				return gridFsTemplate.store(inputStream, params.getFilename(), params.getContentType(), params.getMetaData());
			else return gridFsTemplate.store(inputStream, params.getFilename(), params.getMetaData());
		}
		finally
		{
			FileHelper.closeStream(inputStream);
		}
	}
	
	public static class GridFsParams
	{
		private final String filename;
		private String contentType;
		private InputStream inputStream;
		private Document metaData=new Document();
	
//		public GridFsParams(String path)
//		{
//			this.filename=FileHelper.stripPath(path);
//			this.inputStream=FileHelper.openFileInputStream(path);
//			this.contentType=ContentType.findByFilename(filename).getMimeType();
//		}
		
		public GridFsParams(String path)
		{
			BufferedImage image=ImageHelper.readImage(path);
			this.filename=FileHelper.stripPath(path);
			this.inputStream=ImageHelper.createInputStream(image, ImageHelper.Format.find(filename));
			this.contentType=ContentType.findByFilename(filename).getMimeType();
		}
		
		public GridFsParams(BufferedImage image, String filename)
		{
			this.filename=filename;
			this.inputStream=ImageHelper.createInputStream(image, ImageHelper.Format.find(filename));
			this.contentType=ContentType.findByFilename(filename).getMimeType();
		}
		
		public void setMetaData(String name, Object value)
		{
			if (StringHelper.hasContent(value))
				metaData.append(name, value);
		}
		
		public void setMetaData(Map<String, Object> map)
		{
			for (String name : map.keySet())
			{
				setMetaData(name, map.get(name));
			}
		}
		
		public void setMetaData(Object...args)
		{
			setMetaData(StringHelper.createMap(args));
		}
		
		public boolean hasContentType()
		{
			return StringHelper.hasContent(contentType);
		}
		
		public InputStream getInputSteam(){return inputStream;}
		public Document getMetaData(){return this.metaData;}
		public String getFilename(){return this.filename;}
		public String getContentType(){return this.contentType;}
		public void setContentType(final String contentType){this.contentType=contentType;}

	}
}
