package com.example.workflowmanager.entity.issue;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IssueFieldId implements Serializable
{
    private Long issueId;
    private Long definitionId;

    public IssueFieldId(Long issueId, Long definitionId)
    {
        this.issueId = issueId;
        this.definitionId = definitionId;
    }

    protected IssueFieldId()
    {
        // for hibernate
    }

    public Long getIssueId()
    {
        return issueId;
    }

    public void setIssueId(final Long issueId)
    {
        this.issueId = issueId;
    }

    public Long getDefinitionId()
    {
        return definitionId;
    }

    public void setDefinitionId(final Long definitionId)
    {
        this.definitionId = definitionId;
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
        final IssueFieldId that = (IssueFieldId) o;
        return Objects.equals(issueId, that.issueId)
            && Objects.equals(definitionId, that.definitionId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(issueId, definitionId);
    }

}
