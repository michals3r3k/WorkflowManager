package com.example.workflowmanager.rest.issue;

import java.util.List;

public class IssueDefinitionRest
{
    private List<String> statuses;
    private List<String> categories;
    private List<IssueFieldDefinitionRest> fields;
    private List<String> constStatuses;

    IssueDefinitionRest(final List<String> statuses,
        final List<String> categories,
        final List<IssueFieldDefinitionRest> fields,
        final List<String> constStatuses)
    {
        this.statuses = statuses;
        this.categories = categories;
        this.fields = fields;
        this.constStatuses = constStatuses;
    }

    public IssueDefinitionRest()
    {
        // for Spring
    }

    public List<String> getStatuses()
    {
        return statuses;
    }

    public void setStatuses(final List<String> statuses)
    {
        this.statuses = statuses;
    }

    public List<String> getCategories()
    {
        return categories;
    }

    public void setCategories(final List<String> categories)
    {
        this.categories = categories;
    }

    public List<IssueFieldDefinitionRest> getFields()
    {
        return fields;
    }

    public void setFields(
        final List<IssueFieldDefinitionRest> fields)
    {
        this.fields = fields;
    }

    public List<String> getConstStatuses()
    {
        return constStatuses;
    }

    public void setConstStatuses(final List<String> constStatuses)
    {
        this.constStatuses = constStatuses;
    }

}
