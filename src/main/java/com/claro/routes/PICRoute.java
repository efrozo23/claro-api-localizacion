package com.claro.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.claro.beans.ValidatorDate;

@Component
public class PICRoute extends RouteBuilder  {

	public static final String PIC_ROUTE ="direct://pic-endpoint";
	private Logger logger = LoggerFactory.getLogger(PICRoute.class);
	
	@Override
	public void configure() throws Exception {
		
		from(PIC_ROUTE).routeId("PIC-SERVICE")
			.setHeader(Exchange.HTTP_METHOD, simple("{{pic.method}}"))
			.setHeader(Exchange.HTTP_URI, simple("{{pic.url}}{{pic.prefijo}}${exchangeProperty.min}"))	
			.log("Invoking pic web service with: ${exchangeProperty.min}")
			.to("http4:PICWS")
			.log("respuesta PIC WS: ${body}")
		.end();	
	}
	
	
}
