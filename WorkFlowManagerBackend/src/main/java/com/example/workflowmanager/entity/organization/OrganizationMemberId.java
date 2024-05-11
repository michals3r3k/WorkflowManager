package com.example.workflowmanager.entity.organization;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrganizationMemberId implements Serializable
{
    private Long organizationId;
    private Long userId;

    public OrganizationMemberId(Long organizationId, Long userId)
    {
        this.organizationId = organizationId;
        this.userId = userId;
    }

    protected OrganizationMemberId()
    {
        // for hibernate
    }

    public Long getOrganizationId()
    {
        return organizationId;
    }

    protected void setOrganizationId(Long organizationId)
    {
        this.organizationId = organizationId;
    }

    public Long getUserId()
    {
        return userId;
    }

    protected void setUserId(Long userId)
    {
        this.userId = userId;
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
        OrganizationMemberId that = (OrganizationMemberId) o;
        return Objects.equals(organizationId, that.organizationId)
            && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(organizationId, userId);
    }

}
