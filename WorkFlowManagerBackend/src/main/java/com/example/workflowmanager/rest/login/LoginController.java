package com.example.workflowmanager.rest.login;

import com.example.workflowmanager.service.auth.jwt.JwtService;
import com.example.workflowmanager.service.login.LoginService;
import com.example.workflowmanager.service.login.LoginService.LoginServiceResult;
import com.example.workflowmanager.service.login.RegisterService;
import com.example.workflowmanager.service.login.RegisterService.RegisterServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class LoginController
{
    private final LoginService loginService;
    private final RegisterService registerService;
    private final JwtService jwtService;

    public LoginController(LoginService loginService,
        RegisterService registerService, JwtService jwtService)
    {
        this.loginService = loginService;
        this.registerService = registerService;
        this.jwtService = jwtService;
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

    @PostMapping("api/checkToken")
    public ResponseEntity<Boolean> checkToken(
        @RequestBody TokenCheckRequest tokenCheckRequest)
    {
        return ResponseEntity.ok(jwtService.isTokenValid(
            tokenCheckRequest.getToken(), tokenCheckRequest.getEmail()));
    }

    public static class TokenCheckRequest
    {
        private String email;
        private String token;

        public TokenCheckRequest(final String email, final String token)
        {
            this.email = email;
            this.token = token;
        }

        public String getEmail()
        {
            return email;
        }

        public void setEmail(String email)
        {
            this.email = email;
        }

        public String getToken()
        {
            return token;
        }

        public void setToken(String token)
        {
            this.token = token;
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
