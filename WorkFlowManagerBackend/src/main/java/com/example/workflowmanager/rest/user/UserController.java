package com.example.workflowmanager.rest.user;

import com.example.workflowmanager.db.user.UserRepository;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.auth.CurrentUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class UserController
{
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public UserController(final UserRepository userRepository,
        final CurrentUserService currentUserService)
    {
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/api/users/current-user")
    public ResponseEntity<UserRest> getCurrentUser()
    {
        return currentUserService.getCurrentUser()
            .map(UserRest::new)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/users/like/{searchValue}")
    public ResponseEntity<List<UserRest>> getList(@PathVariable String searchValue)
    {
        if(StringUtils.isBlank(searchValue))
        {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<UserRest> users = userRepository.getListByEmailLike('%' + searchValue + '%').stream()
            .map(UserRest::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    private static class UserRest
    {
        private final User user;

        private UserRest(User user)
        {
            this.user = user;
        }

        public Long getId()
        {
            return user.getId();
        }

        public String getName()
        {
            return user.getEmail();
        }

    }


}
