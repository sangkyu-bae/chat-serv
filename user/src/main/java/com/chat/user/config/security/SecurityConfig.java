package com.chat.user.config.security;

//import com.chat.user.module.jwt.JwtAuthenticationFilter;
import com.chat.user.module.jwt.TokenManager;
import com.chat.user.module.loign.LoginFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenManager tokenService;
    private final AuthenticationConfiguration authenticationConfiguration;

    private final ObjectMapper objectMapper;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-ui/index.html",
                "/v3/api-docs/**",
                "/v3/**",
                "/error"
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        LoginFilter loginFilter = new LoginFilter(tokenService,authenticationManager(),objectMapper);
        loginFilter.setFilterProcessesUrl("/api/login");
        loginFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api/public/**").permitAll()
//                        .requestMatchers("/swagger-ui/**","/v3/**","/v3/api-docs","/v3/api-docs/**").permitAll()
//                        .requestMatchers("/api/account/**").permitAll()
                                .requestMatchers(
                                        "/api/auth/**",
                                        "/api/public/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/swagger-ui/index.html",
                                        "/v3/api-docs/**",
                                        "/v3/**",
                                        "/api/account/**",
                                        "/error",
                                        "/favicon.ico",
                                        "/.well-known/**",
                                        "/.well-known/appspecific/com.chrome.devtools.json"
                                ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilter(loginFilter)
        ;
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
