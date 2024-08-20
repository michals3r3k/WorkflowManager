package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.OrganizationMember;
import com.example.workflowmanager.entity.organization.OrganizationMemberInvitationStatus;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.auth.CurrentUserService;
import com.example.workflowmanager.service.organization.OrganizationService;
import com.example.workflowmanager.service.organization.OrganizationService.OrganizationServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin
@RestController
public class OrganizationController
{
    private final OrganizationRepository organizationRepository;
    private final OrganizationService organizationService;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final CurrentUserService currentUserService;

    public OrganizationController(OrganizationRepository organizationRepository,
        OrganizationService organizationService,
        OrganizationMemberRepository organizationMemberRepository,
        CurrentUserService currentUserService)
    {
        this.organizationRepository = organizationRepository;
        this.organizationService = organizationService;
        this.organizationMemberRepository = organizationMemberRepository;
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
        final Optional<Long> userId = currentUserService.getCurrentUser()
            .map(User::getId);
        final Optional<Set<Long>> userIds = userId.map(Collections::singleton);
        final Stream<Organization> organizationsMember = userIds
            .map(userIdColl -> organizationMemberRepository.getListByUserIdsWithOrganization(
                userIdColl, Collections.singleton(OrganizationMemberInvitationStatus.ACCEPTED)))
            .orElse(Collections.emptyList())
            .stream()
            .map(OrganizationMember::getOrganization);
        final Stream<Organization> organizationsOwner = userIds
            .map(organizationRepository::getListByUserIds).stream()
            .flatMap(Collection::stream);
        List<OrganizationRest> organizations = Stream
            .concat(organizationsOwner, organizationsMember)
            .distinct()
            .map(organization -> new OrganizationRest(userId.orElse(null), organization))
            .sorted(Comparator.comparing(OrganizationRest::getName,
                Comparator.naturalOrder()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/api/organization/{id}")
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
