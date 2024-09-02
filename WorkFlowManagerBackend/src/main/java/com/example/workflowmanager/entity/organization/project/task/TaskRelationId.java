package com.example.workflowmanager.entity.organization.project.task;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TaskRelationId implements Serializable
{
    @Column(name = "source_task_id")
    private Long sourceTaskId;
    @Column(name = "target_task_id")
    private Long targetTaskId;
    @Enumerated(EnumType.STRING)
    private TaskRelationType relationType;

    public TaskRelationId(final Long sourceTaskId, final Long targetTaskId,
        final TaskRelationType relationType)
    {
        this.sourceTaskId = sourceTaskId;
        this.targetTaskId = targetTaskId;
        this.relationType = relationType;
    }

    protected TaskRelationId()
    {
        // for hibernate
    }

    public Long getSourceTaskId()
    {
        return sourceTaskId;
    }

    protected void setSourceTaskId(final Long sourceTaskId)
    {
        this.sourceTaskId = sourceTaskId;
    }

    public Long getTargetTaskId()
    {
        return targetTaskId;
    }

    protected void setTargetTaskId(final Long targetTaskId)
    {
        this.targetTaskId = targetTaskId;
    }

    public TaskRelationType getRelationType()
    {
        return relationType;
    }

    protected void setRelationType(final TaskRelationType relationType)
    {
        this.relationType = relationType;
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
        final TaskRelationId that = (TaskRelationId) o;
        return Objects.equals(sourceTaskId, that.sourceTaskId)
            && Objects.equals(targetTaskId, that.targetTaskId)
            && relationType == that.relationType;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(sourceTaskId, targetTaskId, relationType);
    }

}
