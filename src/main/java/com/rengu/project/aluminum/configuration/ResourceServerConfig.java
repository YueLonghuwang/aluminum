package com.rengu.project.aluminum.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * com.rengu.project.aluminum.configuration
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        // 放行swagger2文档页面
        // 放行所有Option请求
        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll();
        http.authorizeRequests().antMatchers("/modelResource/**").permitAll();
        http.authorizeRequests().antMatchers("/standards/**").permitAll();
        http.authorizeRequests().antMatchers("/toolsAndSoftware/**").permitAll();
        http.authorizeRequests().antMatchers("/algorithmAndServer/**").permitAll();
        http.authorizeRequests().antMatchers("/tasks", "/process", "/flowable-modeler").permitAll();
        http.authorizeRequests().antMatchers("/swagger-ui.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs").permitAll();
        http.authorizeRequests().antMatchers("/ALUMINUM/**").permitAll();
        http.authorizeRequests().anyRequest().authenticated();
    }


}