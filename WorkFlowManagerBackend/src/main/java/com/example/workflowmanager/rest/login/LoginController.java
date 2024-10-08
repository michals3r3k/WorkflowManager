package com.example.workflowmanager.rest.login;

import com.example.workflowmanager.entity.organization.role.Permission;
import com.example.workflowmanager.service.auth.UserPermissionService;
import com.example.workflowmanager.service.auth.jwt.JwtService;
import com.example.workflowmanager.service.login.LoginService;
import com.example.workflowmanager.service.login.LoginService.LoginServiceResult;
import com.example.workflowmanager.service.login.RegisterService;
import com.example.workflowmanager.service.login.RegisterService.RegisterServiceResult;
import com.google.common.collect.Maps;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class LoginController
{
    private final LoginService loginService;
    private final RegisterService registerService;
    private final JwtService jwtService;
    private final UserPermissionService permissionService;

    public LoginController(LoginService loginService,
        RegisterService registerService, JwtService jwtService,
        UserPermissionService permissionService)
    {
        this.loginService = loginService;
        this.registerService = registerService;
        this.jwtService = jwtService;
        this.permissionService = permissionService;
    }

    @PostMapping("api/login")
    @Transactional
    public ResponseEntity<LoginServiceResult> login(
        @RequestBody LoginForm loginForm)
    {
        LoginServiceResult loginResult = loginService.login(
            loginForm.getEmail(), loginForm.getPassword());
        return ResponseEntity.ok(loginResult);
    }

    @PostMapping("api/register")
    @Transactional
    public ResponseEntity<RegisterServiceResult> register(
        @RequestBody RegisterForm registerForm)
    {
        RegisterServiceResult registerResult = registerService.register(
            registerForm.getEmail(),
            registerForm.getPassword(),
            registerForm.getFirstName(),
            registerForm.getSecondName());
        return ResponseEntity.ok(registerResult);
    }

    @GetMapping("api/permissions")
    @Transactional
    public ResponseEntity<Map<Long, List<String>>> getPermissions()
    {
        final Map<Long, Collection<Permission>> permissionMap = permissionService.getCurrentUserPermissions();
        return ResponseEntity.ok(Maps.transformValues(permissionMap, permissions -> permissions.stream()
            .map(Permission::name)
            .collect(Collectors.toList())));
    }

    public static class PermissionsRequest
    {
        private Long organizationId;

        public PermissionsRequest()
        {
            // for spring
        }

        public PermissionsRequest(Long organizationId)
        {
            this.organizationId = organizationId;
        }

        public Long getOrganizationId()
        {
            return organizationId;
        }

        public void setOrganizationId(final Long organizationId)
        {
            this.organizationId = organizationId;
        }

    }

    public static class LoginForm
    {
        private String email;
        private String password;

        public LoginForm(final String email, final String password)
        {
            this.email = email;
            this.password = password;
        }

        public String getEmail()
        {
            return email;
        }

        public void setEmail(String email)
        {
            this.email = email;
        }

        public String getPassword()
        {
            return password;
        }

        public void setPassword(String password)
        {
            this.password = password;
        }

    }

    public static class RegisterForm
    {
        private String email;
        private String password;
        private String firstName;
        private String secondName;

        public RegisterForm(final String email, final String password,
            final String firstName, final String secondName)
        {
            this.email = email;
            this.password = password;
            this.firstName = firstName;
            this.secondName = secondName;
        }

        public String getEmail()
        {
            return email;
        }

        public void setEmail(String email)
        {
            this.email = email;
        }

        public String getPassword()
        {
            return password;
        }

        public void setPassword(String password)
        {
            this.password = password;
        }

        public String getFirstName()
        {
            return firstName;
        }

        public void setFirstName(final String firstName)
        {
            this.firstName = firstName;
        }

        public String getSecondName()
        {
            return secondName;
        }

        public void setSecondName(final String secondName)
        {
            this.secondName = secondName;
        }

    }

}
