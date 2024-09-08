package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.entity.issue.IssueFieldType;

public class IssueFieldDefinitionRest
{
    private Long definitionId;
    private String name;
    private Byte column;
    private IssueFieldType type;
    private boolean required;
    private boolean clientVisible;

    protected IssueFieldDefinitionRest(final Long definitionId, final String name,
        final Byte column, final IssueFieldType type, final boolean required,
        final boolean clientVisible)
    {
        this.definitionId = definitionId;
        this.name = name;
        this.column = column;
        this.type = type;
        this.required = required;
        this.clientVisible = clientVisible;
    }

    public IssueFieldDefinitionRest()
    {
        // for Spring
    }

    public Long getDefinitionId()
    {
        return definitionId;
    }

    public void setDefinitionId(final Long definitionId)
    {
        this.definitionId = definitionId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Byte getColumn()
    {
        return column;
    }

    public void setColumn(final Byte column)
    {
        this.column = column;
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

}
