package org.biobrief.util;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;

import static org.assertj.core.api.Assertions.assertThat;

//gradle --stacktrace --info :biobrief-util:test --tests *TestJsonHelper
public class TestJsonHelper
{
	@Test
	public void toJavascriptHasCorrectFormatting()
	{
		Map<String, Object> values=Maps.newLinkedHashMap();
		values.put("group", "'hirodai'");
		values.put("mode", "'edit'");
		values.put("entity", "'Ultrasound'");
		String json=JsonHelper.toJavascript(values);
		String expected="{\n\tgroup: 'hirodai',\n\tmode: 'edit',\n\tentity: 'Ultrasound'\n}";
		System.out.println(json);
		System.out.println(expected);
		assertThat(json).isEqualTo(expected);
	}
	
	@Test
	public void deepClone()
	{
		Address address = new Address("Downing St 10", "London", "England");
		User user = new User("Prime", "Minister", address);
		System.out.println("user="+StringHelper.toString(user));
		User copy=JsonHelper.deepClone(user);
		System.out.println("copy="+StringHelper.toString(copy));
		assertThat(copy).isNotSameAs(user);
	}
	
	@Test
	public void deepCloneSublclass()
	{
		Address address = new Address("Downing St 10", "London", "England");
		User user = new SpecialUser("Prime", "Minister", address);
		System.out.println("user="+StringHelper.toString(user));
		User copy=JsonHelper.deepClone(user);
		System.out.println("copy="+StringHelper.toString(copy));
		assertThat(copy).isNotSameAs(user);
		System.out.println("user.class="+user.getClass().getName());
		System.out.println("copy.class="+user.getClass().getName());
	}
	
	static class Address {
		 
	    private String street;
	    private String city;
	    private String country;
	    
	    public Address() {}
	    
	    public Address(String street, String city, String country)
	    {
	    	this.street=street;
	    	this.city=city;
	    	this.country=country;
	    }
	    
		public String getStreet(){return this.street;}
		public void setStreet(final String street){this.street=street;}

		public String getCity(){return this.city;}
		public void setCity(final String city){this.city=city;}

		public String getCountry(){return this.country;}
		public void setCountry(final String country){this.country=country;}	 
	}
	
	static class User {
	 
	    private String firstName;
	    private String lastName;
	    private Address address;
	    
	    public User() {}
	    
	    public User(String firstName, String lastName, Address address)
	    {
	    	this.firstName=firstName;
	    	this.lastName=lastName;
	    	this.address=address;
	    }
	    
	    public String getFirstName(){return this.firstName;}
		public void setFirstName(final String firstName){this.firstName=firstName;}

		public String getLastName(){return this.lastName;}
		public void setLastName(final String lastName){this.lastName=lastName;}

		public Address getAddress(){return this.address;}
		public void setAddress(final Address address){this.address=address;} 
	}
	
	static class SpecialUser extends User
	{
	    public SpecialUser() {}
	    
	    public SpecialUser(String firstName, String lastName, Address address)
	    {
	    	super(firstName, lastName, address);
	    }
	}
}