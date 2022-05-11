package com.company.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecuredFilterConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public FilterRegistrationBean filterRegistrationBeanRegion() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(jwtFilter);
        bean.addUrlPatterns("/region/adminjon/*");
        bean.addUrlPatterns("/category/adminjon/*");
        bean.addUrlPatterns("/profile/adminjon/*");
        bean.addUrlPatterns("/profile/image/*");
        bean.addUrlPatterns("/article_type/adminjon/*");
        bean.addUrlPatterns("/article/adminjon/*");
        bean.addUrlPatterns("/attach/adminjon/*");
        bean.addUrlPatterns("/email/adminjon/*");
        bean.addUrlPatterns("/comment/public/*");
        bean.addUrlPatterns("/comment/adminjon/*");
        bean.addUrlPatterns("/like/public/*");
        bean.addUrlPatterns("/like/adminjon/*");
        bean.addUrlPatterns("/tag/adminjon/*");
        return bean;
    }

}
