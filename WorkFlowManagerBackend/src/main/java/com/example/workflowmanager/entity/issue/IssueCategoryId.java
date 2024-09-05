package com.example.workflowmanager.entity.issue;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IssueCategoryId implements Serializable
{
    @Column(name = "organization_id")
    private Long organizationId;
    private String category;

    public IssueCategoryId(final Long organizationId, final String category)
    {
        this.organizationId = organizationId;
        this.category = category;
    }

    public IssueCategoryId()
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

    public String getCategory()
    {
        return category;
    }

    protected void setCategory(final String category)
    {
        this.category = category;
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
        final IssueCategoryId that = (IssueCategoryId) o;
        return Objects.equals(organizationId, that.organizationId)
            && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(organizationId, category);
    }

}
