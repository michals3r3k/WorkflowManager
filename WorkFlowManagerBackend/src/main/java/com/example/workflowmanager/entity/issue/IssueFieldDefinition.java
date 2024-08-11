package com.example.workflowmanager.entity.issue;

import com.example.workflowmanager.entity.organization.Organization;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class IssueFieldDefinition
{
    @EmbeddedId
    private IssueFieldDefinitionId id;
    private String name;
    @Enumerated(EnumType.STRING)
    private IssueFieldType type;
    private boolean required;
    private boolean clientVisible;
    @ManyToOne
    @JoinColumn(name = "organizationId", insertable = false, updatable = false)
    private Organization organization;

    public IssueFieldDefinition(final IssueFieldDefinitionId id,
        final String name, final IssueFieldType type,
        final boolean required, final boolean clientVisible)
    {
        this.id = id;
        this.name = name;
        this.type = type;
        this.required = required;
        this.clientVisible = clientVisible;
    }

    protected IssueFieldDefinition()
    {
        // for Hibernate
    }

    public IssueFieldDefinitionId getId()
    {
        return id;
    }

    public void setId(final IssueFieldDefinitionId id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public IssueFieldType getType()
    {
        return type;
    }

    public void setType(final IssueFieldType type)
    {
        this.type = type;
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired(final boolean required)
    {
        this.required = required;
    }

    public boolean isClientVisible()
    {
        return clientVisible;
    }

    public void setClientVisible(final boolean clientVisible)
    {
        this.clientVisible = clientVisible;
    }

    public Organization getOrganization()
    {
        return organization;
    }

    public void setOrganization(final Organization organization)
    {
        this.organization = organization;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
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
        final IssueFieldDefinition that = (IssueFieldDefinition) o;
        return Objects.equals(id, that.id);
    }

}
