package org.biobrief.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.biobrief.util.RandomHelper;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

//gradle --stacktrace --info test --tests *TestUtil
public class TestUtil
{	
	@Test
	public void convertToPercentage()
	{
		assertThat(Util.convertToPercentage(500, 1000)).isEqualTo(50);
	}
	
	@Test
	public void convertToPercentages()
	{
		List<Integer> values=Lists.newArrayList(100, 200, 100, 400, 100);
		List<Integer> percentages=Util.convertToPercentages(values);
		assertThat(percentages).hasSize(5);
		Integer sum=percentages.stream().collect(Collectors.summingInt(Integer::intValue));
		assertThat(sum).isEqualTo(100);
	}
	
	@Test
	public void convertToPercentagesRandom()
	{
		List<Integer> values=RandomHelper.randomIntegers(10, 10, 500, false);
		List<Integer> percentages=Util.convertToPercentages(values);
		Integer sum=percentages.stream().collect(Collectors.summingInt(Integer::intValue));
		assertThat(sum).isEqualTo(100);
	}
	
//	@Test
//	public void insertText()
//	{
//		String filename=org.biobrief.util.Constants.BASE_DIR+"/angular/src/app/kanken/view/grids/kanken-treatments-grid.component.ts";
//		String str=FileHelper.readFile(filename);
//		String replacetext="NELSON\n\n";		
//		System.out.println(Util.insertText("INIT", str, replacetext, true));
//	}
	
	@Test
	public void getIndent()
	{
		String str=
				"\r\n" + 
				"	ngOnInit() {\r\n" + 
				"		// INIT_START";
		int index=str.indexOf("// INIT_START");
		System.out.println("["+Util.getIndent(str, index)+"]");
	}
}