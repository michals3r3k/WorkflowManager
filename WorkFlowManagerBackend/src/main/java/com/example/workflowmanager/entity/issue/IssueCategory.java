package com.example.workflowmanager.entity.issue;

import com.example.workflowmanager.entity.organization.Organization;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class IssueCategory
{
    @EmbeddedId
    private IssueCategoryId id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Organization organization;

    public IssueCategory(final IssueCategoryId id)
    {
        this.id = id;
    }

    public IssueCategory()
    {
        // for hibernate
    }

    public IssueCategoryId getId()
    {
        return id;
    }

    protected void setId(final IssueCategoryId id)
    {
        this.id = id;
    }

    public Organization getOrganization()
    {
        return organization;
    }

    protected void setOrganization(final Organization organization)
    {
        this.organization = organization;
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
        final IssueCategory that = (IssueCategory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
