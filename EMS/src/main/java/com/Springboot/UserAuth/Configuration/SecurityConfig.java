package com.Springboot.UserAuth.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers("/register").hasAuthority("Admin") 
            	    .requestMatchers("/login").permitAll()
            	    .requestMatchers("/admin/**").hasAuthority("Admin")
            	    .requestMatchers("/manager/**").hasAuthority("Manager")
            	    .requestMatchers("/employee/**").hasAuthority("Employee")
            	    .requestMatchers("/hr/**").hasAuthority("Hr")
            	    .anyRequest().authenticated()
            	)

            .formLogin(form -> form
            	    .loginPage("/login").permitAll() // Admin login
            	    .loginProcessingUrl("/login")
            	    .usernameParameter("email")
            	    .passwordParameter("password")
            	    .failureUrl("/login?error=true")
            	    .successHandler(customSuccessHandler())
            	)

            .logout(logout -> logout
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) 
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    AuthenticationSuccessHandler customSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                String role = authentication.getAuthorities().iterator().next().getAuthority();
                
                if ("Admin".equals(role)) {
                    response.sendRedirect("/admin/dashboard");
                } else if ("Manager".equals(role)) {
                    response.sendRedirect("/manager/dashboard");
                } else if ("Employee".equals(role)) {
                    response.sendRedirect("/employee/dashboard");
                } else if ("Hr".equals(role)) {
                    response.sendRedirect("/hr/dashboard");
                } else {
                    response.sendRedirect("/home");
                }
            }
        };
    }
}
