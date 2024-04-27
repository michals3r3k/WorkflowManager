package com.example.workflowmanager.service.login;

import com.example.workflowmanager.db.user.UserRepository;
import com.example.workflowmanager.entity.user.User;
import com.google.common.collect.Sets;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@Service
public class RegisterService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(UserRepository userRepository,
        PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterServiceResult register(String email, String password)
    {
        Set<RegisterError> errors = getErrors(email);
        if(errors.isEmpty())
        {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }
        return new RegisterServiceResult(errors);
    }

    private Set<RegisterError> getErrors(String email)
    {
        User userOrNull = userRepository.findByEmail(email).orElse(null);
        if(userOrNull != null)
        {
            return EnumSet.of(RegisterError.EMAIL_BUSY);
        }
        return Collections.emptySet();
    }

    public enum RegisterError
    {
        EMAIL_BUSY
    }

    public static class RegisterServiceResult
    {
        private final Set<RegisterError> errors;

        private RegisterServiceResult(Set<RegisterError> errors)
        {
            this.errors = Sets.immutableEnumSet(errors);
        }

        public boolean isSuccess()
        {
            return errors.isEmpty();
        }

        public Set<RegisterError> getErrors()
        {
            return errors;
        }

    }

}
