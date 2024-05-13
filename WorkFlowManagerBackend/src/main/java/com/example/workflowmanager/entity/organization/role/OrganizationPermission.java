package com.example.workflowmanager.entity.organization.role;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class OrganizationPermission
{
    @EmbeddedId
    private OrganizationPermissionId id;

    @ManyToOne()
    @JoinColumns({
        @JoinColumn(name = "role", referencedColumnName = "role", insertable = false, updatable = false),
        @JoinColumn(name = "organizationId", referencedColumnName = "organizationId", insertable = false, updatable = false)
    })
    private OrganizationRole role;

    public OrganizationPermission(OrganizationPermissionId id)
    {
        this.id = id;
    }

    protected OrganizationPermission()
    {
        // for hibernate
    }

    public OrganizationPermissionId getId()
    {
        return id;
    }

    protected void setId(OrganizationPermissionId id)
    {
        this.id = id;
    }

    public OrganizationRole getRole()
    {
        return role;
    }

    protected void setRole(OrganizationRole role)
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
        OrganizationPermission that = (OrganizationPermission) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
