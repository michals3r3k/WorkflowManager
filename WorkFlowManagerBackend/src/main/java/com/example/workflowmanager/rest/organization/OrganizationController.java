package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.auth.CurrentUserService;
import com.example.workflowmanager.service.organization.OrganizationService;
import com.example.workflowmanager.service.organization.OrganizationService.OrganizationServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin
@RestController
public class OrganizationController
{
    private final OrganizationRepository organizationRepository;
    private final OrganizationService organizationService;
    private final CurrentUserService currentUserService;

    public OrganizationController(OrganizationRepository organizationRepository,
        OrganizationService organizationService,
        CurrentUserService currentUserService)
    {
        this.organizationRepository = organizationRepository;
        this.organizationService = organizationService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/api/organization/create")
    public ResponseEntity<OrganizationServiceResult> create(
        @RequestBody OrganizationCreateRequest organizationCreateRequest)
    {
        String currentUserEmail = CurrentUserService.getCurrentUserEmail();
        return ResponseEntity.ok(organizationService.create(currentUserEmail,
            organizationCreateRequest.getName(), organizationCreateRequest.getDescription()));
    }

    @GetMapping("/api/organization/list")
    public ResponseEntity<List<OrganizationRest>> getList()
    {
        List<OrganizationRest> organizations = currentUserService.getCurrentUser()
            .map(User::getId)
            .map(Collections::singleton)
            .map(organizationRepository::getListByUserIds)
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(OrganizationRest::new)
            .sorted(Comparator.comparing(OrganizationRest::getName,
                Comparator.naturalOrder()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(organizations);
    }

    public static class OrganizationRest
    {
        private Organization organization;

        private OrganizationRest(Organization organization)
        {
            this.organization = organization;
        }

        public Long getId()
        {
            return organization.getId();
        }

        public String getName()
        {
            return organization.getName();
        }

        public String getDescription()
        {
            return organization.getDescription();
        }

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
