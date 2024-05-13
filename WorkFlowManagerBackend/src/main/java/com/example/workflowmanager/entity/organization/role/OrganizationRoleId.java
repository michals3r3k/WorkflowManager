package com.example.workflowmanager.entity.organization.role;

import com.google.common.base.Preconditions;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrganizationRoleId implements Serializable
{
    private Long organizationId;
    private String role;

    public OrganizationRoleId(Long organizationId, String role)
    {
        Preconditions.checkNotNull(organizationId);
        Preconditions.checkNotNull(role);
        this.organizationId = organizationId;
        this.role = role;
    }

    protected OrganizationRoleId()
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

    public String getRole()
    {
        return role;
    }

    protected void setRole(String role)
    {
        this.role = role;
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
        OrganizationRoleId that = (OrganizationRoleId) o;
        return organizationId.equals(that.organizationId) && role.equals(that.role);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(organizationId, role);
    }

}
