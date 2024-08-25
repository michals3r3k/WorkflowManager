package com.example.workflowmanager.entity.issue;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IssueFieldDefinitionId implements Serializable
{
    private Long organizationId;
    private Short row;
    private Byte col;

    public IssueFieldDefinitionId(Long organizationId, Short row, Byte col)
    {
        this.organizationId = organizationId;
        this.row = row;
        this.col = col;
    }

    protected IssueFieldDefinitionId()
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

    @Override
    public int hashCode()
    {
        return Objects.hash(organizationId, row, col);
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
        final IssueFieldDefinitionId that = (IssueFieldDefinitionId) o;
        return Objects.equals(organizationId, that.organizationId)
            && Objects.equals(row, that.row)
            && Objects.equals(col, that.col);
    }

}
