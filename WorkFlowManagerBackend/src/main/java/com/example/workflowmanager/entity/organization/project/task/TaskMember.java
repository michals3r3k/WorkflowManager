package com.example.workflowmanager.entity.organization.project.task;

import com.example.workflowmanager.entity.organization.OrganizationMember;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class TaskMember
{
    @EmbeddedId
    private TaskMemberId id;
    @ManyToOne()
    @JoinColumns({
        @JoinColumn(name = "taskId", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "organizationId", referencedColumnName = "organization_id", insertable = false, updatable = false),
    })
    private Task task;
    @ManyToOne()
    @JoinColumns({
        @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false),
        @JoinColumn(name = "organizationId", referencedColumnName = "organizationId", insertable = false, updatable = false),
    })
    private OrganizationMember member;

    public TaskMember(final TaskMemberId id)
    {
        this.id = id;
    }

    protected TaskMember()
    {
        // for hibernate
    }

    public TaskMemberId getId()
    {
        return id;
    }

    public void setId(final TaskMemberId id)
    {
        this.id = id;
    }

    public Task getTask()
    {
        return task;
    }

    public void setTask(final Task task)
    {
        this.task = task;
    }

    public OrganizationMember getMember()
    {
        return member;
    }

    public void setMember(final OrganizationMember member)
    {
        this.member = member;
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
        final TaskMember that = (TaskMember) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
