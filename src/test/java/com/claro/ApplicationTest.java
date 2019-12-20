package com.claro;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.claro.dto.DataRequest;
import com.claro.dto.Request;


/**
 * 
 * @author Assert Solutions S.A.S
 *
 */
@RunWith(SpringRunner.class)
@Configuration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApplicationTest {

    private Request request;
    private final static String URL = "http://localhost:8081/localizacion/geoposicion/v1";
    
    private Logger log = LoggerFactory.getLogger(ApplicationTest.class);

    @Before
    public void iniciar() {
        request = new Request();
        DataRequest data = new DataRequest();
        data.setNumeroCelular("3103144478ww");
        request.setData(data);
    }

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testPost() throws Exception {
        // Call the REST API .
    	HttpHeaders headers = new HttpHeaders();
    	headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    	headers.add("ClientIDd", "545454554ff");
    	headers.add("NombreClientee", "Davivienda");
    	HttpEntity<Request> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);
        log.info("Body :{}", response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}