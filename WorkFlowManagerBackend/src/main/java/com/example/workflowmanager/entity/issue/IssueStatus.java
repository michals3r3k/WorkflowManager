package com.example.workflowmanager.entity.issue;

import com.example.workflowmanager.entity.organization.Organization;
import com.google.common.collect.ImmutableList;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.List;
import java.util.Objects;

@Entity
public class IssueStatus
{
    public static final String NEW = "New";
    public static final List<String> CONST_STATUSES = ImmutableList.of(NEW);

    @EmbeddedId
    private IssueStatusId id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Organization organization;

    public IssueStatus(final IssueStatusId id)
    {
        this.id = id;
    }

    public IssueStatus()
    {
        // for hibernate
    }

    public IssueStatusId getId()
    {
        return id;
    }

    protected void setId(final IssueStatusId id)
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
        final IssueStatus that = (IssueStatus) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
