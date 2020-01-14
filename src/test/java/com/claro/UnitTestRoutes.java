package com.claro;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.camel.BeanInject;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import com.claro.routes.LBSRoute;
import com.claro.routes.PICRoute;
import com.claro.routes.TransitionRoute;




@RunWith(CamelSpringBootRunner.class)
@MockEndpoints("log:*")
@UseAdviceWith
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UnitTestRoutes {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@BeanInject
	protected CamelContext camelContext2;
	
	@Test
	public void testLBSTime() throws Exception {
		camelContext2.getRouteDefinition("SearchCoordinates").adviceWith(camelContext2, new AdviceWithRouteBuilder() {
	          @Override
	          public void configure() throws Exception {
	            // send the outgoing message to mock
	        	//Ok, funcionan los dos.
//	            weaveByToUri("direct:test-route").replace().inOut("mock:routeB").setBody(simple("{{api.wallet.descripcion}}"));
	            interceptSendToEndpoint(PICRoute.PIC_ROUTE).skipSendToOriginalEndpoint().log("Inicio a buscar en PIC").setBody(constant("OK"));
	            interceptSendToEndpoint(TransitionRoute.ROUTE_UBICACITION).skipSendToOriginalEndpoint().log("Inicio a Homologar coordenadas");
	          }
	        });
			camelContext2.start();
			ProducerTemplate pT = camelContext2.createProducerTemplate();
			String body = "<svc_result ver=\"3.2.0\"><slia ver=\"3.0.0\"><pos><msid enc=\"ASC\" type=\"MSISDN\">573004186582</msid><pd><time utc_off=\"+0000\">20200114081948</time><shape><EllipticalArea><coord><X>4 39 6.086597 N</X><Y>74 6 21.529427 W</Y></coord><angle>0.0</angle><semiMajor>19.0</semiMajor><semiMinor>19.0</semiMinor></EllipticalArea></shape></pd></pos></slia></svc_result>";
			Object ret = pT.requestBody(LBSRoute.LBS_COORDINATES, body);
			logger.info("Objeto obtenido:{}" , ret);
			assertThat("OK").isEqualTo(ret);
		
	}
	
	@Test
	public void testLBSTimeMenor() throws Exception {
		camelContext2.getRouteDefinition("SearchCoordinates").adviceWith(camelContext2, new AdviceWithRouteBuilder() {
	          @Override
	          public void configure() throws Exception {
	            // send the outgoing message to mock
	        	//Ok, funcionan los dos.
//	            weaveByToUri("direct:test-route").replace().inOut("mock:routeB").setBody(simple("{{api.wallet.descripcion}}"));
	            interceptSendToEndpoint(PICRoute.PIC_ROUTE).skipSendToOriginalEndpoint().log("NO DEBERIA INGRESAR").setBody(constant("OK"));
	            interceptSendToEndpoint(TransitionRoute.ROUTE_UBICACITION).skipSendToOriginalEndpoint().log("Inicio a Homologar coordenadas").setBody(constant("ok-dos"));
	          }
	        });
			camelContext2.start();
			ProducerTemplate pT = camelContext2.createProducerTemplate();
			String body = "<svc_result ver=\"3.2.0\"><slia ver=\"3.0.0\"><pos><msid enc=\"ASC\" type=\"MSISDN\">573004186582</msid><pd><time utc_off=\"+0000\">20200114113048</time><shape><EllipticalArea><coord><X>4 39 6.086597 N</X><Y>74 6 21.529427 W</Y></coord><angle>0.0</angle><semiMajor>19.0</semiMajor><semiMinor>19.0</semiMinor></EllipticalArea></shape></pd></pos></slia></svc_result>";
			Object ret = pT.requestBody(LBSRoute.LBS_COORDINATES, body);
			logger.info("Objeto obtenido:{}" , ret);
			assertThat("ok-dos").isEqualTo(ret);
		
	}
}
