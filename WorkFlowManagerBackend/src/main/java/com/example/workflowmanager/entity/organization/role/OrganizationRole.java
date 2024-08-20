package com.example.workflowmanager.entity.organization.role;

import com.example.workflowmanager.entity.organization.Organization;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Entity
public class OrganizationRole
{
    @EmbeddedId
    private OrganizationRoleId id;
    private boolean addToNewMembers;
    @ManyToOne()
    @JoinColumn(name = "organizationId",
        referencedColumnName = "id",
        insertable = false,
        updatable = false)
    private Organization organization;

    public OrganizationRole(OrganizationRoleId id, boolean addToNewMembers)
    {
        this.id = id;
        this.addToNewMembers = addToNewMembers;
    }

    protected OrganizationRole()
    {
        // for hibernate
    }

    public OrganizationRoleId getId()
    {
        return id;
    }

    protected void setId(OrganizationRoleId id)
    {
        this.id = id;
    }

    public Organization getOrganization()
    {
        return organization;
    }

    protected void setOrganization(
        Organization organization)
    {
        this.organization = organization;
    }

    public boolean isAddToNewMembers()
    {
        return addToNewMembers;
    }

    public void setAddToNewMembers(final boolean addToNewMembers)
    {
        this.addToNewMembers = addToNewMembers;
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
        OrganizationRole that = (OrganizationRole) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
