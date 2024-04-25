package com.example.workflowmanager;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController
{
    @GetMapping(path="/helloworld")
    public ResponseEntity<String> getHello()
    {
        return ResponseEntity.ok("Hello World!!");
    }

}
