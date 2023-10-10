package com.elearning.configs.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.security.Security;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfiguration {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    @Bean
    public JwtAuthenticationFilter jwtAuthFilter(){
        return new JwtAuthenticationFilter(exceptionResolver);
    }
    private final AuthenticationProvider authenticationProvider;
//    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .antMatchers(
                        "/**",
                        "/api/user/password/reset",
                        "/api/auth/**",
                        "/api/category/**",
                        "/api/course/**",
                        "/api/file-relationship/**",
                        "/api-docs/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-ui.html"
                ).permitAll()
                .antMatchers(HttpMethod.POST, "/api/user/password/reset").permitAll()
//                .antMatchers("/test/users").hasAnyRole(ROLE_ADMIN.name())
//                .antMatchers("/api/user/**").permitAll()
                .anyRequest().authenticated()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);


//                .logout()
//                .logoutUrl("/api/auth/logout")
//                .addLogoutHandler(logoutHandler)
//                .logoutSuccessHandler((
//                        request, response, authentication) ->
//                        SecurityContextHolder.clearContext()
//                );
        return http.build();
    }
}
