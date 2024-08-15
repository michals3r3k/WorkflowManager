package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.entity.issue.IssueFieldType;

public class IssueFieldEditRest extends IssueFieldDefinitionRest
{
    private Long organizationId;
    private String value;
    private Short row;

    IssueFieldEditRest(Long organizationId, String value, Short row,
        final String name, final Byte column, final IssueFieldType type,
        final boolean required, final boolean clientVisible)
    {
        super(name, column, type, required, clientVisible);
        this.organizationId = organizationId;
        this.value = value;
        this.row = row;
    }

    public IssueFieldEditRest()
    {
        // for Spring
    }

    public Long getOrganizationId()
    {
        return organizationId;
    }

    public void setOrganizationId(final Long organizationId)
    {
        this.organizationId = organizationId;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(final String value)
    {
        this.value = value;
    }

    public Short getRow()
    {
        return row;
    }

    public void setRow(final Short row)
    {
        this.row = row;
    }

}
