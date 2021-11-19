package org.biobrief.dictionary;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.biobrief.util.AbstractEntity;
import org.biobrief.util.CException;
import org.biobrief.util.DataFrame;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.StringHelper;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@SuppressWarnings("rawtypes")
public class EntityUpdater<T extends AbstractEntity>
{	
	private final Map<String, T> entities;
	private final boolean overwrite;
	private final MessageWriter out;
	private final SpelParserConfiguration config=new SpelParserConfiguration(true, true);
	private final ExpressionParser parser=new SpelExpressionParser(config);
	
	private EntityUpdater(Map<String, T> entities, boolean overwrite, MessageWriter out)
	{
		this.entities=entities;
		this.overwrite=overwrite;
		this.out=out;
	}
	
	@SuppressWarnings("unchecked")
	public static void update(Map entities, Values values, boolean overwrite, MessageWriter out)
	{
		EntityUpdater updater=new EntityUpdater(entities, overwrite, out);
		updater.update(values);
	}
	
	@SuppressWarnings("unchecked")
	public static void update(AbstractEntity entity, Values values, boolean overwrite, MessageWriter out)
	{
		Map<String, AbstractEntity> entities=Maps.newLinkedHashMap();
		entities.put((String)entity.getId(), entity);
		EntityUpdater updater=new EntityUpdater(entities, overwrite, out);
		updater.update(values);
	}

	public void update(Values values)
	{
		for (Value value : values.getValues())
		{
			T entity=getEntity(value.getIdentifier());
			update(entity, value);
		}
	}
	
	//http://www.baeldung.com/spring-expression-language
	private void update(T entity, Value value)
	{
		out.println("setting value from string: identifier="+value.getIdentifier()+", property="+value.getProperty()+", value="+value.getValue());
		StandardEvaluationContext context=new StandardEvaluationContext(entity);
		Object oldvalue=parser.parseExpression(value.getProperty()).getValue(context);
		if (overwrite || !StringHelper.hasContent(oldvalue))
			parser.parseExpression(value.getProperty()).setValue(context, value.getValue());
	}
		
	private T getEntity(String identifier)
	{
		if (!entities.containsKey(identifier))
			throw new CException("cannot find entity with identifier: "+identifier);
		return entities.get(identifier);
	}
	
	//////////////////////////////////////////////////////////////
	
	public static class Values
	{
		protected final List<Value> values=Lists.newArrayList();
	
		private void loadValues(String str)
		{
			DataFrame<String> dataframe=DataFrame.parse(str, new DataFrame.Options("identifier", "property"));
			for (String rowname : dataframe.getRowNames())
			{
				String identifier=dataframe.getStringValue("identifier", rowname);
				String property=dataframe.getStringValue("property", rowname);
				String value=dataframe.getStringValue("value", rowname);
				add(identifier, property, value);
			}
		}
		
		public void add(Value value)
		{
			this.values.add(value);
		}
		
		public void add(String identifier, String property, String value)
		{
			add(new Value(identifier, property, value));
		}

		public Set<String> getIdentifiers()
		{
			Set<String> identifiers=Sets.newLinkedHashSet();
			for (Value value : values)
			{
				identifiers.add(value.getIdentifier());
			}
			return identifiers;
		}
		
		public List<Value> getValues(){return this.values;}
		
		public String toString()
		{
			StringBuilder buffer=new StringBuilder();
			buffer.append("identifier\tproperty\tvalue\n");
			for (Value value : values)
			{
				buffer.append(value.identifier+"\t");
				buffer.append(value.property+"\t");
				buffer.append(value.value+"\n");
			}
			return buffer.toString();
		}
		
		public static Values load(String str) 
		{
			Values values=new Values();
			values.loadValues(str);
			return values;
		}
	}
	
	public static class Value
	{
		protected String identifier;
		protected String property;
		protected String value;
		
		public Value(){}
		
		public Value(String identifier, String property, String value)
		{
			assert(StringHelper.hasContent(identifier));
			assert(StringHelper.hasContent(property));
			this.identifier=identifier;
			this.property=property;
			this.value=value;
		}
		
		public String toString() {return StringHelper.toString(this);}
		
		public String getIdentifier(){return this.identifier;}
		public void setIdentifier(final String identifier){this.identifier=identifier;}

		public String getProperty(){return this.property;}
		public void setProperty(final String property){this.property=property;}

		public String getValue(){return this.value;}
		public void setValue(final String value){this.value=value;}
	}
}