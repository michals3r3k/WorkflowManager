package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.entity.issue.IssueFieldType;

import java.util.List;

public class IssueFormRest
{
    private Long issueId;
    private String title;
    private String description;
    private String status;
    private String category;
    private List<IssueFieldEditRest> fields;
    private List<String> statusOptions;
    private List<String> categoryOptions;

    IssueFormRest(final Long issueId, final String title, final String description,
        final String status, final String category, final List<IssueFieldEditRest> fields,
        final List<String> statusOptions, final List<String> categoryOptions)
    {
        this.issueId = issueId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.category = category;
        this.fields = fields;
        this.statusOptions = statusOptions;
        this.categoryOptions = categoryOptions;
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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(final String status)
    {
        this.status = status;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(final String category)
    {
        this.category = category;
    }

    public List<IssueFieldEditRest> getFields()
    {
        return fields;
    }

    public void setFields(final List<IssueFieldEditRest> fields)
    {
        this.fields = fields;
    }

    public List<String> getStatusOptions()
    {
        return statusOptions;
    }

    public void setStatusOptions(final List<String> statusOptions)
    {
        this.statusOptions = statusOptions;
    }

    public List<String> getCategoryOptions()
    {
        return categoryOptions;
    }

    public void setCategoryOptions(final List<String> categoryOptions)
    {
        this.categoryOptions = categoryOptions;
    }

    public Long getIssueId()
    {
        return issueId;
    }

    public void setIssueId(final Long issueId)
    {
        this.issueId = issueId;
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
