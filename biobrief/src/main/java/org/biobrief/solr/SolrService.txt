package org.biobrief.solr;

import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.biobrief.solr.SolrHelper.Facets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolrService
{
	@SuppressWarnings("unused") private final SolrProperties properties;
	@Autowired private SolrClient client;
	
	public SolrService(SolrProperties properties)
	{
		this.properties=properties;
	}
	
	public void deleteAll(String collection)
	{
		SolrHelper.deleteAll(client, collection);
	}
	
	public void indexDocuments(List<?> documents, String collection)
	{
		SolrHelper.indexDocuments(client, documents, collection);
	}
	
	
	public <T> List<T> search(String collection, String term, Class<T> cls)
	{
		return SolrHelper.search(client, collection, term, cls);
	}
	
	public Facets getTermFacet(String collection, String query, List<String> fields)
	{
		return SolrHelper.getTermFacet(client, collection, query, fields);
	}
	/*
	public void indexTrials(MessageWriter out)
	{
		try
		{
			out.println("indexing trials");
			for (int i=0; i<1000; i++)
			{
				SolrInputDocument doc = new SolrInputDocument();
				doc.addField("id", "NCT0000"+i);
				doc.addField("name", RandomHelper.randomWord(10, 20));
				out.println("indexing trial: "+StringHelper.toString(doc));
				UpdateResponse response=client.add("nctclinicaltrials", doc);
				client.commit();
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
	
	public void queryTrials(String search)
	{
		try
		{
			SolrQuery query=new SolrQuery();
			query.setQuery(search);
			QueryResponse response=client.query("nctclinicaltrials", query);
			SolrDocumentList list=response.getResults();
			for (SolrDocument trial : response.getResults())
			{
				System.out.println("found: "+StringHelper.toString(trial));
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
	*/
	
//	solrQuery.setFields("id");
////for (String filter : StringHelper.split(search.getFields(),","))
////{
////	solrQuery.addFilterQuery(filter);
////}
////solrQuery.setRows(Integer.MAX_VALUE);
////SearchParams params=new SearchParams(solrQuery);
////SearchResults results=searchService.search(params);
	
	/*
	public void indexTrials(MessageWriter out)
	{
		out.println("indexing trials");
		for (int i=0; i<1000; i++)
		{
			SolrNctClinicalTrial trial = new SolrNctClinicalTrial();
			trial.setId("NCT0000"+i);
			trial.setName(RandomHelper.randomWord(10, 20));
			out.println("indexing trial: "+trial.getId());
			repository.save(trial);
		}
	}
	
	public List<SolrNctClinicalTrial> queryTrials(String query)
	{
		return repository.findByCustomQuery(query);
	}
	*/
}
