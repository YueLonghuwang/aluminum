package com.rengu.project.aluminum.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * author : yaojiahao
 * Date: 2019/6/17 10:24
 **/
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // config.enableSimpleBroker("/deviceInfo", "/onlineDevice", "/deployProgress");
        config.enableSimpleBroker("/userInfo", "/personalInfo", "/audit");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ALUMINUM").setAllowedOrigins("*").withSockJS();
    }
}
