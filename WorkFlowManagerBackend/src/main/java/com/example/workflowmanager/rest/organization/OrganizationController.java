package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.service.organization.OrganizationService;
import com.example.workflowmanager.service.organization.OrganizationService.OrganizationServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class OrganizationController
{
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService)
    {
        this.organizationService = organizationService;
    }

    @PostMapping("/api/organization/create")
    public ResponseEntity<OrganizationServiceResult> create(
        @RequestBody OrganizationCreateRequest organizationCreateRequest)
    {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        return ResponseEntity.ok(organizationService.create(email,
            organizationCreateRequest.getName(), organizationCreateRequest.getDescription()));
    }

    public static class OrganizationCreateRequest
    {
        private String name;
        private String description;

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

    }

}
