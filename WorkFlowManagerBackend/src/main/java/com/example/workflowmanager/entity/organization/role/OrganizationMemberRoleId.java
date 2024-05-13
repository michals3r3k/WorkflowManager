package com.example.workflowmanager.entity.organization.role;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrganizationMemberRoleId implements Serializable
{
    private Long userId;
    private OrganizationRoleId organizationRoleId;

    public OrganizationMemberRoleId(Long userId,
        OrganizationRoleId organizationRoleId)
    {
        this.userId = userId;
        this.organizationRoleId = organizationRoleId;
    }


    protected OrganizationMemberRoleId()
    {
        // for hibernate
    }

    public Long getUserId()
    {
        return userId;
    }

    protected void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public OrganizationRoleId getOrganizationRoleId()
    {
        return organizationRoleId;
    }

    protected void setOrganizationRoleId(
        OrganizationRoleId organizationRoleId)
    {
        this.organizationRoleId = organizationRoleId;
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
        OrganizationMemberRoleId that = (OrganizationMemberRoleId) o;
        return Objects.equals(userId,that.userId)
            && Objects.equals(organizationRoleId, that.organizationRoleId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(userId, organizationRoleId);
    }

}
