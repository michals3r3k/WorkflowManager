package com.example.workflowmanager.entity.organization.project.task;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TaskMemberId implements Serializable
{
    private Long userId;
    private Long organizationId;
    private Long taskId;

    public TaskMemberId(final Long userId, final Long organizationId,
        final Long taskId)
    {
        this.userId = userId;
        this.organizationId = organizationId;
        this.taskId = taskId;
    }

    protected TaskMemberId()
    {
        // for hibernate
    }

    public Long getUserId()
    {
        return userId;
    }

    protected void setUserId(final Long userId)
    {
        this.userId = userId;
    }

    public Long getOrganizationId()
    {
        return organizationId;
    }

    protected void setOrganizationId(final Long organizationId)
    {
        this.organizationId = organizationId;
    }

    public Long getTaskId()
    {
        return taskId;
    }

    protected void setTaskId(final Long taskId)
    {
        this.taskId = taskId;
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
        final TaskMemberId that = (TaskMemberId) o;
        return Objects.equals(userId, that.userId)
            && Objects.equals(organizationId, that.organizationId)
            && Objects.equals(taskId, that.taskId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(userId, organizationId, taskId);
    }

}
