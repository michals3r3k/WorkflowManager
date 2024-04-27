package com.example.workflowmanager.service.login;

import com.example.workflowmanager.db.user.UserRepository;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.auth.jwt.JwtService;
import com.google.common.collect.Sets;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@Service
public class LoginService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginService(UserRepository userRepository,
        PasswordEncoder passwordEncoder, JwtService jwtService)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginServiceResult login(String email, String password)
    {
        User userOrNull = userRepository.findByEmail(email).orElse(null);
        Set<LoginError> errors = getErrors(userOrNull, password);
        if(!errors.isEmpty())
        {
            return new LoginServiceResult(null, errors);
        }
        String token = jwtService.generateToken(email);
        return new LoginServiceResult(token, errors);
    }

    private Set<LoginError> getErrors(User userOrNull, String password)
    {
        if(userOrNull == null)
        {
            return EnumSet.of(LoginError.USER_NOT_EXISTS);
        }
        if(!passwordEncoder.matches(password, userOrNull.getPassword()))
        {
            return EnumSet.of(LoginError.INCORRECT_PASSWORD);
        }
        return Collections.emptySet();
    }

    public enum LoginError
    {
        USER_NOT_EXISTS,
        INCORRECT_PASSWORD;
    }

    public static class LoginServiceResult
    {
        private final String token;
        private final Set<LoginError> errors;

        private LoginServiceResult(String token, Set<LoginError> errors)
        {
            this.token = token;
            this.errors = Sets.immutableEnumSet(errors);
        }

        public boolean isSuccess()
        {
            return errors.isEmpty();
        }

        public Set<LoginError> getErrors()
        {
            return errors;
        }

        public String getToken()
        {
            return token;
        }


    }

}
