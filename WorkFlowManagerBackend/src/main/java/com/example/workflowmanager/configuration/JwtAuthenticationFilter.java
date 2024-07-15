package com.example.workflowmanager.configuration;

import com.example.workflowmanager.service.auth.CustomUserDetailsService;
import com.example.workflowmanager.service.auth.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public JwtAuthenticationFilter(CustomUserDetailsService userDetailsService,
        JwtService jwtService)
    {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        String authHeader = request.getHeader(AUTH_HEADER);
        if(authHeader == null || !authHeader.startsWith(BEARER_TOKEN_PREFIX))
        {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(BEARER_TOKEN_PREFIX.length());
        String email = jwtService.getEmail(token);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if(email != null && securityContext.getAuthentication() == null)
        {
            if(jwtService.isTokenValid(token, email))
            {
                String uri = request.getRequestURI();
                Long organizationIdOrNull = extractIdFromUri(uri, "organization");
                Long projectIdOrNull = extractIdFromUri(uri, "project");
                UserDetails userDetails = userDetailsService.loadUserByUsernameAndOrganizationId(email, organizationIdOrNull);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    new WebAuthenticationDetailsSource().buildDetails(request),
                    userDetails.getAuthorities());
                securityContext.setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private Long extractIdFromUri(String uri, String prefix) {
        int index = uri.indexOf(prefix + "/");
        if (index != -1) {
            index += prefix.length() + 1;
            int endIndex = uri.indexOf("/", index);
            if (endIndex == -1) {
                endIndex = uri.length();
            }
            String idStr = uri.substring(index, endIndex);
            try {
                return Long.valueOf(idStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

}
