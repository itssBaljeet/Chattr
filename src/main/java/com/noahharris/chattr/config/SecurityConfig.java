package com.noahharris.chattr.config;

import com.noahharris.chattr.service.ChattrUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ChattrUserDetailsService chattrUserDetailsService;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    @Autowired
    public SecurityConfig(ChattrUserDetailsService chattrUserDetailsService, CustomLoginSuccessHandler customLoginSuccessHandler) {
        this.chattrUserDetailsService = chattrUserDetailsService;
        this.customLoginSuccessHandler = customLoginSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomLogoutSuccessHandler customLogoutSuccessHandler, CustomLoginFailureHandler customLoginFailureHandler) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/users/login", "/users/register", "/logout", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/users/login")
                        .loginProcessingUrl("/login")
                        .successHandler(customLoginSuccessHandler)
                        .failureHandler(customLoginFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
