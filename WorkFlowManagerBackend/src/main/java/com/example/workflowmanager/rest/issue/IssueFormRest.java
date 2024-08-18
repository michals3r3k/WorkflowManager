package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.entity.issue.IssueFieldType;

import java.util.List;

public class IssueFormRest
{
    private String title;
    private List<IssueFieldEditRest> fields;

    IssueFormRest(String title, List<IssueFieldEditRest> fields)
    {
        this.title = title;
        this.fields = fields;
    }

    public IssueFormRest()
    {
        // for spring
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(final String title)
    {
        this.title = title;
    }

    public List<IssueFieldEditRest> getFields()
    {
        return fields;
    }

    public void setFields(final List<IssueFieldEditRest> fields)
    {
        this.fields = fields;
    }

    public static class IssueFieldEditRest extends IssueFieldDefinitionRest
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

}
