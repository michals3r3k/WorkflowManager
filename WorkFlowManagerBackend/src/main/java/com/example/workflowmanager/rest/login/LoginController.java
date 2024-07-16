package com.example.workflowmanager.rest.login;

import com.example.workflowmanager.service.auth.UserPermissionService;
import com.example.workflowmanager.service.auth.jwt.JwtService;
import com.example.workflowmanager.service.login.LoginService;
import com.example.workflowmanager.service.login.LoginService.LoginServiceResult;
import com.example.workflowmanager.service.login.RegisterService;
import com.example.workflowmanager.service.login.RegisterService.RegisterServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    public ResponseEntity<LoginServiceResult> login(
        @RequestBody LoginForm loginForm)
    {
        LoginServiceResult loginResult = loginService.login(
            loginForm.getEmail(), loginForm.getPassword());
        return ResponseEntity.ok(loginResult);
    }

    @PostMapping("api/register")
    public ResponseEntity<RegisterServiceResult> register(
        @RequestBody LoginForm loginForm)
    {
        RegisterServiceResult registerResult = registerService.register(
            loginForm.getEmail(), loginForm.getPassword());
        return ResponseEntity.ok(registerResult);
    }

    @PostMapping("api/permissions")
    public ResponseEntity<List<String>> getPermissions(@RequestBody PermissionsRequest request)
    {
        final List<String> permissions = permissionService
            .getCurrentUserPermissions(request.getOrganizationId()).stream()
            .map(Enum::name)
            .collect(Collectors.toList());
        return ResponseEntity.ok(permissions);
    }

    public static class PermissionsRequest
    {
        private Long organizationId;

        public PermissionsRequest()
        {

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

}
