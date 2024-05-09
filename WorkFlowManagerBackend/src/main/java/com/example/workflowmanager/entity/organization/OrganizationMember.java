package com.example.workflowmanager.entity.organization;

import com.example.workflowmanager.entity.user.User;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class OrganizationMember
{
    @EmbeddedId
    private OrganizationMemberId id;
    @ManyToOne()
    @JoinColumn(name = "organizationId",
        referencedColumnName = "id",
        insertable = false,
        updatable = false)
    private Organization organization;
    @ManyToOne()
    @JoinColumn(name = "userId",
        referencedColumnName = "id",
        insertable = false,
        updatable = false)
    private User user;

    public OrganizationMember(OrganizationMemberId id)
    {
        this.id = id;
    }

    protected OrganizationMember()
    {
        // for hibernate
    }

    protected void setId(OrganizationMemberId id)
    {
        this.id = id;
    }

    public OrganizationMemberId getId()
    {
        return id;
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

    public User getUser()
    {
        return user;
    }

    protected void setUser(User user)
    {
        this.user = user;
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
        OrganizationMember that = (OrganizationMember) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

}
