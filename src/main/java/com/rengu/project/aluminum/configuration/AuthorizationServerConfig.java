package com.rengu.project.aluminum.configuration;

import com.rengu.project.aluminum.ApplicationConfig;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * com.rengu.project.aluminum.configuration
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final ApplicationConfig applicationConfig;

    public AuthorizationServerConfig(AuthenticationManager authenticationManager, UserService userService, ApplicationConfig applicationConfig) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.applicationConfig = applicationConfig;
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(applicationConfig.getOAUTH_JWT_SIGNINGKEY());
        return jwtAccessTokenConverter;
    }

    @Bean
    public JwtTokenStore jwtTokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients();
        security.tokenKeyAccess("permitAll()");
        security.checkTokenAccess("isAuthenticated()");
        super.configure(security);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(applicationConfig.getOAUTH_CLIENT_ID())
                .authorizedGrantTypes("implicit", "authorization_code", "client_credentials", "refresh_token", "password")
                .secret(new BCryptPasswordEncoder().encode(applicationConfig.getOAUTH_CLIENT_SECRET()))
                .scopes(applicationConfig.getOAUTH_CLIENT_SCOPES());
        super.configure(clients);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
        endpoints.authenticationManager(authenticationManager);
        endpoints.accessTokenConverter(accessTokenConverter());
        endpoints.tokenStore(jwtTokenStore());
        endpoints.tokenEnhancer(tokenEnhancerChain);
        endpoints.userDetailsService(userService);
        super.configure(endpoints);
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }


    public class CustomTokenEnhancer implements TokenEnhancer {
        @Override
        public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
            UserEntity loginUsers = userService.getUserByUsername(authentication.getName());
            Map<String, Object> additionalInfo = new HashMap<>();
            additionalInfo.put("userId", loginUsers.getId());
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            return accessToken;
        }
    }
}

