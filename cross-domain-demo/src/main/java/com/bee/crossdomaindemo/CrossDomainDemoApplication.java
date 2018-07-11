package com.bee.crossdomaindemo;

import com.os.crossdomain.CrossDomainMainServlet;
import com.os.crossdomain.CrossDomainSubServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CrossDomainDemoApplication {

    @Bean
    public ServletRegistrationBean crossDomainMainJsServlet() {
        ServletRegistrationBean registration= new ServletRegistrationBean(new CrossDomainMainServlet());
        registration.addUrlMappings("/cross-domain-main.js");
        registration.addUrlMappings("/cross-domain-main.html");
        return registration;
    }

    @Bean
    public ServletRegistrationBean crossDomainSubJsServlet() {
        ServletRegistrationBean registration= new ServletRegistrationBean(new CrossDomainSubServlet());
        registration.addUrlMappings("/cross-domain-sub.js");
        registration.addUrlMappings("/cross-domain-sub.html");
        return registration;
    }



    public static void main(String[] args) {
        SpringApplication.run(CrossDomainDemoApplication.class, args);
    }
}