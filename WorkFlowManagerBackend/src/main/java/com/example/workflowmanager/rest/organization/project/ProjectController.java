package com.example.workflowmanager.rest.organization.project;

import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.db.organization.project.ProjectRepository;
import com.example.workflowmanager.entity.organization.Organization;
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

    public ProjectController(ProjectRepository projectRepository,
        OrganizationRepository organizationRepository)
    {
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
    }

    @PostMapping("/api/project/create/{organizationId}")
    public ResponseEntity<ProjectServiceResult> create(
        @PathVariable Long organizationId,
        @RequestBody ProjectCreateRequest projectCreateRequest)
    {
        Organization organization = organizationRepository.getReferenceById(organizationId);
        Project project = new Project();
        project.setOrganization(organization);
        project.setName(projectCreateRequest.getName());
        project.setDescription(projectCreateRequest.getDescription());
        projectRepository.save(project);
        return ResponseEntity.ok(new ProjectServiceResult());
    }


    @GetMapping("api/project/{organizationId}")
    public ResponseEntity<List<ProjectRest>> getList(@PathVariable Long organizationId)
    {
        List<ProjectRest> projects = projectRepository.getListByOrganizationIds(
            Collections.singleton(organizationId)).stream()
            .map(ProjectRest::new)
            .sorted(Comparator.comparing(ProjectRest::getName, Comparator.naturalOrder())
                .thenComparing(ProjectRest::getId))
            .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }

    public static class ProjectCreateRequest
    {
        private String name;
        private String description;

        public ProjectCreateRequest()
        {
            //
        }

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
        private final Project project;

        private ProjectRest(Project project)
        {
            this.project = project;
        }

        public Long getId()
        {
            return project.getId();
        }

        public String getName()
        {
            return project.getName();
        }

        public String getDescription()
        {
            return project.getDescription();
        }

    }



}
