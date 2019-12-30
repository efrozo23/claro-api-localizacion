package com.claro.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockEndpoints extends RouteBuilder{
	
	private Logger lg = LoggerFactory.getLogger(getClass());
	
	public static final String MOCKROUTE = "direct:MockEndpoint";
	

	@Override
	public void configure() throws Exception {
	
		from(MOCKROUTE).routeId("MOCKROUTE").streamCaching("true")
			.log(LoggingLevel.INFO, lg, "Inicia la ruta")
			.end();
			
		
	}

}
