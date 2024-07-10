package com.example.workflowmanager.entity.organization;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrganizationInProjectId implements Serializable
{
    private Long organizationId;
    private Long projectId;

    public OrganizationInProjectId(Long organizationId, Long projectId)
    {
        this.organizationId = organizationId;
        this.projectId = projectId;
    }

    protected OrganizationInProjectId()
    {
        // for hibernate
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

    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
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
        OrganizationInProjectId that = (OrganizationInProjectId) o;
        return Objects.equals(organizationId, that.organizationId)
            && Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(organizationId, projectId);
    }

}
