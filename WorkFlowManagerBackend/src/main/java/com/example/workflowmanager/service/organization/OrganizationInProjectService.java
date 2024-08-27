package com.example.workflowmanager.service.organization;

import com.example.workflowmanager.db.organization.OrganizationInProjectRepository;
import com.example.workflowmanager.entity.organization.OrganizationInProject;
import com.example.workflowmanager.entity.organization.OrganizationInProjectId;
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

    public void create(OrganizationInProjectId id,
        OrganizationInvitationStatus invitationStatus)
    {
        OrganizationInProject organizationInProject = new OrganizationInProject(id);
        organizationInProject.setInvitationStatus(invitationStatus);
        organizationInProjectRepository.save(organizationInProject);
    }

}
