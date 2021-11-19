package org.biobrief.dictionary;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.biobrief.dictionary.EntityUpdater.Values;
import org.biobrief.util.AbstractEntity;
import org.biobrief.util.MessageWriter;
import org.biobrief.util.StringHelper;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

//gradle --stacktrace --info test --tests *TestEntityUpdater
public class TestEntityUpdater
{
	@Test
	public void update()
	{
		Integer dbno=5000;
		String identifier=""+dbno;
		
		Patient patient=new Patient();
		patient.setId("ABNKDFHJDFJKNCSDE");
		patient.setDbno(dbno);
		patient.setName("Johnny B. Good");
		patient.setAge(32);
		
		Biopsy biopsy=new Biopsy();
		biopsy.setHai("HAI there");
		patient.getBiopsies().add(biopsy);
		
		System.out.println("before: "+StringHelper.toString(patient));
		
		Map<String, Patient> entities=Maps.newLinkedHashMap();
		entities.put(identifier, patient);
		
		Values values=new Values();
		values.add(identifier, "name", "Johnny B. Bad");
		values.add(identifier, "info", "important detail");
		values.add(identifier, "biopsies[0].fibrosis", "2");
		values.add(identifier, "biopsies[1].fibrosis", "3");
				
		EntityUpdater.update(entities, values, false, new MessageWriter());
		System.out.println("after: "+StringHelper.toString(patient));
		
		assertThat(entities.get(identifier).getName()).isEqualTo("Johnny B. Good");// should not overwrite existing values
		assertThat(entities.get(identifier).getInfo()).isEqualTo("important detail");
		
		// there should be two biopsies, the second one should be F3
		assertThat(entities.get(identifier).getBiopsies()).hasSize(2);
		assertThat(entities.get(identifier).getBiopsies().get(0).getHai()).isEqualTo("HAI there");
		assertThat(entities.get(identifier).getBiopsies().get(1).getFibrosis()).isEqualTo(3);
	}
	
	public static class Patient extends AbstractEntity<String>
	{
		protected String id;
		protected Integer dbno;
		protected String name;
		protected Integer age;
		protected String info;
		protected List<Biopsy> biopsies=Lists.newArrayList();
		
		public String getId(){return this.id;}
		public void setId(final String id){this.id=id;}

		public Integer getDbno(){return this.dbno;}
		public void setDbno(final Integer dbno){this.dbno=dbno;}
		
		public String getName(){return this.name;}
		public void setName(final String name){this.name=name;}

		public Integer getAge(){return this.age;}
		public void setAge(final Integer age){this.age=age;}
		
		public String getInfo(){return this.info;}
		public void setInfo(final String info){this.info=info;}
		
		public List<Biopsy> getBiopsies(){return this.biopsies;}
	}
	
	public static class Biopsy
	{
		protected Integer fibrosis;
		protected String hai;
		
		public Integer getFibrosis(){return this.fibrosis;}
		public void setFibrosis(final Integer fibrosis){this.fibrosis=fibrosis;}

		public String getHai(){return this.hai;}
		public void setHai(final String hai){this.hai=hai;}
	}
}