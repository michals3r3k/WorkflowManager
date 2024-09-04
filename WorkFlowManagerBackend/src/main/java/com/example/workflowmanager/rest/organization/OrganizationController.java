package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.auth.CurrentUserService;
import com.example.workflowmanager.service.organization.OrganizationService;
import com.example.workflowmanager.service.organization.OrganizationService.OrganizationServiceResult;
import com.example.workflowmanager.service.organization.UserOrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class OrganizationController
{
    private final OrganizationRepository organizationRepository;
    private final OrganizationService organizationService;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final CurrentUserService currentUserService;
    private final UserOrganizationService userOrganizationService;

    public OrganizationController(final OrganizationRepository organizationRepository,
        final OrganizationService organizationService,
        final OrganizationMemberRepository organizationMemberRepository,
        final CurrentUserService currentUserService,
        final UserOrganizationService userOrganizationService)
    {
        this.organizationRepository = organizationRepository;
        this.organizationService = organizationService;
        this.organizationMemberRepository = organizationMemberRepository;
        this.currentUserService = currentUserService;
        this.userOrganizationService = userOrganizationService;
    }

    @PostMapping("/api/organization/create")
    @Transactional
    public ResponseEntity<OrganizationServiceResult> create(
        @RequestBody OrganizationCreateRequest organizationCreateRequest)
    {
        String currentUserEmail = CurrentUserService.getCurrentUserEmail();
        return ResponseEntity.ok(organizationService.create(currentUserEmail,
            organizationCreateRequest.getName(), organizationCreateRequest.getDescription()));
    }

    @GetMapping("/api/organization/list")
    @Transactional
    public ResponseEntity<List<OrganizationRest>> getList()
    {
        final Optional<Long> userId = currentUserService.getCurrentUser()
            .map(User::getId);
        final List<OrganizationRest> organizations = userOrganizationService
            .getSet(userId.map(Collections::singleton).orElse(Collections.emptySet()))
            .stream()
            .map(organization -> new OrganizationRest(userId.orElse(null), organization))
            .sorted(Comparator.comparing(OrganizationRest::getName, Comparator.naturalOrder()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/api/organization/{id}")
    @Transactional
    @PreAuthorize("hasAuthority('ORGANIZATION_R')")
    public ResponseEntity<OrganizationRest> getDetails(@PathVariable Long id)
    {
        OrganizationRest organizationRest = Optional.ofNullable(id)
            .map(organizationRepository::getReferenceById)
            .map(organization -> new OrganizationRest(null, organization))
            .orElse(null);
        return ResponseEntity.ok(organizationRest);
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

    public static class OrganizationRest
    {
        private Long userIdOrNull;
        private Organization organization;

        private OrganizationRest(final Long userIdOrNull, Organization organization)
        {
            this.userIdOrNull = userIdOrNull;
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

        public boolean isOwner() {
            return organization.getUser().getId().equals(userIdOrNull);
        }

    }

}
