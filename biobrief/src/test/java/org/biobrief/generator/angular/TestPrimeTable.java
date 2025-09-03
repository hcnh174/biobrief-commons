package org.biobrief.generator.angular;

import static org.assertj.core.api.Assertions.assertThat;

import org.biobrief.dictionary.Dictionary;
import org.biobrief.generator.GeneratorConstants.RenderMode;
import org.biobrief.generator.templates.Grid;
import org.biobrief.generator.templates.Grid.GridParams;
import org.biobrief.util.FileHelper;
import org.biobrief.util.SimpleMap;
import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestPrimeTable
public class TestPrimeTable
{
	@Test
	public void buildTurboTable()
	{
		Grid grid=buildGrid();
		PrimeTable table=new PrimeTable(grid);
		assertThat(table).isNotNull();
		FileHelper.writeFile(".temp/table.html", table.render(new RenderParams(RenderMode.angular)));
		FileHelper.writeFile(".temp/table.ts", table.toTypescript());
	}
	
	////////////////////////////////////////
	
	private Grid buildGrid()
	{
		Dictionary dictionary=new Dictionary("c:/workspace/hlsg/data/dictionary");
		SimpleMap<Object> values=new SimpleMap<Object>();
		values.put("group", "hirodai");
		values.put("mode", "readonly");
		values.put("entity", "Ultrasound");
		values.put("title", "[AUTO]");
		GridParams params=new GridParams(values);
		Grid grid=new Grid("hirodai-ultrasounds", params, dictionary);
		
		addColumn(grid, "hirodaiId", 50);
		addColumn(grid, "name", 100);
		addColumn(grid, "kana", 100);
		addColumn(grid, "sex", 20, "i18n.sex");
		addColumn(grid, "age", 20, "i18n.age");
		addColumn(grid, "date", 30);
		//FileHelper.writeFile(".temp/grid.txt", StringHelper.toString(grid)
		return grid;
	}
	
	private void addColumn(Grid grid, String field, int width)
	{
		addColumn(grid, field, width, "[AUTO]");
	}
	
	private void addColumn(Grid grid, String field, int width, String header)
	{
		SimpleMap<Object> values=new SimpleMap<Object>();
		values.put("header", header);
		values.put("body", "${"+field+"}");
		values.put("width", width);
		grid.addColumn(field, grid.getParams().createColumnParams(values));
	}
}