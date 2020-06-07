package org.activiti.app.ui;

import org.activiti.app.conf.ApplicationConfiguration;
import org.activiti.app.servlet.ApiDispatcherServletConfiguration;
import org.activiti.app.servlet.AppDispatcherServletConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * 将war启动改成Spring Boot项目
 * @see modules/activiti-ui/activiti-app/src/main/webapp/WEB-INF/web.xml
 *      定义了listener {@link org.activiti.app.servlet.WebConfigurer}
 */
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        org.activiti.spring.boot.SecurityAutoConfiguration.class

})
@Import({ApplicationConfiguration.class})
public class AtcivitiUIApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AtcivitiUIApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(AtcivitiUIApplication.class);
    }

    @Bean
    public ServletRegistrationBean apiDispatcher(){
        DispatcherServlet api = new DispatcherServlet();
        api.setContextClass(AnnotationConfigWebApplicationContext.class);
        api.setContextConfigLocation(ApiDispatcherServletConfiguration.class.getName());

        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(api);
        registrationBean.addUrlMappings("/api/*");  // 设置路径映射
        registrationBean.setLoadOnStartup(1);       // 设置启动优先级
        registrationBean.setAsyncSupported(true);   // 开启异步支持
        registrationBean.setName("api");
        return registrationBean;
    }

    @Bean
    public ServletRegistrationBean appDispatcher(){
        DispatcherServlet app = new DispatcherServlet();
        app.setContextClass(AnnotationConfigWebApplicationContext.class);
        app.setContextConfigLocation(AppDispatcherServletConfiguration.class.getName());

        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(app);
        registrationBean.addUrlMappings("/app/*");  // 设置路径映射
        registrationBean.setLoadOnStartup(1);       // 设置启动优先级
        registrationBean.setAsyncSupported(true);   // 开启异步支持
        registrationBean.setName("app");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean openEntityManagerInViewFilter(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new OpenEntityManagerInViewFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("openEntityManagerInViewFilter");
        registrationBean.setOrder(-200);
        registrationBean.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC));
        return registrationBean;
    }
}
