package org.biobrief.util;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

//gradle --stacktrace --info test --tests *TestBeanHelper
public class TestBeanHelper
{	
	@Test
	public void flatten()
	{
		Person person=new Person();
		//System.out.println(StringHelper.toString(person));
		Map<?,?> map=BeanHelper.flatten(person);
		System.out.println(map);
	}
	
	public static class Person
	{
		private String name="Nelson Hayes";
		private Integer	age=44;
		private Address address=new Address();
		private List<Job> jobs=Lists.newArrayList();
		private Map<String, Prize> prizes=Maps.newLinkedHashMap();
		
		public Person()
		{
			jobs.add(new Job());
			jobs.add(new Job());
			
			prizes.put("2017/07/09", new Prize());
			prizes.put("2018/07/09", new Prize());
		}
		
		public String getName(){return this.name;}
		public void setName(final String name){this.name=name;}

		public Integer getAge(){return this.age;}
		public void setAge(final Integer age){this.age=age;}
		
		public Address getAddress(){return this.address;}
		public void setAddress(final Address address){this.address=address;}

		public List<Job> getJobs(){return this.jobs;}
		public void setJobs(final List<Job> jobs){this.jobs=jobs;}
		
		public Map<String, Prize> getPrizes(){return this.prizes;}
		public void setPrizes(final Map<String, Prize> prizes){this.prizes=prizes;}
	}
	
	public static class Address
	{
		private String address="123 Here Dr.";
		private String city="Anywhere";
		private String state="PA";
		private String zip="12345";
		
		public String getAddress(){return this.address;}
		public void setAddress(final String address){this.address=address;}

		public String getCity(){return this.city;}
		public void setCity(final String city){this.city=city;}

		public String getState(){return this.state;}
		public void setState(final String state){this.state=state;}

		public String getZip(){return this.zip;}
		public void setZip(final String zip){this.zip=zip;}
	}
	
	public static class Job
	{
		private String name="Dunder Mifflin";
		private String address="79 Mifflin Ave.";
		private String role="trash collector";
		
		public String getName(){return this.name;}
		public void setName(final String name){this.name=name;}

		public String getAddress(){return this.address;}
		public void setAddress(final String address){this.address=address;}

		public String getRole(){return this.role;}
		public void setRole(final String role){this.role=role;}
	}
	
	public static class Prize
	{
		private String award="first place in participation award";
		private String amount="$1.00";
		
		public String getAward(){return this.award;}
		public void setAward(final String award){this.award=award;}

		public String getAmount(){return this.amount;}
		public void setAmount(final String amount){this.amount=amount;}
	}
}