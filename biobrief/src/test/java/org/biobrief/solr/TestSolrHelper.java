package org.biobrief.solr;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.biobrief.solr.SolrHelper.Facets;
import org.biobrief.util.CException;
import org.biobrief.util.JsonHelper;
import org.biobrief.util.StringHelper;
import org.junit.jupiter.api.Test;

//https://lucene.apache.org/solr/guide/8_1/using-solrj.html
//gradle --stacktrace --info :biobrief-solr:test --tests *TestSolrHelper
public class TestSolrHelper
{
	//@Test
	public void indexTrials()
	{
		try
		{
			SolrClient client=solrClient();
			System.out.println("indexing trials");
			for (int i=0; i<1000; i++)
			{
				SolrInputDocument doc = new SolrInputDocument();
				doc.addField("id",  UUID.randomUUID().toString());
				doc.addField("name", "NCT0000"+i);
				System.out.println("indexing trial: "+StringHelper.toString(doc));
				UpdateResponse response=client.add("nctclinicaltrials", doc);
				client.commit("nctclinicaltrials");
			}
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
	
	//@Test
	public void queryTrials()
	{
		try
		{
			String search="name:NCT";
			SolrClient client=solrClient();
			
			final Map<String, String> queryParamMap = new HashMap<String, String>();
			queryParamMap.put("q", "*:*");
			queryParamMap.put("fl", "id, name");
			queryParamMap.put("sort", "id asc");
			MapSolrParams queryParams = new MapSolrParams(queryParamMap);

			//SolrQuery query=new SolrQuery();
			//query.setQuery(search);
			QueryResponse response=client.query("nctclinicaltrials", queryParams);
			final SolrDocumentList documents = response.getResults();
			
			 System.out.println("Found " + documents.getNumFound() + " documents");
			for(SolrDocument document : documents)
			{
			  final String id = (String) document.getFirstValue("id");
			  final String name = (String) document.getFirstValue("name");
			  System.out.println("id: " + id + "; name: " + name);
			  //System.out.println("found: "+StringHelper.toString(trial));
			}
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
	
	/*	
	//https://stackoverflow.com/questions/14485031/faceting-using-solrj-and-solr4
	@Test
	public void facets()
	{
		try
		{
			SolrClient client=solrClient();
			
			String collection="nctclinicaltrials";
			List<String> fields=Arrays.asList("genes", "conditions", "interventions", "phase");
			
			JsonQueryRequest request = new JsonQueryRequest()
				.setQuery("*:*")
				.setLimit(0);
			for (String field : fields)
			{
				request = request.withFacet(field, new TermsFacetMap(field));
			}
			QueryResponse response = request.process(client, collection);
			String json=response.jsonStr();
			FileHelper.writeFile(Constants.TMP_DIR+"/solr/facets.json", json);
			
			Facets facets=new Facets();
			
			JsonNode rootNode=JsonHelper.parse(json);
			JsonNode facetsNode=rootNode.path("facets");
			Iterator<Entry<String,JsonNode>> iter=facetsNode.fields();
			while (iter.hasNext())
			{
				Entry<String,JsonNode> entry=iter.next();
				if (entry.getKey().equals("count"))
				{
					//System.out.println("count="+entry.getValue().asLong());
					facets.setCount(entry.getValue().asLong());
				}
				else
				{
					Facets.TermFacet facet=new Facets.TermFacet();
					facet.setName(entry.getKey());
					facets.getFacets().add(facet);
					
					JsonNode facetNode=entry.getValue();
					Iterator<JsonNode> bucketIter=facetNode.get("buckets").elements();
					while (bucketIter.hasNext())
					{
						JsonNode bucketNode=bucketIter.next();
						Facets.Count count=new Facets.Count();
						count.setName(bucketNode.get("val").asText());
						count.setCount(bucketNode.get("count").asLong());
						facet.getBuckets().add(count);
					}
				}
					
			}
			
			System.out.println("facets="+JsonHelper.toJson(facets));
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
	*/
	
	//@Test
	public void facets()
	{
		SolrClient client=solrClient();
		Facets facets=SolrHelper.getTermFacet(client, "nctclinicaltrials", "*:*", Arrays.asList("genes", "conditions", "interventions", "phase"));
		System.out.println("facets="+JsonHelper.toJson(facets));
	}
	
	///////////////////////////////////
	
	private SolrClient solrClient()
	{
		return new HttpSolrClient.Builder("http://1703-030.b.hiroshima-u.ac.jp:8983/solr").build();
	}
}