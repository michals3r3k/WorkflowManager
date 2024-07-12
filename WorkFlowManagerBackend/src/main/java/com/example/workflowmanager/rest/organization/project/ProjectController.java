package com.example.workflowmanager.rest.organization.project;

import com.example.workflowmanager.db.organization.OrganizationInProjectRepository;
import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.db.organization.project.ProjectRepository;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.OrganizationInProject;
import com.example.workflowmanager.entity.organization.OrganizationInProjectId;
import com.example.workflowmanager.entity.organization.OrganizationInProjectRole;
import com.example.workflowmanager.entity.organization.project.Project;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class ProjectController
{
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationInProjectRepository organizationInProjectRepository;

    public ProjectController(ProjectRepository projectRepository,
        OrganizationRepository organizationRepository,
        OrganizationInProjectRepository organizationInProjectRepository)
    {
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.organizationInProjectRepository = organizationInProjectRepository;
    }

    @PostMapping("/api/project/create/{organizationId}")
    public ResponseEntity<ProjectServiceResult> create(
        @PathVariable Long organizationId,
        @RequestBody ProjectCreateRequest projectCreateRequest)
    {
        Organization organization = organizationRepository.getReferenceById(organizationId);
        Project project = new Project();
        project.setName(projectCreateRequest.getName());
        project.setDescription(projectCreateRequest.getDescription());
        projectRepository.save(project);
        OrganizationInProject organizationInProject = new OrganizationInProject(
            new OrganizationInProjectId(organizationId, project.getId()));
        organizationInProject.setRole(OrganizationInProjectRole.OWNER);
        organizationInProjectRepository.save(organizationInProject);
        return ResponseEntity.ok(new ProjectServiceResult());
    }

    @GetMapping("/api/project/{organizationId}")
    public ResponseEntity<List<ProjectRest>> getList(@PathVariable Long organizationId)
    {
        List<ProjectRest> projects = organizationInProjectRepository.getListByOrganizationIds(
            Collections.singleton(organizationId)).stream()
            .map(ProjectRest::new)
            .sorted(Comparator.comparing(ProjectRest::getName, Comparator.naturalOrder())
                .thenComparing(ProjectRest::getProjectId))
            .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/api/project-details/{organizationId}/{projectId}")
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

    public static class ProjectCreateRequest
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

    public static class ProjectServiceResult
    {
        private ProjectServiceResult()
        {
            // TODO:
        }

        public boolean isSuccess()
        {
            return true;
        }

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
