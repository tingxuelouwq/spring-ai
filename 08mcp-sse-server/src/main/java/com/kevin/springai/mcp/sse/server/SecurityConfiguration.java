package com.kevin.springai.mcp.sse.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer.authorizationServer;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 所有请求都要经过身份认证
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                // 启用授权服务器，暴露/oauth2/token等端点
                .with(authorizationServer(), Customizer.withDefaults())
                // 启用资源服务器，验证请求中的JWT Token
                .oauth2ResourceServer(resource -> resource.jwt(Customizer.withDefaults()))
                // 关闭CSRF（跨站请求伪造防护），因为MCP不是给浏览器直接使用的，因此无需开启
                .csrf(CsrfConfigurer::disable)
                // 打开CORS（跨域资源共享）
                .cors(Customizer.withDefaults())
                .build();
    }
}