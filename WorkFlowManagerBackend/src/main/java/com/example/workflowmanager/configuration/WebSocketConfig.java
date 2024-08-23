package com.example.workflowmanager.configuration;

import com.example.workflowmanager.service.auth.CustomUserDetailsService;
import com.example.workflowmanager.service.auth.jwt.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

import static com.example.workflowmanager.configuration.JwtAuthenticationFilter.AUTH_HEADER;
import static com.example.workflowmanager.configuration.JwtAuthenticationFilter.BEARER_TOKEN_PREFIX;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer
{
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public WebSocketConfig(final CustomUserDetailsService userDetailsService,
        final JwtService jwtService)
    {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    final List<String> authHeaders = accessor.getNativeHeader(AUTH_HEADER);
                    final SecurityContext securityContext = SecurityContextHolder.getContext();
                    if(authHeaders != null && !authHeaders.isEmpty())
                    {
                        final String authHeader = authHeaders.get(0);
                        if(authHeader.startsWith(BEARER_TOKEN_PREFIX))
                        {
                            String token = authHeader.substring(BEARER_TOKEN_PREFIX.length());
                            String email = jwtService.getEmail(token);
                            if (email != null && securityContext.getAuthentication() == null && jwtService.isTokenValid(token, email)) {
                                final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                                );
                                securityContext.setAuthentication(authentication);
                                accessor.setUser(authentication);
                            }
                        }
                    }
                }
                return message;
            }
        });
    }

}
