package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.db.organization.OrganizationInProjectRepository;
import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.entity.organization.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class OrganizationInviteController
{
    private final OrganizationRepository organizationRepository;
    private final OrganizationInProjectRepository organizationInProjectRepository;

    public OrganizationInviteController(
        OrganizationRepository organizationRepository,
        OrganizationInProjectRepository organizationInProjectRepository)
    {
        this.organizationRepository = organizationRepository;
        this.organizationInProjectRepository = organizationInProjectRepository;
    }

    @GetMapping("/api/organizations/not-in-project/{projectId}/like/{searchValue}")
    @Transactional
    public ResponseEntity<List<OrganizationRest>> getOrganizationNotInProject(@PathVariable Long projectId,
        @PathVariable String searchValue)
    {
        Supplier<Set<Long>> organizationIdsSupplier = () -> organizationInProjectRepository
            .getIdListByProjectIds(Collections.singleton(projectId)).stream()
            .map(OrganizationInProjectId::getOrganizationId)
            .collect(Collectors.toSet());
        return searchOrganizations(searchValue, organizationIdsSupplier);
    }

    @GetMapping("/api/organizations/different-organization/{organizationId}/like/{searchValue}")
    @Transactional
    public ResponseEntity<List<OrganizationRest>> getOrganizationDifferent(@PathVariable Long organizationId,
        @PathVariable String searchValue)
    {
        return searchOrganizations(searchValue, () -> Collections.singleton(organizationId));
    }

    private ResponseEntity<List<OrganizationRest>> searchOrganizations(
        final String searchValue, final Supplier<Set<Long>> excludedOrganizationIdsSupplier)
    {
        if(StringUtils.isBlank(searchValue))
        {
            return ResponseEntity.ok(Collections.emptyList());
        }
        final Set<Long> organizationIds = excludedOrganizationIdsSupplier.get();
        List<OrganizationRest> organizations = organizationRepository.getListByNameLike('%' + searchValue + '%').stream()
            .filter(organization -> !organizationIds.contains(organization.getId()))
            .map(OrganizationRest::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(organizations);
    }

    @PostMapping("/api/organization-in-project/invite")
    @Transactional
    public ResponseEntity<OrganizationInviteServiceResult> inviteOrganization(@RequestBody OrganizationInviteRequest request)
    {
        // TODO: add service with validation
        OrganizationInProject organizationInProject = new OrganizationInProject(
            new OrganizationInProjectId(request.getOrganizationId(), request.getProjectId()));
        organizationInProject.setRole(OrganizationInProjectRole.REPORTER);
        organizationInProject.setInvitationStatus(OrganizationInvitationStatus.INVITED);
        organizationInProjectRepository.save(organizationInProject);
        return ResponseEntity.ok(new OrganizationInviteServiceResult(true));
    }

    public static class OrganizationInviteServiceResult
    {
        private final boolean success;

        private OrganizationInviteServiceResult(boolean success)
        {
            this.success = success;
        }

        public boolean isSuccess()
        {
            return success;
        }

    }

    public static class OrganizationInviteRequest
    {
        private Long organizationId;
        private Long projectId;

        public OrganizationInviteRequest(Long organizationId, Long projectId)
        {
            this.organizationId = organizationId;
            this.projectId = projectId;
        }

        public Long getOrganizationId()
        {
            return organizationId;
        }

        public void setOrganizationId(Long organizationId)
        {
            this.organizationId = organizationId;
        }

        public Long getProjectId()
        {
            return projectId;
        }

        public void setProjectId(Long userId)
        {
            this.projectId = userId;
        }

    }

    public static class OrganizationRest
    {
        private final Organization organization;

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

    }

}
