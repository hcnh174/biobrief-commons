package org.biobrief.util;

//import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;

//gradle  --stacktrace --info test --tests *TestMustacheHelper
public class TestMustacheHelper
{
	@Test
	public void testRender()
	{
		System.out.println("TestMustacheHelper.testRender*********************");
		System.out.println(MustacheHelper.render("{{hello}}, {{world}}!", new HelloWorld()));
	}
	
	public static class HelloWorld
	{
		private String hello="HELLO";
		private String world="WORLD";
		
		public String getHello() {return hello;}
		public String getWorld() {return world;}
	}
}