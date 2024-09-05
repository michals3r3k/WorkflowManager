package com.example.workflowmanager.entity.issue;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IssueStatusId implements Serializable
{
    @Column(name = "organization_id")
    private Long organizationId;
    private String status;

    public IssueStatusId(final Long organizationId, final String status)
    {
        this.organizationId = organizationId;
        this.status = status;
    }

    public IssueStatusId()
    {
        // for hibernate
    }

    public Long getOrganizationId()
    {
        return organizationId;
    }

    protected void setOrganizationId(final Long organizationId)
    {
        this.organizationId = organizationId;
    }

    public String getStatus()
    {
        return status;
    }

    protected void setStatus(final String category)
    {
        this.status = category;
    }

    @Override
    public boolean equals(final Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        final IssueStatusId that = (IssueStatusId) o;
        return Objects.equals(organizationId, that.organizationId)
            && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(organizationId, status);
    }

}
