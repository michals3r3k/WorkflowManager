package com.example.workflowmanager.entity.organization.role;

import com.example.workflowmanager.entity.organization.OrganizationMember;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class OrganizationMemberRole
{
    @EmbeddedId
    private OrganizationMemberRoleId id;
    @ManyToOne()
    @JoinColumns({
        @JoinColumn(name = "organizationId", referencedColumnName = "organizationId", insertable = false, updatable = false),
        @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    })
    private OrganizationMember member;
    @ManyToOne()
    @JoinColumns({
        @JoinColumn(name = "organizationId", referencedColumnName = "organizationId", insertable = false, updatable = false),
        @JoinColumn(name = "role", referencedColumnName = "role", insertable = false, updatable = false)
    })
    private OrganizationRole role;

    public OrganizationMemberRole(OrganizationMemberRoleId id)
    {
        this.id = id;
    }

    protected OrganizationMemberRole()
    {
        // for hibernate
    }

    public OrganizationMemberRoleId getId()
    {
        return id;
    }

    protected void setId(
        OrganizationMemberRoleId id)
    {
        this.id = id;
    }

    public OrganizationMember getMember()
    {
        return member;
    }

    protected void setMember(
        OrganizationMember member)
    {
        this.member = member;
    }

    public OrganizationRole getRole()
    {
        return role;
    }

    protected void setRole(
        OrganizationRole role)
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
        OrganizationMemberRole that = (OrganizationMemberRole) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
