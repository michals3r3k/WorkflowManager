package com.example.workflowmanager.entity.issue;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Check(name = "chk_value", constraints = "(" +
    "(date_value IS NULL AND number_value IS NULL AND text_value IS NULL AND flag_value IS NULL) " +
    "OR (date_value IS NOT NULL AND number_value IS NULL AND text_value IS NULL AND flag_value IS NULL) " +
    "OR (date_value IS NULL AND number_value IS NOT NULL AND text_value IS NULL AND flag_value IS NULL) " +
    "OR (date_value IS NULL AND number_value IS NULL AND text_value IS NOT NULL AND flag_value IS NULL) " +
    "OR (date_value IS NULL AND number_value IS NULL AND text_value IS NULL AND flag_value IS NOT NULL))")
public class IssueField
{
    @EmbeddedId
    private IssueFieldId id;
    private LocalDateTime dateValue;
    private BigDecimal numberValue;
    private String textValue;
    private Boolean flagValue;
    @ManyToOne()
    @JoinColumn(name = "issueId", insertable = false, updatable = false)
    private Issue issue;
    @ManyToOne()
    @JoinColumn(name = "definition_id", referencedColumnName = "id", insertable=false, updatable=false)
    private IssueFieldDefinition definition;

    public IssueField(IssueFieldId id)
    {
        this.id = id;
    }

    protected IssueField()
    {
        // for hibernate
    }

    public IssueFieldId getId()
    {
        return id;
    }

    public void setId(final IssueFieldId id)
    {
        this.id = id;
    }

    public LocalDateTime getDateValue()
    {
        return dateValue;
    }

    public void setDateValue(final LocalDateTime dateValue)
    {
        this.dateValue = dateValue;
    }

    public BigDecimal getNumberValue()
    {
        return numberValue;
    }

    public void setNumberValue(final BigDecimal numberValue)
    {
        this.numberValue = numberValue;
    }

    public String getTextValue()
    {
        return textValue;
    }

    public void setTextValue(final String textValue)
    {
        this.textValue = textValue;
    }

    public Boolean isFlagValue()
    {
        return flagValue;
    }

    public void setFlagValue(final Boolean flagValue)
    {
        this.flagValue = flagValue;
    }

    public Issue getIssue()
    {
        return issue;
    }

    public void setIssue(final Issue issue)
    {
        this.issue = issue;
    }

    public IssueFieldDefinition getDefinition()
    {
        return definition;
    }

    public void setDefinition(
        final IssueFieldDefinition definition)
    {
        this.definition = definition;
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
        final IssueField that = (IssueField) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
