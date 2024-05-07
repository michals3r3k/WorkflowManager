package com.example.workflowmanager.service.auth;

import com.example.workflowmanager.db.user.UserRepository;
import com.example.workflowmanager.entity.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentUserService
{
    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public static String getCurrentUserEmail()
    {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Optional<User> getCurrentUser()
    {
        String currentUserEmail = getCurrentUserEmail();
        return userRepository.findByEmail(currentUserEmail);
    }

}
