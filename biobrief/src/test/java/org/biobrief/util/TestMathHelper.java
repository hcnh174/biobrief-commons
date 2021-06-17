package org.biobrief.util;

import org.junit.jupiter.api.Test;

//gradle  --stacktrace --info test --tests *TestMathHelper
public class TestMathHelper
{
	//https://www.dreamincode.net/forums/topic/344766-scientific-notation-in-java/
	@Test
	public void parseScientificNotation()
	{
		//2.5×10^9
		//1.2×10^6
		System.out.println(MathHelper.parseScientificNotation("6.7E2"));
		System.out.println(MathHelper.parseScientificNotation("6.7*10^2"));
		System.out.println(MathHelper.parseScientificNotation("2.5×10^9"));
		System.out.println(MathHelper.parseScientificNotation("2/5 sacrificed"));
	}
	
	//https://www.dreamincode.net/forums/topic/344766-scientific-notation-in-java/
	
	@Test
	public void formatPercent()
	{
		System.out.println(MathHelper.format(100.0f * 0.15361111f, "#.##"));
	}
}
