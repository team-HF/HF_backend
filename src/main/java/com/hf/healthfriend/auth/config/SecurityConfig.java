package com.hf.healthfriend.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hf.healthfriend.auth.exceptionhandling.AuthenticationExceptionHandler;
import com.hf.healthfriend.auth.filter.AccessControlFilter;
import com.hf.healthfriend.auth.filter.AccessDeniedExceptionResolverFilter;
import com.hf.healthfriend.auth.filter.AuthExceptionHandlerFilter;
import com.hf.healthfriend.domain.member.constant.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] WHITE_LIST = {
            "/",
            "/error",
            "/oauth/code/*",
            "/swagger-ui/**",
            "/actuator/**",
            "/favicon.ico",
            "/oauth/token/**",

            // TODO: 해당 endpoint 확인 후 삭제할 수 있음
            "/login",
            "/api/member/**",
            "/api/jwt/reissue",
            "/v3/**"
    };

    private final OpaqueTokenIntrospector opaqueTokenIntrospector;
    private final ObjectMapper objectMapper;
    private final List<AuthenticationExceptionHandler> exceptionHandlers;
    private final ApplicationContext context;

    @Bean
    @Profile("!no-auth")
    public AccessControlFilter accessControlFilter() {
        return new AccessControlFilter(this.context);
    }

    @Bean
    @Profile("!no-auth")
    public AccessDeniedExceptionResolverFilter accessDeniedExceptionResolverFilter() {
        return new AccessDeniedExceptionResolverFilter(this.objectMapper);
    }

    @Bean
    @Profile("!no-auth")
    public AuthExceptionHandlerFilter authExceptionHandlerFilter() {
        return new AuthExceptionHandlerFilter(this.objectMapper, this.exceptionHandlers);
    }

    @Bean
    @Profile("!no-auth")
    public SecurityFilterChain domainSecurityFilterChain(HttpSecurity http) throws Exception {
        return
                http
                        .cors(corsCustomizer ->corsCustomizer.configurationSource(corsConfigurationSource()))
                        .csrf(AbstractHttpConfigurer::disable) // TODO: 추후 보안 정책에 따라 CSRF 방지 활용 가능
                        .formLogin(AbstractHttpConfigurer::disable)
                        .httpBasic(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests((auth) -> auth
                                .requestMatchers(WHITE_LIST).permitAll()
                                .requestMatchers(HttpMethod.POST, "/members").hasAnyRole(
                                        Role.ROLE_NON_MEMBER.roleName(), Role.ROLE_MEMBER.roleName()
                                )
                                .anyRequest().hasAnyRole(Role.ROLE_ADMIN.roleName(), Role.ROLE_MEMBER.roleName())
                        )
                        .oauth2ResourceServer((oauth) ->
                                oauth.opaqueToken((opaqueToken) ->
                                        opaqueToken.introspector(this.opaqueTokenIntrospector)))
                        .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .addFilterBefore(authExceptionHandlerFilter(), BearerTokenAuthenticationFilter.class)
                        .addFilterBefore(accessDeniedExceptionResolverFilter(), AuthorizationFilter.class)
                        .addFilterAfter(accessControlFilter(), AuthorizationFilter.class)
                        .build();
    }

    @Bean
    @Profile("no-auth")
    public SecurityFilterChain noAuthCheckSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(corsCustomizer ->corsCustomizer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((request) -> request.anyRequest().permitAll())
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8081"));
        // config.addAllowedOriginPattern("http://localhost:3000/reissue");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "Cookie"));
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setMaxAge(3600L); // 1시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

