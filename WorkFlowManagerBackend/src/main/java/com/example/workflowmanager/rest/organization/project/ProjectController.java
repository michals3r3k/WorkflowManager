package com.example.workflowmanager.rest.organization.project;

import com.example.workflowmanager.db.organization.OrganizationInProjectRepository;
import com.example.workflowmanager.entity.organization.OrganizationInProject;
import com.example.workflowmanager.entity.organization.OrganizationInProjectId;
import com.example.workflowmanager.entity.organization.OrganizationInProjectRole;
import com.example.workflowmanager.entity.organization.project.Project;
import com.example.workflowmanager.service.project.ProjectCreateRest;
import com.example.workflowmanager.service.project.ProjectCreateService;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class ProjectController
{
    private final OrganizationInProjectRepository organizationInProjectRepository;
    private final ProjectCreateService projectService;

    public ProjectController(OrganizationInProjectRepository organizationInProjectRepository,
        ProjectCreateService projectService)
    {
        this.organizationInProjectRepository = organizationInProjectRepository;
        this.projectService = projectService;
    }

    @PostMapping("/api/organization/{organizationId}/project/create")
    @PreAuthorize("hasAuthority('PROJECT_C')")
    public ResponseEntity<ServiceResult<?>> create(
        @PathVariable Long organizationId,
        @RequestBody ProjectCreateRest projectCreateRequest)
    {
        return ResponseEntity.ok(projectService.create(organizationId, projectCreateRequest));
    }

    @GetMapping("/api/organization/{organizationId}/projects")
    @PreAuthorize("hasAuthority('PROJECT_R')")
    public ResponseEntity<List<ProjectRest>> getList(@PathVariable Long organizationId)
    {
        return ResponseEntity.ok(getProjects(organizationId, EnumSet.allOf(OrganizationInProjectRole.class)));
    }

    @GetMapping("/api/organization/{organizationId}/projects-owned")
    @PreAuthorize("hasAuthority('PROJECT_R')")
    public ResponseEntity<List<ProjectRest>> getOwnedList(@PathVariable Long organizationId)
    {
        return ResponseEntity.ok(getProjects(organizationId, Collections.singleton(OrganizationInProjectRole.OWNER)));
    }

    public List<ProjectRest> getProjects(Long organizationId, Collection<OrganizationInProjectRole> roles)
    {
        return organizationInProjectRepository.getListByOrganizationIds(
                Collections.singleton(organizationId), roles).stream()
            .map(ProjectRest::new)
            .sorted(Comparator.comparing(ProjectRest::getName, Comparator.naturalOrder())
                .thenComparing(ProjectRest::getProjectId))
            .collect(Collectors.toList());
    }

    @GetMapping("/api/organization/{organizationId}/project/{projectId}")
    @PreAuthorize("hasAuthority('PROJECT_R')")
    public ResponseEntity<ProjectRest> getDetails(@PathVariable Long organizationId, @PathVariable Long projectId)
    {
        if(organizationId == null || projectId == null)
        {
            return ResponseEntity.ok(null);
        }
        ProjectRest projectRest = new ProjectRest(organizationInProjectRepository
            .getReferenceById(new OrganizationInProjectId(organizationId, projectId)));
        return ResponseEntity.ok(projectRest);
    }

    public static class ProjectRest
    {
        private final OrganizationInProject oip;
        private final Project project;

        private ProjectRest(OrganizationInProject oip)
        {
            this.oip = oip;
            this.project = oip.getProject();
        }

        public Long getProjectId()
        {
            return oip.getId().getProjectId();
        }
        public Long getOrganizationId()
        {
            return oip.getOrganization().getId();
        }

        public String getName()
        {
            return project.getName();
        }

        public String getDescription()
        {
            return project.getDescription();
        }

        public OrganizationInProjectRole getRole()
        {
            return oip.getRole();
        }

    }

}
