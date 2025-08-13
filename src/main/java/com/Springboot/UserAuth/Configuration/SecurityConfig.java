package com.Springboot.UserAuth.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.Springboot.UserAuth.Security.JsonUsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JsonUsernamePasswordAuthenticationFilter jsonFilter =
            new JsonUsernamePasswordAuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)));
        jsonFilter.setAuthenticationSuccessHandler(customSuccessHandler());

        jsonFilter.setAuthenticationFailureHandler((request, response, exception) -> {
            Object emailAttr = request.getAttribute("email");
            String email = emailAttr != null ? emailAttr.toString() : "unknown";

            System.out.println("❌ Login failed for email: " + email + " – " + exception.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Login failed\", \"email\": \"" + email + "\", \"reason\": \"" + exception.getMessage() + "\"}");
        });

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // ✅ Public routes
                .requestMatchers(
                    "/", "/index", "/home", "/aboutus", "/contactus", "/services", "/services/**","/careers",
                    "/DigitalDesignServices","/DigitalVerificationServices","/SoftwareServices","/DftServices", "/login", "/header.html", "/register"
                ).permitAll()

                // ✅ Static resources (needed for JS/CSS/images/fonts)
                .requestMatchers("/css/**","/videos/**", "/js/**", "/images/**", "/fonts/**", "/webjars/**", "/static/**","/svg/**").permitAll()

                // ✅ Role-based protected routes
                .requestMatchers("/admin/**").hasAuthority("Admin")
                .requestMatchers("/manager/**").hasAuthority("Manager")
                .requestMatchers("/employee/**").hasAuthority("Employee")
                .requestMatchers("/hr/**").hasAuthority("HR")

                // ✅ Anything else must be authenticated
                .anyRequest().authenticated()
            )

            .addFilterBefore(jsonFilter, UsernamePasswordAuthenticationFilter.class)

            .formLogin(form -> form
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .failureHandler((request, response, exception) -> {
                    String email = request.getParameter("email");
                    System.out.println("❌ Login failed for email: " + email + " – Bad credentials");
                    response.sendRedirect("/login?error=true");
                })
                .successHandler(customSuccessHandler())
            )

            .logout(logout -> logout
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessHandler((request, response, authentication) -> {
                    if (authentication != null) {
                        System.out.println("🚪 User logged out: " + authentication.getName());
                    } else {
                        System.out.println("🚪 Logout called but no user was authenticated.");
                    }
                    response.sendRedirect("/login?logout");
                })
                .permitAll()
            );

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
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
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            String email = authentication.getName();
            System.out.println("✅ User logged in: " + email + " with role " + role);

            String contentType = request.getContentType();
            boolean isJson = contentType != null && contentType.contains("application/json");

            if (isJson) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                String jsonResponse = String.format(
                    "{ \"message\": \"Login successful\", \"email\": \"%s\", \"role\": \"%s\" }",
                    email, role
                );
                response.getWriter().write(jsonResponse);
            } else {
                switch (role) {
                    case "Admin" -> response.sendRedirect("/admin/dashboard");
                    case "Manager" -> response.sendRedirect("/manager/dashboard");
                    case "Employee" -> response.sendRedirect("/employee/dashboard");
                    case "HR" -> response.sendRedirect("/hr/dashboard");
                    default -> response.sendRedirect("/login");
                }
            }
        };
    }
}
