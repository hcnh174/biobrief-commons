package org.biobrief.users;

import org.biobrief.users.dao.ActivityDao;
import org.biobrief.users.dao.AngularErrorDao;
import org.biobrief.users.dao.LoginDao;
import org.biobrief.users.dao.LogoutDao;
import org.biobrief.users.dao.RouteDao;
import org.biobrief.users.dao.UserDao;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(UsersProperties.class)
@EntityScan(basePackages={"org.biobrief.users"})
@EnableMongoRepositories(basePackages={"org.biobrief.users"})
public class UsersConfiguration
{
	{System.out.println("UsersConfiguration");}
	
	@Bean
	UserService userService()
	{
		return new UserServiceImpl();
	}

	@Bean
	PasswordEncoder passwordEncoder()
	{
		//return new PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return new BCryptPasswordEncoder(16);
	}
	
	@Bean
	ActivityDao activityDao()
	{
		return new ActivityDao();
	}
	
	@Bean
	LoginDao loginDao()
	{
		return new LoginDao();
	}
	
	@Bean
	LogoutDao logoutDao()
	{
		return new LogoutDao();
	}
	
	@Bean
	RouteDao routeDao()
	{
		return new RouteDao();
	}
	
	@Bean
	AngularErrorDao angularErrorDao()
	{
		return new AngularErrorDao();
	}
	
	@Bean
	UserDao userDao()
	{
		return new UserDao();
	}
}
