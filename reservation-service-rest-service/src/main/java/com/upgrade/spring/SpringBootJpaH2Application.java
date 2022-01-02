package com.upgrade.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class SpringBootJpaH2Application
{

	@Autowired
	private ObjectMapper objectMapper;

	public static void main( String[] args )
	{
		SpringApplication.run( SpringBootJpaH2Application.class, args );
	}

	@PostConstruct
	public void setUp()
	{
		objectMapper.registerModule( new JavaTimeModule() );
	}
}
