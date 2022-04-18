package org.biobrief.solr;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.json.JsonQueryRequest;
import org.apache.solr.client.solrj.request.json.TermsFacetMap;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest.MultiUpdate;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.util.NamedList;
import org.biobrief.util.CException;
import org.biobrief.util.JsonHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

import lombok.Data;

public class SolrHelper
{
	public static SolrClient getSolrClient()
	{
		String solrUrl = "http://1703-030.b.hiroshima-u.ac.jp:8983/solr";
		return new HttpSolrClient.Builder(solrUrl)
			.withConnectionTimeout(10000)
			.withSocketTimeout(60000)
			.build();
	}
	
	public static void deleteAll(SolrClient client, String collection)
	{
		try
		{
			client.deleteByQuery(collection,  "*:*" );
			client.commit(collection);
		}
		catch(SolrServerException e)
		{
			throw new CException(e);
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}
	
	@SuppressWarnings("unused")
	public static void indexDocuments(SolrClient client, List<?> documents, String collection)
	{
		try
		{
			for (Object document : documents)
			{
				UpdateResponse response = client.addBean(collection, document);
			}
			client.commit(collection);
		}
		catch(SolrServerException e)
		{
			throw new CException(e);
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}
	
	public static <T> List<T> search(SolrClient client, String collection, String term, Class<T> cls)
	{
		try
		{
			SolrQuery query = new SolrQuery(term);
			query.setSort("id", ORDER.asc);
			QueryResponse response = client.query(collection, query);
			List<T> documents = response.getBeans(cls);
			return documents;
		}
		catch(SolrServerException e)
		{
			throw new CException(e);
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void findLiveNodes(SolrClient client)
	{
		try
		{
			final SolrRequest<?> request = new CollectionAdminRequest.ClusterStatus();
			final NamedList<Object> response = client.request(request);
			final NamedList<Object> cluster = (NamedList<Object>) response.get("cluster");
			final List<String> liveNodes = (List<String>) cluster.get("live_nodes");
			System.out.println("Found " + liveNodes.size() + " live nodes");
		}
		catch(SolrServerException e)
		{
			throw new CException(e);
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}
	
	//https://lucene.apache.org/solr/8_1_0//solr-solrj/org/apache/solr/client/solrj/request/schema/SchemaRequest.AddField.html
	//https://lucene.apache.org/solr/8_1_0//solr-solrj/org/apache/solr/client/solrj/SolrClient.html
	//https://lucene.apache.org/solr/8_1_0//solr-solrj/org/apache/solr/client/solrj/request/schema/SchemaRequest.MultiUpdate.html#%3Cinit%3E(java.util.List)
	public static void updateSchema(SolrClient client, String collection)
	{
		try
		{
			List<SchemaRequest.Update> updates=Lists.newArrayList();
			final SolrRequest<?> request=new MultiUpdate(updates);
			client.request(request);//final NamedList<Object> response = 
		}
		catch(SolrServerException e)
		{
			throw new CException(e);
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}
	
	//https://lucene.apache.org/solr/guide/8_4/json-facet-api.html
//	public static QueryResponse getTermFacet(SolrClient client, String collection, String field)
//	{
//		try
//		{
//			final TermsFacetMap facet = new TermsFacetMap(field);//.setLimit(3);
//			final JsonQueryRequest request = new JsonQueryRequest()
//				.setQuery("*:*")
//				.setLimit(0)
//				.withFacet(field, facet);
//			QueryResponse response = request.process(client, collection);
//			return response;
//			//response.
//		}
//		catch(SolrServerException e)
//		{
//			throw new CException(e);
//		}
//		catch(IOException e)
//		{
//			throw new CException(e);
//		}
//	}
	
	
	//https://lucene.apache.org/solr/guide/8_4/json-facet-api.html
	//getTermFacet("nctclinicaltrials", "*:*", Arrays.asList("genes", "conditions", "interventions", "phase")_;
	public static Facets getTermFacet(SolrClient client, String collection, String query, List<String> fields)
	{
		try
		{			
			JsonQueryRequest request = new JsonQueryRequest().setQuery(query).setLimit(0);
			for (String field : fields)
			{
				request = request.withFacet(field, new TermsFacetMap(field));
			}
			QueryResponse response = request.process(client, collection);
			String json=response.jsonStr();
			//FileHelper.writeFile(Constants.TMP_DIR+"/solr/facets.json", json);
			
			Facets facets=new Facets();
			
			JsonNode rootNode=JsonHelper.parse(json);
			JsonNode facetsNode=rootNode.path("facets");
			Iterator<Entry<String,JsonNode>> iter=facetsNode.fields();
			while (iter.hasNext())
			{
				Entry<String,JsonNode> entry=iter.next();
				if (entry.getKey().equals("count"))
					facets.setCount(entry.getValue().asLong());
				else
				{
					Facets.TermFacet facet=facets.addTermFacet(entry.getKey());
					JsonNode facetNode=entry.getValue();
					Iterator<JsonNode> bucketIter=facetNode.get("buckets").elements();
					while (bucketIter.hasNext())
					{
						JsonNode bucketNode=bucketIter.next();
						facet.add(bucketNode.get("val").asText(), bucketNode.get("count").asLong());
					}
				}
			}
			return facets;
		}
		catch(SolrServerException e)
		{
			throw new CException(e);
		}
		catch(IOException e)
		{
			throw new CException(e);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Data
	public static class Facets
	{
		private Long count;
		private List<TermFacet> facets=Lists.newArrayList();
		
		public TermFacet addTermFacet(String name)
		{
			TermFacet facet=new TermFacet(name);
			this.facets.add(facet);
			return facet;
		}
		
		@Data
		public static class TermFacet
		{
			private String name;
			private List<Bucket> buckets=Lists.newArrayList();
			
			public TermFacet() {}
			
			public TermFacet(String name)
			{
				this.name=name;
			}
			
			public Bucket add(String name, Long count)
			{
				Bucket bucket=new Bucket(name, count);
				this.buckets.add(bucket);
				return bucket;
			}
		}
		
		@Data
		public static class Bucket
		{
			private String name;
			private Long count;
			
			public Bucket() {}
			
			public Bucket(String name, Long count)
			{
				this.name=name;
				this.count=count;
			}
		}
	}
}
