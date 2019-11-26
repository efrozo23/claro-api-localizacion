package com.claro;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


/**
 *
 * @author Asssert Solutions S.A.S
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class Application{
	


    /**
     * A main method to start this application.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	

}
