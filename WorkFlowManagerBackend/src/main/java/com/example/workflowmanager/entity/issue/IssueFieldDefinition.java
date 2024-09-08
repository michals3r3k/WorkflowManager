package com.example.workflowmanager.entity.issue;

import com.example.workflowmanager.entity.organization.Organization;
import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Objects;

@Entity
public class IssueFieldDefinition
{
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "organization_id")
    private Long organizationId;
    private Short row;
    private Byte col;
    @NotNull
    private String name;
    @Enumerated(EnumType.STRING)
    @NotNull
    private IssueFieldType type;
    private boolean required;
    private boolean clientVisible;
    @ManyToOne
    @JoinColumn(name = "organization_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Organization organization;

    public IssueFieldDefinition(final Long organizationId, final Short row,
        final Byte col, final String name, final IssueFieldType type,
        final boolean required, final boolean clientVisible)
    {
        this.organizationId = organizationId;
        this.row = row;
        this.col = col;
        this.name = name;
        this.type = type;
        this.required = required;
        this.clientVisible = clientVisible;
    }

    protected IssueFieldDefinition()
    {
        // for Hibernate
    }

    public Long getId()
    {
        return id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    public Long getOrganizationId()
    {
        return organizationId;
    }

    protected void setOrganizationId(final Long organizationId)
    {
        this.organizationId = organizationId;
    }

    public Short getRow()
    {
        return row;
    }

    protected void setRow(final Short row)
    {
        this.row = row;
    }

    public Byte getCol()
    {
        return col;
    }

    protected void setCol(final Byte col)
    {
        this.col = col;
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
