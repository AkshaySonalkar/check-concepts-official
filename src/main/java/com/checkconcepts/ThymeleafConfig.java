package com.checkconcepts;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class ThymeleafConfig {

    @Bean
    public ClassLoaderTemplateResolver userTemplateResolver() {
        ClassLoaderTemplateResolver userTemplateResolver = new ClassLoaderTemplateResolver();
        userTemplateResolver.setPrefix("enduser/");
        userTemplateResolver.setSuffix(".html");
        userTemplateResolver.setTemplateMode(TemplateMode.HTML);
        userTemplateResolver.setCharacterEncoding("UTF-8");
        userTemplateResolver.setOrder(1);
        userTemplateResolver.setCheckExistence(true);

        return userTemplateResolver;
    }
    
    @Bean
    public ClassLoaderTemplateResolver staffTemplateResolver() {
        ClassLoaderTemplateResolver staffTemplateResolver = new ClassLoaderTemplateResolver();
        staffTemplateResolver.setPrefix("staff/");
        staffTemplateResolver.setSuffix(".html");
        staffTemplateResolver.setTemplateMode(TemplateMode.HTML);
        staffTemplateResolver.setCharacterEncoding("UTF-8");
        staffTemplateResolver.setOrder(2);
        staffTemplateResolver.setCheckExistence(true);

        return staffTemplateResolver;
    }
    
    @Bean
    public ClassLoaderTemplateResolver adminTemplateResolver() {
        ClassLoaderTemplateResolver adminTemplateResolver = new ClassLoaderTemplateResolver();
        adminTemplateResolver.setPrefix("admin/");
        adminTemplateResolver.setSuffix(".html");
        adminTemplateResolver.setTemplateMode(TemplateMode.HTML);
        adminTemplateResolver.setCharacterEncoding("UTF-8");
        adminTemplateResolver.setOrder(3);
        adminTemplateResolver.setCheckExistence(true);

        return adminTemplateResolver;
    }
}
