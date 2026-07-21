package com.tacomoloco.reportes.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.context.annotation.Bean;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor feignAuthInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth instanceof JwtAuthenticationToken) {
                    Jwt jwt = ((JwtAuthenticationToken) auth).getToken();
                    template.header("Authorization", "Bearer " + jwt.getTokenValue());
                }
            }
        };
    }
}
