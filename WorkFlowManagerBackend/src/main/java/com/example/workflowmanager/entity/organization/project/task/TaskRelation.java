package com.example.workflowmanager.entity.organization.project.task;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class TaskRelation
{
    @EmbeddedId
    private TaskRelationId id;
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;
    @Column(name = "project_id", nullable = false)
    private Long projectId;
    @ManyToOne()
    @JoinColumns(value = {
        @JoinColumn(name = "source_task_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable = false, updatable = false),
        @JoinColumn(name = "project_id", referencedColumnName = "project_id", insertable = false, updatable = false),
    })
    private Task sourceTask;
    @ManyToOne()
    @JoinColumns(value = {
        @JoinColumn(name = "target_task_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable = false, updatable = false),
        @JoinColumn(name = "project_id", referencedColumnName = "project_id", insertable = false, updatable = false),
    })
    private Task targetTask;

    public TaskRelation(final TaskRelationId id, final Long organizationId,
        final Long projectId)
    {
        this.id = id;
        this.organizationId = organizationId;
        this.projectId = projectId;
    }

    protected TaskRelation()
    {
        // for hibernate
    }

    public TaskRelationId getId()
    {
        return id;
    }

    protected void setId(final TaskRelationId id)
    {
        this.id = id;
    }

    public Long getOrganizationId()
    {
        return organizationId;
    }

    protected void setOrganizationId(final Long organizationId)
    {
        this.organizationId = organizationId;
    }

    public Long getProjectId()
    {
        return projectId;
    }

    protected void setProjectId(final Long projectId)
    {
        this.projectId = projectId;
    }

    public Task getSourceTask()
    {
        return sourceTask;
    }

    protected void setSourceTask(final Task sourceTask)
    {
        this.sourceTask = sourceTask;
    }

    public Task getTargetTask()
    {
        return targetTask;
    }

    protected void setTargetTask(final Task targetTask)
    {
        this.targetTask = targetTask;
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
        final TaskRelation that = (TaskRelation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
