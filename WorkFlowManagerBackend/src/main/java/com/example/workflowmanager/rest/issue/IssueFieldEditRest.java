package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.entity.issue.IssueFieldType;

public class IssueFieldEditRest extends IssueFieldDefinitionRest
{
    private Long organizationId;
    private Object value;
    private Short row;
    private String key;

    IssueFieldEditRest(Long organizationId, Object value, Short row, String key,
        final String name, final Byte column, final IssueFieldType type,
        final boolean required, final boolean clientVisible)
    {
        super(name, column, type, required, clientVisible);
        this.organizationId = organizationId;
        this.value = value;
        this.row = row;
        this.key = key;
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

    public Object getValue()
    {
        return value;
    }

    public void setValue(final Object value)
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

    public String getKey()
    {
        return key;
    }

    public void setKey(final String key)
    {
        this.key = key;
    }

}
