package org.biobrief.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestLoginHelper
public class TestLoginHelper
{
	@Test
	public void generatePassword()
	{
		for (int index=0; index<100; index++)
		{
			int length=12;
			String password=LoginHelper.generatePassword(length);
			System.out.println("generated password: "+password);
			assertThat(password.length()).isEqualTo(length);
		}
	}
}