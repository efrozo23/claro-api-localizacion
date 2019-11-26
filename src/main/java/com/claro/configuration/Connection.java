package com.claro.configuration;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class Connection {


	@Bean(destroyMethod = "")
    @Qualifier("datasource1")
    @ConfigurationProperties(prefix = "claro.connection")
    @Primary
    public DataSource dataSourceIccid() {
        return DataSourceBuilder.create().type(BasicDataSource.class).build();
    }
	
    
 
}
