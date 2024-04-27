package com.example.workflowmanager.service.auth;

import com.example.workflowmanager.db.user.UserRepository;
import com.example.workflowmanager.entity.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.NoSuchElementException;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        User user = userRepository.findByEmail(email)
            .orElseThrow(NoSuchElementException::new);
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), Collections.emptySet());
    }

}
