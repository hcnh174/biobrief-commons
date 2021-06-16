package org.biobrief.util;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.bson.Document;
//import org.springframework.data.mongodb.core.query.BasicQuery;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

//https://gist.github.com/olivergierke/decf03d4948cd58a51bc
public class JsonFilterParser
{
	@SuppressWarnings("unused")	private static final Logger log=LoggerFactory.getLogger(JsonFilterParser.class);
	public enum Operator {equals, in, after, before, gt, lt, gte, lte}
	
	private JsonFilterParser(){}
		
	public static Filters parseFilter(String filter)
	{
		Filters filters=new Filters();
		if (!StringHelper.hasContent(filter))
			return filters;
		JsonNode root=JsonHelper.parse(filter);
		for (Iterator<JsonNode> iter=root.iterator(); iter.hasNext();)
		{
			JsonNode node=iter.next();
			//System.out.println("node: "+node.toString());
			String field=node.get("field").asText();
			Operator operator=Operator.valueOf(node.get("operator").asText());
			String value=node.get("value").asText();
			filters.add(new Filter(field, operator, value));
		}
		return filters;
	}
	
	////////////////////////////////////////////////////////////////
	
	public static class Filters
	{
		List<Filter> filters=Lists.newArrayList();
		
		public void add(String field, String value)
		{
			add(field, Operator.equals, value);
		}
		
		public void add(String field, Operator operator, String value)
		{
			add(new Filter(field, operator, value));
		}
		
		public void add(Filter filter){this.filters.add(filter);}

//		public Query createMongoQuery()
//		{
//			Query query=new BasicQuery("{}");
//			for (Filter filter : filters)
//			{
//				query.addCriteria(filter.createCriteria());
//			}
//			return query;
//		}
//		
//		public Document createMongoDocument()
//		{
//			Document query=new Document();
//			for (Filter filter : filters)
//			{
//				query.put(filter.getField(), filter.getValue());
//			}
//			return query;
//		}
		
		public boolean isEmpty()
		{
			return filters.isEmpty();
		}
		
		@Override
		public String toString()
		{
			List<String> arr=Lists.newArrayList();
			for (Filter filter : filters)
			{
				arr.add(filter.toString());
			}
			return StringHelper.join(arr, " AND ");
		}
		
		public List<Filter> getFilters(){return filters;}
	}
	
	public static class Filter
	{
		private String field;
		private Operator operator;
		private String value;
		
		public Filter(String field, Operator operator, String value)
		{
			this.field=field;
			this.operator=operator;
			this.value=value;
		}
		
//		public Criteria createCriteria()
//		{
//			return new Criteria(field).is(value);// TODO ignores operator
//		}
		
		@Override
		public String toString()
		{
			return "("+field+" "+operator+" "+value+")";
		}
		
		public String getField(){return this.field;}
		public void setField(final String field){this.field=field;}

		public Operator getOperator(){return this.operator;}
		public void setOperator(final Operator operator){this.operator=operator;}

		public String getValue(){return this.value;}
		public void setValue(final String value){this.value=value;}
	}
	
//	public static Document getFileFilter(String filter)
//	{
//		System.out.println("filter="+filter);
//		Document query=new Document();
//		if (!StringHelper.hasContent(filter))
//			return query;
//		JsonNode root=JsonHelper.parse(filter);
//		for (Iterator<JsonNode> iter=root.iterator(); iter.hasNext();)
//		{
//			JsonNode node=iter.next();
//			System.out.println("node: "+node.toString());
//			String field=node.get("field").asText();
//			//String op=node.get("op").asText();// TODO ignores op for now
//			Object value=node.get("value").asText();
//			if (field.equals("dbno"))
//			{
//				field="metadata.dbno";
//				value=MathHelper.parseInt(value);
//			}
//			else if (field.equals("username"))
//			{
//				field="metadata.username";
//			}
//			if (value!=null)
//				query.put(field, value);
//		}
//		return query;
//	}
}