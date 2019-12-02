package com.claro.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.claro.beans.ResponseHandler;
import com.claro.beans.ValidatorDate;

@Component
public class PICRoute extends RouteBuilder {

	public static final String PIC_ROUTE = "direct://pic-endpoint";
	private Logger logger = LoggerFactory.getLogger(PICRoute.class);

	public static final String AUDIT_DETAILS = "auditDetails";
	public static final String HEADERS_ERROR = "error";
	public static final String RESPONSE = "response";
	public static final String AUDIT_ROUTE = "direct://adutoria";

	@Override
	public void configure() throws Exception {
		

		from(PIC_ROUTE).routeId("PIC-SERVICE")
		.onException(Exception.class)
	     	.handled(true)
	     	.log(LoggingLevel.ERROR, log, "Ingreso por error: ${body}")
	        .log(LoggingLevel.ERROR, log, "Message: ${exception.message} ${exception.cause}")
	        .setProperty("exception", simple("message.response.error"))
	        .setProperty(LBSRoute.AUDIT_DETAILS, simple("${exception.message} || STATUS CODE: ${headers.CamelHttpResponseCode}"))
	        .choice()
	        	.when(simple("${headers.error} == 400 "))
	        		.log("Error 400")
	        		.setProperty("exception",  simple("message.response.otros"))
	        		.bean(ResponseHandler.class, "buildResponseError({{message.response.failed}},, ${property.exception})")
	        	.endChoice()
	        	.otherwise()
	        		.bean(ResponseHandler.class, "buildResponseError({{message.response.success.ok}},, ${property.exception})")
	        		.log(LoggingLevel.ERROR, log, "Exchange= ${exchangeProperty.procesoId} || Ingreso por otherwise : message: ${body} ")
	        	.endChoice()
	        .end()
	        .setProperty(RESPONSE, body())
	        .log(LoggingLevel.ERROR, log, "Exchange= ${exchangeProperty.procesoId} || Respuesta construida : message: ${body} ")
	        .to(AUDIT_ROUTE)
	        .end()
		
		
			.setHeader(Exchange.HTTP_METHOD, simple("{{pic.method}}"))
			.setHeader(Exchange.HTTP_URI, simple("{{pic.url}}{{pic.prefijo}}${exchangeProperty.min}"))
			.log(LoggingLevel.INFO, logger, "Invoking pic web service with: ${exchangeProperty.min}")
			.doTry()
				.to("http4:PICWS")
				//.setBody(constant("{\"roamingLocation\":{\"timestamp\":\"2019-11-19T00:00:00\",\"msisdn\":573228888376,\"vlrgt\":\"[TBA]\",\"country\":\"Colombia\"} }"))
				.setHeader("CamelHttpResponseCode", constant(200))
			.endDoTry()
			.doCatch(Exception.class)
				.log(LoggingLevel.ERROR, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = Error en el consumo del servicio PIC || Codigo: ${headers.CamelHttpResponseCode} ")
				.throwException(NullPointerException.class, "{{message.response.otros}}")
			.end()
			.choice()
				.when(simple("${headers.CamelHttpResponseCode} != 200 "))
					.log(LoggingLevel.ERROR, logger,
						"Exchange= ${exchangeProperty.procesoId} || mensage = Error en el consumo del servicio PIC || Codigo: ${headers.CamelHttpResponseCode} ")
					.setProperty(AUDIT_DETAILS, simple("SERVICIO NO DISPONIBLE || STATUS CODE: ${headers.CamelHttpResponseCode}"))
					.setHeader(HEADERS_ERROR, constant(400))
					.throwException(NullPointerException.class, "{{message.response.otros}}")
				.endChoice()
				.end()
				.choice()
					.when().jsonpath("$.[?(@.error)]")
						.choice()
							.when().jsonpath("$.error[?(@.code == 9000)]")
								.log("Error 9000")
								.setHeader("msgPicResponse", constant("message.response.pic.9000"))
							.when().jsonpath("$.error[?(@.code == 9001)]")
								.log("Error 9001")
								.setHeader("msgPicResponse", constant("message.response.pic.9001"))
							.when().jsonpath("$.error[?(@.code == 9002)]")
								.log("Error 9002")
								.setHeader("msgPicResponse", constant("message.response.pic.9002"))
							.when().jsonpath("$.error[?(@.code == 9003)]")
								.log("Error 9003")
								.setHeader("msgPicResponse", constant("message.response.pic.9003"))
							.when().jsonpath("$.error[?(@.code == 9004)]")
								.log("Error 9004")
								.setHeader("msgPicResponse", constant("message.response.pic.9004"))
							.when().jsonpath("$.error[?(@.code == 9005)]")
								.log("Error 9005")
								.setHeader("msgPicResponse", constant("message.response.pic.9005"))
							.endChoice()
						.bean(ResponseHandler.class, "buildResponseError({{message.response.success.ok}}, ${headers.msgPicResponse}, ${property.exception})")
						.setProperty(RESPONSE, body())
						.setHeader("isPIC", constant(true))
					.otherwise()
						.setProperty("codigoRespuesta", simple("{{message.response.success.ok}}"))
						.setProperty("mensajeRespuesta", simple("{{message.response.success.message}}"))
						.log(LoggingLevel.INFO, log,
									"Exchange= ${exchangeProperty.procesoId} || mensaje= Inicio a construir la respuesta")
						.removeHeaders("*")
						.bean(ResponseHandler.class, "buildPicResponse")
						.setProperty(RESPONSE, body())
						.setHeader("isPIC", constant(true))
					.end()
				.end();
	}

}
