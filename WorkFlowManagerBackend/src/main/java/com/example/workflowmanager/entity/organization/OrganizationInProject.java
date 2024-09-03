package com.example.workflowmanager.entity.organization;

import com.example.workflowmanager.entity.organization.project.Project;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class OrganizationInProject
{
    @EmbeddedId
    private OrganizationInProjectId id;
    @Enumerated(EnumType.STRING)
    @Column(name = "invitation_status")
    private OrganizationInvitationStatus invitationStatus;
    @ManyToOne()
    @JoinColumn(name = "organizationId",
        referencedColumnName = "id",
        insertable = false,
        updatable = false)
    private Organization organization;
    @ManyToOne()
    @JoinColumn(name = "projectId",
        referencedColumnName = "id",
        insertable = false,
        updatable = false)
    private Project project;

    public OrganizationInProject(OrganizationInProjectId id)
    {
        this.id = id;
    }

    protected OrganizationInProject()
    {
        // for hibernate
    }

    protected void setId(OrganizationInProjectId id)
    {
        this.id = id;
    }

    public OrganizationInProjectId getId()
    {
        return id;
    }

    public OrganizationInvitationStatus getInvitationStatus()
    {
        return invitationStatus;
    }

    public void setInvitationStatus(
        OrganizationInvitationStatus invitationStatus)
    {
        this.invitationStatus = invitationStatus;
    }

    public Organization getOrganization()
    {
        return organization;
    }

    protected void setOrganization(Organization organization)
    {
        this.organization = organization;
    }

    public Project getProject()
    {
        return project;
    }

    protected void setProject(Project user)
    {
        this.project = user;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        OrganizationInProject that = (OrganizationInProject) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
