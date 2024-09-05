package com.example.workflowmanager.rest.organization.project;

import com.example.workflowmanager.db.organization.OrganizationInProjectRepository;
import com.example.workflowmanager.db.organization.project.ProjectRepository;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.project.Project;
import com.example.workflowmanager.service.project.ProjectCreateRest;
import com.example.workflowmanager.service.project.ProjectCreateService;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin
@RestController
public class ProjectController
{
    private final OrganizationInProjectRepository organizationInProjectRepository;
    private final ProjectRepository projectRepository;
    private final ProjectCreateService projectService;

    public ProjectController(final OrganizationInProjectRepository organizationInProjectRepository,
        final ProjectRepository projectRepository,
        final ProjectCreateService projectService)
    {
        this.organizationInProjectRepository = organizationInProjectRepository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
    }

    @PostMapping("/api/organization/{organizationId}/project/create")
    @Transactional
    @PreAuthorize("hasAuthority('PROJECT_C')")
    public ResponseEntity<ServiceResult<?>> create(
        @PathVariable Long organizationId,
        @RequestBody ProjectCreateRest projectCreateRequest)
    {
        return ResponseEntity.ok(projectService.create(organizationId, projectCreateRequest));
    }

    @GetMapping("/api/organization/{organizationId}/projects")
    @Transactional
    @PreAuthorize("hasAuthority('PROJECT_R')")
    public ResponseEntity<List<ProjectRest>> getList(@PathVariable Long organizationId)
    {
        return ResponseEntity.ok(getProjects(organizationId, EnumSet.allOf(OrganizationInProjectRole.class)));
    }

    @GetMapping("/api/organization/{organizationId}/projects-owned")
    @Transactional
    @PreAuthorize("hasAuthority('PROJECT_R')")
    public ResponseEntity<List<ProjectRest>> getOwnedList(@PathVariable Long organizationId)
    {
        return ResponseEntity.ok(getProjects(organizationId, Collections.singleton(OrganizationInProjectRole.OWNER)));
    }

//    @GetMapping("/api/organization/{organizationId}/project/{projectId}")
//    @Transactional
//    @PreAuthorize("hasAuthority('PROJECT_R')")
//    public ResponseEntity<ProjectRest> getProject(@PathVariable Long organizationId, @PathVariable Long projectId)
//    {
//        return ResponseEntity.ok(getProject(organizationId, projectId, Collections.singleton(OrganizationInProjectRole.OWNER)));
//    }

    public List<ProjectRest> getProjects(Long organizationId, Collection<OrganizationInProjectRole> roles)
    {
        return getProjectStream(organizationId, roles)
            .sorted(Comparator.comparing(ProjectRest::getName, Comparator.naturalOrder())
                .thenComparing(ProjectRest::getProjectId))
            .collect(Collectors.toList());
    }

//    public ProjectRest getProject(Long organizationId, Long projectId, Collection<OrganizationInProjectRole> roles)
//    {
//        return getProjectStream(organizationId, roles)
//                .filter(p -> p.getProjectId().equals(projectId))
//                .findFirst()
//                .orElseThrow();
//    }

    private Stream<ProjectRest> getProjectStream(final Long organizationId,
        final Collection<OrganizationInProjectRole> roles)
    {
        Stream<ProjectRest> stream = Stream.empty();
        if(roles.contains(OrganizationInProjectRole.OWNER))
        {
            stream = Stream.concat(stream, getProjectsOfOwner(organizationId));
        }
        if(roles.contains(OrganizationInProjectRole.REPORTER))
        {
            stream = Stream.concat(stream, getProjectsOfReporter(organizationId));
        }
        return stream;
    }

    private Stream<ProjectRest> getProjectsOfOwner(Long organizationId)
    {
        return projectRepository.getListByOrganizationIds(Collections.singleton(organizationId)).stream()
            .map(project -> new ProjectRest(project.getOrganization(), project, OrganizationInProjectRole.OWNER));
    }

    private Stream<ProjectRest> getProjectsOfReporter(Long organizationId)
    {
        return organizationInProjectRepository.getListByOrganizationIds(
            Collections.singleton(organizationId)).stream()
            .map(oip -> new ProjectRest(oip.getOrganization(), oip.getProject(), OrganizationInProjectRole.REPORTER));
    }

    @GetMapping("/api/organization/{organizationId}/project/{projectId}")
    @Transactional
    @PreAuthorize("hasAuthority('PROJECT_R')")
    public ResponseEntity<ProjectRest> getDetails(@PathVariable Long organizationId, @PathVariable Long projectId)
    {
        if(organizationId == null || projectId == null)
        {
            return ResponseEntity.ok(null);
        }
        final Project project = projectRepository.getReferenceById(projectId);
        return ResponseEntity.ok(new ProjectRest(project.getOrganization(),
            project, OrganizationInProjectRole.OWNER));
    }

    public static class ProjectRest
    {
        private final Organization organization;
        private final Project project;
        private final OrganizationInProjectRole role;

        private ProjectRest(final Organization organization, final Project project,
            final OrganizationInProjectRole role)
        {
            this.organization = organization;
            this.project = project;
            this.role = role;
        }

        public Long getProjectId()
        {
            return project.getId();
        }
        public Long getOrganizationId()
        {
            return organization.getId();
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
            return role;
        }

    }

}
