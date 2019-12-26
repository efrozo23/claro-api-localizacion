package com.claro.routes;

import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestHostNameResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.claro.beans.ResponseHandler;
import com.claro.configuration.JmsConfig;
import com.claro.dto.Request;
import com.claro.dto.Response;
import com.claro.processor.BeanValidatorProcess;

/**
 * 
 * @author Assert Solutions S.A.S
 *
 */
@Component
public class RestDslMainRoute extends RouteBuilder {

    @Value("${camel.component.servlet.mapping.context-path}")
    private String contextPath;

    @Autowired
    private Environment env;
    @Autowired
    private BeanValidatorProcess validatorProcess;
    
    private Logger log = LoggerFactory.getLogger(RestDslMainRoute.class);
    
    public  static final String RESPONSE ="response"; 
    
    public static final String REQUEST ="request";
    
    public static final String ID_CLIENTE ="idCliente";
    
    public static final String NOMBRE_CLIENTE ="nombreCliente";
    
    public  static final String AUDIT_ROUTE ="direct://adutoria";
    
    public  static final String DATA_PROPERTY ="dataR";
    
    @Override
    public void configure() throws Exception {
    // @formatter:off
        restConfiguration()
            .component("servlet")
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", "true")
            .enableCORS(true)
            .port(env.getProperty("server.port"))
            .hostNameResolver(RestHostNameResolver.localIp)
            .contextPath(contextPath.substring(0, contextPath.length() - 2))
            // turn on swagger api-doc
            .apiContextPath("/api-doc")
            .apiProperty("api.title",  env.getProperty("api.title"))
            .apiProperty("api.version", env.getProperty("api.version"))
            .apiProperty("supportedSubmitMethods", "[]");
        
        rest(env.getProperty("endpoint.api")).description(env.getProperty("api.description"))
            .consumes(MediaType.APPLICATION_JSON)
            .produces(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
        
       .post().description(env.getProperty("api.description.service"))
           .type(Request.class).description(env.getProperty("api.description.input.post"))	
           .outType(Response.class) 
           .to("direct:from-api");
        
        rest()
        	.consumes(MediaType.APPLICATION_JSON)
        	.produces(MediaType.APPLICATION_JSON)
        .get(env.getProperty("endpoint.api.get")).description(env.getProperty("endpoint.api.get.description"))
        .type(String.class).outType(String.class).to("direct://get")
        .get(env.getProperty("endpoint.api.geographicLocation")).type(String.class).outType(String.class).to("mock:ok");
        
        onException(Exception.class)
        .handled(true)
        .log(LoggingLevel.ERROR, log,"Error: ${exception.message}")
        .setBody(constant("ERROR-500"))
        	.end();
        
         from("direct:from-api").routeId("Ruta-Principal").streamCaching("true")
         .onException(BeanValidationException.class)
             .handled(true)
             .log(LoggingLevel.INFO, log, "INGRESO POR EL ERROR REQUEST: ${body}")
             .setProperty(REQUEST, body())
             .log(LoggingLevel.ERROR, log, "Error validando campos : message: ${exception.message} cause: ${exception.cause} ")
             .setProperty("exception", simple("${exception.message}"))
             .process(validatorProcess)
             .bean(ResponseHandler.class, "buildResponseError({{message.response.success.ok}}, message.response.bean.validator, ${property.exception})")
             .setProperty(LBSRoute.AUDIT_DETAILS, simple("${exception.message}"))
             .setProperty(RESPONSE, body())
             .to(AUDIT_ROUTE)
         .end()
         .onException(Exception.class)
         	.handled(true)
         	.log(LoggingLevel.ERROR, log, "Ingreso por error : Valores nulos ${body}")
            .log(LoggingLevel.ERROR, log, "Error validando campos : message: ${exception.message} ${exception.cause} ")
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
         	.setProperty("procesoId", simple("${exchangeId}"))
         	.setProperty(ID_CLIENTE).simple("${headers.ClientID}")
         	.setProperty(NOMBRE_CLIENTE).simple("${headers.NombreCliente}")
         	.setProperty(LBSRoute.AUDIT_DETAILS, constant("OK"))
         	.log(LoggingLevel.INFO, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= Inicia el consumo del servicio ")
         	.log(LoggingLevel.DEBUG, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= Request inicial || body = ${body}")
         	.process(exchange->{
        		HttpServletRequest req = exchange.getIn().getBody(HttpServletRequest.class);
        		InetAddress localhost = InetAddress.getLocalHost();
        		log.info("Ip local:{}",localhost.getHostAddress());
        		if(req !=null) {
        			log.info("Ip config:{}",req.getRemoteAddr());
        		}
        	})
         .log(LoggingLevel.DEBUG, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= Headers recibidos || body = ${headers}")
         .removeHeaders("*")
         .to("bean-validator://validatorFields")
         .setProperty(REQUEST, body())
         .marshal().json(JsonLibrary.Jackson)        
         .setProperty("min").jsonpath("$.data.numeroCelular")

         .log(LoggingLevel.DEBUG, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= Valido el request || Request: ${body}")
         .removeHeaders("*")
         .log(LoggingLevel.DEBUG, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= Procede a consultar LBS || Detalle = Número min: ${exchangeProperty.min}")
         
         .choice()
         	.when(simple("${exchangeProperty.min.startsWith('{{lbs.start.number}}')} == false"))
         		.log(LoggingLevel.ERROR, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= El número no comienza con 3 || Detalle = Número min:${exchangeProperty.min} ")
         		.throwException(Exception.class, "El número no inicia en : {{lbs.start.number}}")
         	.endChoice()
         .end()
         .to(LBSRoute.LBS_ROUTE)
         .setProperty("codigoRespuesta", simple("{{message.response.success.ok}}"))
         .setProperty("mensajeRespuesta", simple("{{message.response.success.message}}"))
         .log(LoggingLevel.INFO, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= Inicio a construir la respuesta")
         .removeHeaders("*", "isPIC")
         .choice()
         	.when().simple("${headers.isPIC} != true")
         		.bean(ResponseHandler.class, "buildResponse")
         		.setProperty(RESPONSE,body())
         .end()
         .log(LoggingLevel.INFO, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= Inicio a enviar mensaje al broker")      	
         .to(AUDIT_ROUTE)
        	
         .log(LoggingLevel.INFO, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= Finalizo el proceso")
        .end();
         
         from(AUDIT_ROUTE).routeId("Auditoria").streamCaching("true")
         .log(LoggingLevel.INFO	, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= Inicia a conectarse al broker")
         	.doTry()
         		.bean(JmsConfig.class,"generateMenssage")
         		.log(LoggingLevel.DEBUG	, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= Enviando mensaje al broker || Mensaje= ${body}")
         		.marshal().json(JsonLibrary.Jackson)
         		.log(LoggingLevel.DEBUG	, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= Termino de generar mensaje al broker || Mensaje= ${body}")
         		.bean(JmsConfig.class) 
         		
         	.endDoTry()
         	.doCatch(Exception.class)
    		.log(LoggingLevel.ERROR, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= Error al enviar mensaje al broker || Detalle= ${exception.message}")
    		
    		.doFinally()
         		.setBody(exchangeProperty(RESPONSE))
         		.marshal().json(JsonLibrary.Jackson)
         		.log(LoggingLevel.INFO	, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= Respuesta creada || Detalle ${body}")
         		.unmarshal().json(JsonLibrary.Jackson)
         		.log(LoggingLevel.INFO	, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= Finalizo envío de mensaje")
         		.removeHeader("*")
         		.log(LoggingLevel.INFO	, log, "Exchange= ${exchangeProperty.procesoId} || mensaje= ${headers}")
         		.setHeader("CamelHttpResponseCode", constant(200))
         	.end()
         .end();
         
         from("direct://get")
         	.setBody(constant("OK"))
         .end();
        // @formatter:on
    }

}
