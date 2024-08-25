package com.example.workflowmanager.service.project;

import com.example.workflowmanager.db.organization.OrganizationInProjectRepository;
import com.example.workflowmanager.db.organization.project.ProjectRepository;
import com.example.workflowmanager.entity.organization.OrganizationInProject;
import com.example.workflowmanager.entity.organization.OrganizationInProjectId;
import com.example.workflowmanager.entity.organization.OrganizationInProjectRole;
import com.example.workflowmanager.entity.organization.project.Project;
import com.example.workflowmanager.service.organization.OrganizationInProjectService;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class ProjectCreateService
{
    private final ProjectRepository projectRepository;
    private final OrganizationInProjectRepository oipRepository;
    private final OrganizationInProjectService organizationInProjectService;

    public ProjectCreateService(final ProjectRepository projectRepository,
        final OrganizationInProjectRepository oipRepository,
        final OrganizationInProjectService organizationInProjectService)
    {
        this.projectRepository = projectRepository;
        this.oipRepository = oipRepository;
        this.organizationInProjectService = organizationInProjectService;
    }

    public ProjectCreateResult create(Long organizationId, ProjectCreateRest projectRest)
    {
        if(isExists(organizationId, projectRest)) {
            return new ProjectCreateResult(null, Collections.singleton(ProjectCreateError.EXISTS));
        }
        Project project = new Project();
        project.setName(projectRest.getName());
        project.setDescription(projectRest.getDescription());
        projectRepository.save(project);
        final Long projectId = project.getId();
        final OrganizationInProjectId oipId =
            new OrganizationInProjectId(organizationId, projectId);
        organizationInProjectService.create(oipId, OrganizationInProjectRole.OWNER, null);
        return new ProjectCreateResult(projectId, Collections.emptySet());
    }

    private boolean isExists(final Long organizationId,
        final ProjectCreateRest projectRest)
    {
        return oipRepository.getListByOrganizationIds(
                Collections.singleton(organizationId),
                Collections.singleton(OrganizationInProjectRole.OWNER))
            .stream()
            .map(OrganizationInProject::getProject)
            .map(Project::getName)
            .anyMatch(projectRest.getName()::equals);
    }

    public enum ProjectCreateError
    {
        EXISTS
    }

    public static class ProjectCreateResult extends ServiceResult<ProjectCreateError>
    {
        private final Long projectId;

        public ProjectCreateResult(Long projectId, final Collection<ProjectCreateError> errors)
        {
            super(errors);
            this.projectId = projectId;
        }

        public Long getProjectId()
        {
            return projectId;
        }

    }

}
