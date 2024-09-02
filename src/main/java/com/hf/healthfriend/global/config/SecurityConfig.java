package com.hf.healthfriend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hf.healthfriend.auth.itself.filter.CustomJsonUserPasswordAuthenticationFilter;
import com.hf.healthfriend.auth.itself.handler.LoginFailureHandler;
import com.hf.healthfriend.auth.itself.handler.LoginSuccessHandler;
import com.hf.healthfriend.auth.itself.service.LoginService;
import com.hf.healthfriend.auth.jwt.filter.JwtAuthorizationFilter;
import com.hf.healthfriend.auth.jwt.filter.JwtExceptionHandlerFilter;
import com.hf.healthfriend.auth.oauth2.handler.CustomOauth2SuccessHandler;
import com.hf.healthfriend.auth.oauth2.service.CustomOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] WHITE_LIST = {
            "/error",
            "/oauth/code/*",
            "/swagger-ui/**",

            // TODO: 해당 endpoint 확인 후 삭제할 수 있음
            "/login",
            "/api/member/**",
            "/api/jwt/reissue",
            "/v3/**"
    };

    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final CustomOauth2UserService customOauth2UserService;
    private final CustomOauth2SuccessHandler customOauth2SuccessHandler;
    private final LoginService loginService;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final JwtExceptionHandlerFilter jwtExceptionHandlerFilter;

    private final OpaqueTokenIntrospector opaqueTokenIntrospector;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return
                http
                        .cors(corsCustomizer ->corsCustomizer.configurationSource(corsConfigurationSource()))
                        .csrf(AbstractHttpConfigurer::disable)
                        .formLogin(AbstractHttpConfigurer::disable)
                        .httpBasic(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests((auth) -> auth
                                .requestMatchers(WHITE_LIST).permitAll()
                                .anyRequest().authenticated()
                        )
                        .oauth2ResourceServer((oauth) ->
                                oauth.opaqueToken((opaqueToken) ->
                                        opaqueToken.introspector(this.opaqueTokenIntrospector)))
                        .oauth2Login((oauth2) -> oauth2
                                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOauth2UserService))
                                .successHandler(customOauth2SuccessHandler))
                        .addFilterAfter(customJsonUserPasswordAuthenticationFilter(), LogoutFilter.class)
                        .addFilterBefore(jwtAuthorizationFilter, CustomJsonUserPasswordAuthenticationFilter.class)
                        .addFilterBefore(jwtExceptionHandlerFilter, JwtAuthorizationFilter.class)
                        .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        // config.addAllowedOriginPattern("http://localhost:3000/reissue");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setMaxAge(3600L); // 1시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider
                = new DaoAuthenticationProvider(passwordEncoder);

        provider.setUserDetailsService(loginService);

        return new ProviderManager(provider);
    }

    @Bean
    public CustomJsonUserPasswordAuthenticationFilter customJsonUserPasswordAuthenticationFilter() {
        CustomJsonUserPasswordAuthenticationFilter customJsonUserPasswordAuthenticationFilter
                = new CustomJsonUserPasswordAuthenticationFilter(objectMapper);

        customJsonUserPasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        customJsonUserPasswordAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        customJsonUserPasswordAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler);

        return customJsonUserPasswordAuthenticationFilter;
    }
}

