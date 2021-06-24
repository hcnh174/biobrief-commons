package org.biobrief.users;

import org.biobrief.users.dao.ActivityDao;
import org.biobrief.users.dao.LoginDao;
import org.biobrief.users.dao.RouteDao;
import org.biobrief.users.dao.UserDao;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages={"edu.hiro.users"})
@EnableConfigurationProperties(UsersProperties.class)
@EntityScan(basePackages={"org.biobrief.users"})
@EnableMongoRepositories(basePackages={"org.biobrief.users"})
public class UsersConfiguration
{
	{System.out.println("UsersConfiguration");}
	
	@Bean
	public UserService userService()
	{
		return new UserServiceImpl();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		//return new PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return new BCryptPasswordEncoder(16);
	}
	
	@Bean
	public ActivityDao activityDao()
	{
		return new ActivityDao();
	}
	
	@Bean
	public LoginDao loginDao()
	{
		return new LoginDao();
	}
	
	@Bean
	public RouteDao routeDao()
	{
		return new RouteDao();
	}
	
	@Bean
	public UserDao userDao()
	{
		return new UserDao();
	}
}
