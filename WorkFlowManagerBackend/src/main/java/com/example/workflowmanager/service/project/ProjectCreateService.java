package com.example.workflowmanager.service.project;

import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.db.organization.project.ProjectRepository;
import com.example.workflowmanager.entity.organization.project.Project;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class ProjectCreateService
{
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;

    public ProjectCreateService(final ProjectRepository projectRepository,
        final OrganizationRepository organizationRepository)
    {
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
    }

    public ProjectCreateResult create(Long organizationId, ProjectCreateRest projectRest)
    {
        if(isExists(organizationId, projectRest)) {
            return new ProjectCreateResult(null, Collections.singleton(ProjectCreateError.EXISTS));
        }
        Project project = new Project();
        project.setName(projectRest.getName());
        project.setDescription(projectRest.getDescription());
        project.setOrganization(organizationRepository.getReferenceById(organizationId));
        projectRepository.save(project);
        return new ProjectCreateResult(project.getId(), Collections.emptySet());
    }

    private boolean isExists(final Long organizationId,
        final ProjectCreateRest projectRest)
    {
        return projectRepository.getListByOrganizationIds(
            Collections.singleton(organizationId)).stream()
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
