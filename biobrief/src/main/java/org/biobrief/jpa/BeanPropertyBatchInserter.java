package org.biobrief.jpa;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.biobrief.util.BeanHelper;
import org.biobrief.util.StringHelper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class BeanPropertyBatchInserter<T extends Object>
{
	public static final Integer DEFAULT_BATCH_SIZE=1000;
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final String table;
	private final boolean ignoreConflicts;
	private final Set<String> skipColumns=Sets.newLinkedHashSet();
	private final List<T> data=Lists.newArrayList();

	public BeanPropertyBatchInserter(DataSource dataSource, String table, boolean ignoreConflicts)
	{
		this.jdbcTemplate=new NamedParameterJdbcTemplate(dataSource);
		this.table=table;
		this.ignoreConflicts=ignoreConflicts;
	}
	
	public BeanPropertyBatchInserter(DataSource dataSource, String table)
	{
		this(dataSource, table, false);
	}
	
	public void skipColumn(String col)
	{
		skipColumns.add(col);
	}
	
	public void reset()
	{
		data.clear();
	}
	
	public void add(T obj)
	{
		data.add(obj);
	}

	public void addAll(Iterable<T> objs)
	{
		for (T obj : objs)
		{
			add(obj);
		}
	}
	
	///////////////////////////////////////////////////
	
	public void delete()
	{
		JpaHelper.execute(jdbcTemplate, "DELETE FROM "+table);
	}
	
	public void delete(String where)
	{
		JpaHelper.execute(jdbcTemplate, "DELETE FROM "+table+" WHERE "+where);
	}
	
	public void execute()
	{
		execute(DEFAULT_BATCH_SIZE);
	}
		
	public void execute(int batchsize)
	{
		System.out.println("inserting "+data.size()+" rows");
		if (data.isEmpty())
			return;
		Stopwatch stopwatch=Stopwatch.createStarted();
		String sql=getSql();
		List<List<T>> batches = Lists.partition(data, batchsize);
		System.out.println("starting batches: "+batches.size());
		for (List<T> batch : batches)
		{
			executeBatch(sql, batch);
		}		
		System.out.println("finished all ("+stopwatch.elapsed(TimeUnit.MILLISECONDS)+" milliseconds)");
		reset();
	}
	
	//http://www.java2s.com/Tutorial/Java/0417__Spring/SqlParameterSourceAndSimpleJdbcTemplateforbatchupdate.htm
	private void executeBatch(String sql, List<T> objs)
	{
		System.out.println("starting batch: "+objs.size()+" rows");
		Stopwatch stopwatch=Stopwatch.createStarted();
		SqlParameterSource[] source=new SqlParameterSource[objs.size()];
		for (int index=0;index<objs.size(); index++)
		{
			T obj=objs.get(index);
			source[index]=new BeanPropertySqlParameterSource(obj);
		}
		System.out.println("inserting batch: "+objs.size()+" rows");
		jdbcTemplate.batchUpdate(sql, source);
		System.out.println("finished batch in "+stopwatch.elapsed(TimeUnit.MILLISECONDS)+" milliseconds");
	}
	
	private String getSql()
	{
		//List<String> colnames=BeanHelper.getReadWriteProperties(data.get(0));
		List<String> colnames=getColnames();
		String cols=StringHelper.join(JpaHelper.field2column(colnames), ", ");
		String params=StringHelper.join(StringHelper.prefix(":", colnames), ", ");
		String sql="INSERT into "+table+" ("+cols+") VALUES ("+params+")";
		if (ignoreConflicts)
			sql+=" ON CONFLICT DO NOTHING";
		System.out.println("sql="+sql);
		return sql;
	}
	
	private List<String> getColnames()
	{
		List<String> colnames=BeanHelper.getReadWriteProperties(data.get(0));
		colnames.removeAll(skipColumns);
		return colnames;
	}
}