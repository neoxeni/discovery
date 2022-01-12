package com.mercury.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"com.mercury.discovery"}, nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
@EnableScheduling
@EnableCaching
public class DiscoveryApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryApplication.class, args);
    }

    public DiscoveryApplication() {
        super();
        // Cannot forward to error page for request [/base/menu/index] as the response has already been committed.
        // As a result, the response may have the wrong status code.
        // If your application is running on WebSphere Application Server you may be able to resolve
        // this problem by setting com.ibm.ws.webcontainer.invokeFlushAfterService to false
        setRegisterErrorPageFilter(false); // <- this one
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DiscoveryApplication.class);
    }
}
