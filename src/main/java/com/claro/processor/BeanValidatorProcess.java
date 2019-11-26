package com.claro.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.claro.routes.RestDslMainRoute;

@Component
public class BeanValidatorProcess implements Processor {

    @Autowired
    private Environment env;
    private Logger log = LoggerFactory.getLogger(RestDslMainRoute.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        String exception = (String) exchange.getProperty("exception");
        String message = "";
        String field = "";
        Map<String, String> params = new HashMap<>();
        Matcher matcher = Pattern.compile("\\[([^\\]]+)").matcher(exception);

        List<String> tags = new ArrayList<>();

        int pos = -1;
        try {
            while (matcher.find(pos + 1)) {
                pos = matcher.start();
                tags.add(matcher.group(1));
            }
            if (Objects.nonNull(tags) && tags.size() != 0) {
                message = tags.get(0);
                String prueba[] = message.split(";");
                for(String mapa : prueba) {
                    String valor[] = mapa.split(":");
                    if(Objects.nonNull(valor[0]) && Objects.nonNull(valor[1])) {
                        params.put(valor[0].trim(), valor[1].trim());
                        if(params.containsKey("constraint")) {
                            break;
                        }
                    }                    
                }
            }
        } catch (Exception e) {
            log.error("Error validando respuesta de campos obligatorios:{} " ,e.getMessage());
        }
        
        field = params.get("constraint");
        message = env.getProperty(field);
        exchange.setProperty("exception", field);
    }

}
