package com.upgrade.reservation.service.restclient.optional;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;

public class ReservationApiRestTemplateFactory
{

	@Bean
	public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory()
	{


		// httpClient socket resource will be closed, when spring context is destroyed.
		// It is because httpClient wrapped by HttpComponentsClientHttpRequestFactory.
		// HttpComponentsClientHttpRequestFactory is implemented DisposableBean, and its destroy method will closed httpClient.
		// (@see org.springframework.beans.factory.DisposableBean and @see org.springframework.http.client.HttpComponentsClientHttpRequestFactory.destroy)
		CloseableHttpClient httpClient = HttpClients.custom()

				.build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient( httpClient );
		return requestFactory;
	}

	@Bean
	public RestTemplate restTemplate()
	{
		RestTemplate template = new RestTemplate( clientHttpRequestFactory() );

			template.setInterceptors( Arrays.asList( new ClientHttpRequestInterceptor()
			{
				@Override
				public ClientHttpResponse intercept( HttpRequest request, byte[] body, ClientHttpRequestExecution execution ) throws IOException
				{

					return execution.execute( request, body );
				}
			} ) );

		return template;

	}

}
