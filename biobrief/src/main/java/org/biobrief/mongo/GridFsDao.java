package org.biobrief.mongo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.biobrief.mongo.MongoHelper.GridFsParams;
import org.biobrief.util.CException;
import org.biobrief.util.ContentType;
import org.biobrief.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;

@Repository
//db['fs.files'].find({"metadata.dbno": 12}).limit(10).sort({"uploadDate": 1})
//http://mongodb.github.io/mongo-java-driver/3.2/driver/reference/gridfs/
public class GridFsDao
{	
	private static final String FS_FILES="fs.files";
	
	@Autowired private MongoTemplate mongoTemplate;
	@Autowired private MongoConverter mongoConverter;
	
	public List<FileInfo> findAll()
	{
		return findAll(new Query());
	}
	
	//http://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/gridfs/GridFsCriteria.html
	//http://stackoverflow.com/questions/19580501/create-a-list-from-dbcursor-in-mongodb-and-java
	public Page<FileInfo> findAll(Pageable paging)
	{
		return findAll(new Document(), paging);
	}
	
	public Page<FileInfo> findAll(Bson query, Pageable paging)
	{
		long total=lookupTotal(query);
		List<String> ids=lookupFiles(query, paging);
		System.out.println("total="+total+", ids="+StringHelper.join(ids));
		List<FileInfo> list=findAll(ids);
		return new PageImpl<FileInfo>(list, paging, total);
	}

	private Long lookupTotal(Bson query)
	{
		return mongoTemplate.getCollection(FS_FILES).countDocuments(query);
	}

	private List<String> lookupFiles(Bson query, Pageable paging)
	{
		Document sort=MongoHelper.getSort(paging);
		System.out.println("query="+query.toString());
		System.out.println("paging="+paging.toString());
		System.out.println("sort="+sort.toString());
		MongoCollection<Document> collection=mongoTemplate.getCollection(FS_FILES);
		FindIterable<Document> cursor=collection
				.find(query)
				.skip((int)paging.getOffset())
				.limit(paging.getPageSize())
				.sort(sort);
		Iterator<Document> iter=cursor.iterator();
		List<String> ids=Lists.newArrayList();
		while (iter.hasNext())
		{
			Document document=iter.next();
			ObjectId id=(ObjectId)document.get("_id");
			ids.add(id.toString());
		}
		return ids;
	}

	private List<FileInfo> findAll(List<String> ids)
	{
		Query query=new Query().addCriteria(GridFsCriteria.where("_id").in(ids));
		Map<String,FileInfo> map=Maps.newLinkedHashMap();
		for (FileInfo fileinfo : findAll(query))
		{
			System.out.println("found fileinfo: "+StringHelper.toString(fileinfo));
			map.put(fileinfo.getId(), fileinfo);
		}
		System.out.println("map:"+StringHelper.toString(map));
		//return Lists.newArrayList(map.values()); //TODO not sorted
		if (ids.size()!=map.size())
			throw new CException("fileinfo lookup: ids.size()!=map.size(): "+ids.size()+" vs "+map.size());
		List<FileInfo> list=Lists.newArrayList();
		for (String id : ids)
		{
			if (!map.containsKey(id))
				throw new CException("cannot find fileinfo with id "+id);
			list.add(map.get(id));
		}
		return list;
	}
	
//	public Page<FileInfo> findAll(Pageable paging)
//	{
//		//List<FileInfo> list=findAll(new Query());
//		//return new PageImpl<FileInfo>(list, paging, list.size());
//	}
	/*
	//http://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/gridfs/GridFsCriteria.html
	public Page<FileInfo> findAll(Pageable paging)
	{
		//Page<GridFsDocument> page=gridFsRepository.findAll(paging);
		List<String> ids=Lists.newArrayList();
		for (GridFsDocument document : page.getContent())
		{
			ids.add(document.getId());
		}
		List<FileInfo> list=findAll(new Query().addCriteria(GridFsCriteria.where("_id").in(ids)));
		return new PageImpl<FileInfo>(list, paging, page.getTotalElements());
	}
	*/
	
	public List<FileInfo> findByPatientId(String patientId)
	{
		Query query=new Query().addCriteria(GridFsCriteria.whereMetaData("patientId").is(patientId));
		return findAll(query);
	}
	
	public List<FileInfo> findByDbno(Integer dbno)
	{
		Query query=new Query().addCriteria(GridFsCriteria.whereMetaData("dbno").is(dbno));
		return findAll(query);
	}
	
	//https://docs.mongodb.com/manual/core/gridfs/
	public List<FileInfo> findAll(Query query)
	{
		List<FileInfo> list=Lists.newArrayList();
		for (GridFSFile file : getGridFsTemplate().find(query))
		{
			System.out.println("found file: "+file.getFilename());
			list.add(new FileInfo(file));
		}
		return list;
	}
	
	public FileInfo findById(String id)
	{
		Query query=new Query().addCriteria(GridFsCriteria.where("_id").is(id));
		GridFSFile file=getGridFsTemplate().findOne(query);
		if (file==null)
			throw new CException("cannot find file: "+id);
		return new FileInfo(file);
	}
	
	public FileInfo findByFilename(String filename)
	{
		Query query=new Query().addCriteria(GridFsCriteria.whereFilename().is(filename));
		GridFSFile file=getGridFsTemplate().findOne(query);
		if (file==null)
			throw new CException("cannot find file: "+filename);
		return new FileInfo(file);
	}
	
	public void delete(Query query)
	{
		getGridFsTemplate().delete(query);
	}
	
	////////////////////////////////////////////////////
	
	//@SuppressWarnings("deprecation")
	private GridFsTemplate getGridFsTemplate()
	{
		//System.out.println("getGridFsTemplate()");
		return new GridFsTemplate(mongoTemplate.getMongoDatabaseFactory(), mongoConverter);
		//return new GridFsTemplate(mongoDbFactory, mongoConverter);
	}
	
	/////////////////////////////////////////////////////////////////////
	
	public ObjectId save(String filename, Integer dbno)
	{
		GridFsParams params=new GridFsParams(filename);
		params.setContentType(ContentType.findByFilename(filename).getMimeType());
		params.setMetaData("dbno", dbno);
		return save(params);
	}
	
//	public ObjectId save(MultipartFile file)
//	{
//		Integer dbno=IoUtils.extractDbnoFromUploadFilename(file.getOriginalFilename());
//		GridFsParams params=new GridFsParams(file);
//		params.setMetaData("dbno", dbno);
//		return save(params);
//	}
//	
//	public ObjectId save(MultipartFile file, Integer dbno, String note)
//	{
//		GridFsParams params=new GridFsParams(file);
//		params.setMetaData("dbno", dbno);
//		params.setMetaData("note", note);
//		return save(params);
//	}
	
	public ObjectId save(GridFsParams params)
	{
		//params.setMetaData("username", LoginHelper.getUsername());
		return MongoHelper.saveFile(getGridFsTemplate(), params);
	}
	
	/////////////////////////////////////////////////////
	
//	public void importScans()
//	{
//		for (String dir : env.getScanDirs())
//		{
//			importScans(dir);
//		}
//	}
//	
//	public void importScans(String dir)
//	{
//		for (String filename : FileHelper.listFilesRecursively(dir, ".pdf", true))
//		{
//			importScan(filename);
//		}
//	}
//	
//	public void importScan(String filename)
//	{
//		System.out.println("Importing file: "+filename);
//		Integer dbno=IoUtils.extractDbnoFromScanFilename(filename);
//		if (dbno==null)
//			return;
//		GridFsParams params=new GridFsParams(filename);
//		params.setContentType(ContentType.PDF);
//		params.setMetaData("dbno", dbno);
//		params.setMetaData("folder", FileHelper.getParentDir(filename));
//		save(params);
//	}
//	
//	///////////////////////////////////////////////////////////
//	
//	public void importBiopsies()
//	{
//		String dir=env.getBiopsyDir();
//		for (String filename : FileHelper.listFilesRecursively(dir, ".jpg", true))
//		{
//			importBiopsy(filename);
//		}
//	}
//	
//	public void importBiopsy(String filename)
//	{
//		Integer dbno=IoUtils.extractDbnoFromBiopsyFilename(filename);
//		if (dbno==null)
//			return;
//		Integer filemakerNo=IoUtils.extractFilemakerNoFromBiopsyFilename(filename);
//		GridFsParams params=new GridFsParams(filename);
//		params.setContentType(ContentType.JPEG);
//		params.setMetaData("dbno", dbno);
//		params.setMetaData("filemakerNo", filemakerNo);
//		params.setMetaData("folder", FileHelper.getParentDir(filename));
//		save(params);
//	}
	
	////////////////////////////////////////////////////////////////
//	
	//http://mongodb.github.io/mongo-java-driver/3.3/driver/reference/gridfs/
	public void download(String id, OutputStream stream)
	{
		MongoDatabase database=mongoTemplate.getDb();
		GridFSBucket bucket=GridFSBuckets.create(database);
		bucket.downloadToStream(new ObjectId(id), stream);
	}
	
	public void downloadToFile(String id, String filename)
	{
		try
		{
			FileOutputStream stream = new FileOutputStream(filename);
			//MongoDatabase database=mongoTemplate.getMongoDbFactory().getMongoDatabase();//.getDb();
			MongoDatabase database=mongoTemplate.getDb();
			GridFSBucket bucket=GridFSBuckets.create(database);
			bucket.downloadToStream(new ObjectId(id), stream);
			stream.close();
			System.out.println(stream.toString());
		}
		catch (Exception e)
		{
			throw new CException(e);
		}
	}
	
	//https://sourceforge.net/p/tess4j/discussion/1202293/thread/dda68fe1e6/
	//https://api.mongodb.com/java/3.2/com/mongodb/client/gridfs/GridFSBucket.html#find-org.bson.conversions.Bson-
	//https://stackoverflow.com/questions/12705385/how-to-convert-a-byte-to-a-bufferedimage-in-java/24381183
	public BufferedImage downloadImage(String id)
	{
		try
		{
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			MongoDatabase database=mongoTemplate.getDb();
			GridFSBucket bucket=GridFSBuckets.create(database);
			//GridFSFile gridFSFile = bucket.find(Filters.eq("_id", id)).first();
			bucket.downloadToStream(new ObjectId(id), outstream);
			byte[] bytes=outstream.toByteArray();
			outstream.close();
			ByteArrayInputStream instream = new ByteArrayInputStream(bytes);
			BufferedImage image=ImageIO.read(instream);
			if (image==null)
				throw new CException("GridFS image is null: "+id);
			return image;
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}
}