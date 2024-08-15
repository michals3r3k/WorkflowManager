package com.example.workflowmanager.service.organization;

import com.example.workflowmanager.db.organization.OrganizationInProjectRepository;
import com.example.workflowmanager.entity.organization.OrganizationInProject;
import com.example.workflowmanager.entity.organization.OrganizationInProjectId;
import com.example.workflowmanager.entity.organization.OrganizationInProjectRole;
import com.example.workflowmanager.entity.organization.OrganizationInvitationStatus;
import org.springframework.stereotype.Service;

@Service
public class OrganizationInProjectService
{
    private final OrganizationInProjectRepository organizationInProjectRepository;

    public OrganizationInProjectService(
        final OrganizationInProjectRepository organizationInProjectRepository)
    {
        this.organizationInProjectRepository = organizationInProjectRepository;
    }

    public void create(OrganizationInProjectId id, OrganizationInProjectRole organizationRole,
        OrganizationInvitationStatus invitationStatus)
    {
        OrganizationInProject organizationInProject = new OrganizationInProject(id);
        organizationInProject.setRole(organizationRole);
        organizationInProject.setInvitationStatus(invitationStatus);
        organizationInProjectRepository.save(organizationInProject);
    }

}
