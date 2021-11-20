package org.biobrief.dictionary;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;
import static org.assertj.core.api.Assertions.assertThat;

import org.biobrief.util.StringHelper;

//gradle --stacktrace --info test --tests *MergeSourceMapTest
public class MergeSourceMapTest
{
	@Test
	public void mergeSourceMap()
	{
		MergeSources mergeSources=getMergeSources();
		Map<String,String> map=Maps.newLinkedHashMap();
		map.put("name", "value");
		map.put("priority", "g>csv>excel>ifn,tvr24,tvr48,smv,dcvasv,sof,harvoni>access,fmhbv,fmhcv");
		MergeSourceMap mergemap=new MergeSourceMap(mergeSources, map);
		System.out.println("mergemap="+StringHelper.toString(mergemap));
		assertThat(mergemap.getPriority("excel")).isEqualTo(3);
		// not in the list -- should get a priority lower than any set priorities
		assertThat(mergemap.getPriority("fmfirstexam")).isEqualTo(6);
	}
	
//	@Test
//	public void mergeSourceMapSqlView()
//	{
//		MergeSources mergeSources=getMergeSources();
//		Map<String,String> map=Maps.newLinkedHashMap();
//		map.put("name", "name");
//		map.put("priority", "naikapatients>questionnaires>accessmatome");
//		map.put("naikapatients", "患者氏名（漢字）");
//		map.put("fmfirstexam", "name");
//		map.put("fmpatients", "患者名");
//		map.put("fmhbv", "患者名");
//		map.put("fmhcv", "");
//		map.put("accesssnp", "患者名");
//		map.put("accesspatient", "氏名");
//
//		MergeSourceMap mergemap=new MergeSourceMap(mergeSources, map);
//		System.out.println("mergemap="+StringHelper.toString(mergemap));
//		
//		for (MergeSourceMap.Source source : mergemap.getSources())
//		{
//			System.out.println("source="+source.getMergeSql("id"));
//		}
//	}
	
	protected MergeSources getMergeSources()
	{
		String filename="c:/workspace/hlsg/data/dictionary/sources.txt";
		MergeSources sources=new MergeSources(filename);
		//sources.add(new MergeSources.MergeSource("naika"));
		//sources.add(new MergeSources.MergeSource("mergeinterview", false));
		return sources;
	}
}