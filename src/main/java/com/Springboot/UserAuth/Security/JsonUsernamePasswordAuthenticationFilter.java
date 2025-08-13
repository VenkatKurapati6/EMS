package com.Springboot.UserAuth.Security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public JsonUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/login"); 
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        System.out.println("🚦 Entered attemptAuthentication()");
        System.out.println("📦 Request Content-Type: " + request.getContentType());

        if (request.getContentType() != null &&
            request.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)) {

            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(request.getInputStream());

                String email = json.has("email") ? json.get("email").asText() : null;
                String password = json.has("password") ? json.get("password").asText() : null;
                
                request.setAttribute("email", email);

                System.out.println("📨 JSON Body Parsed:");
                System.out.println("   📧 Email: " + email);
                System.out.println("   🔑 Password: " + password);

                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken(email, password);

                setDetails(request, token);
                System.out.println("🔐 Attempting authentication with token...");
                return this.getAuthenticationManager().authenticate(token);

            } catch (IOException e) {
                System.out.println("❌ Failed to parse JSON body: " + e.getMessage());
                throw new RuntimeException("Failed to parse JSON login request", e);
            }
        }

        System.out.println("💡 Falling back to form login");
        return super.attemptAuthentication(request, response);
    }
    
    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return "/login".equals(request.getServletPath()) &&
               "POST".equalsIgnoreCase(request.getMethod()) &&
               request.getContentType() != null &&
               request.getContentType().contains("application/json");
    }

}
