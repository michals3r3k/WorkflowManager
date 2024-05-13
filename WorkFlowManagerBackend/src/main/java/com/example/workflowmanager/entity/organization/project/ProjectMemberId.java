package com.example.workflowmanager.entity.organization.project;

import com.example.workflowmanager.entity.organization.OrganizationMemberId;
import com.google.common.base.Preconditions;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProjectMemberId implements Serializable
{
    private OrganizationMemberId memberId;
    private Long projectId;

    public ProjectMemberId(OrganizationMemberId memberId, Long projectId)
    {
        Preconditions.checkNotNull(memberId);
        Preconditions.checkNotNull(projectId);
        this.memberId = memberId;
        this.projectId = projectId;
    }

    protected ProjectMemberId()
    {
        // for hibernate
    }

    public OrganizationMemberId getMemberId()
    {
        return memberId;
    }

    protected void setMemberId(
        OrganizationMemberId memberId)
    {
        this.memberId = memberId;
    }

    public Long getProjectId()
    {
        return projectId;
    }

    protected void setProjectId(Long projectId)
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
        ProjectMemberId that = (ProjectMemberId) o;
        return memberId.equals(that.memberId) && projectId.equals(that.projectId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(memberId, projectId);
    }

}
