package com.example.workflowmanager.entity.organization.project.task;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
public class Task
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private LocalDateTime createTime;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
    private LocalDateTime deadlineDate;
    @Column(nullable = false, name = "task_column_id", insertable = false, updatable = false)
    private Long taskColumnId;
    @Column(nullable = false, name = "organization_id", insertable = false, updatable = false)
    private Long organizationId;
    @Column(nullable = false, name = "project_id", insertable = false, updatable = false)
    private Long projectId;
    @ManyToOne(optional = false)
    @JoinColumns({
        @JoinColumn(name = "task_column_id", referencedColumnName = "id"),
        @JoinColumn(name = "project_id", referencedColumnName = "project_id"),
        @JoinColumn(name = "organization_id", referencedColumnName = "organization_id")
    })
    private TaskColumn taskColumn;
    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;
    @OneToMany(mappedBy = "parentTask")
    private Set<Task> subTasks;
    @OneToMany(mappedBy = "task")
    private Set<TaskMember> members;

    public Long getId()
    {
        return id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(final String title)
    {
        this.title = title;
    }

    public LocalDateTime getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime)
    {
        this.createTime = createTime;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public LocalDateTime getStartDate()
    {
        return startDate;
    }

    public void setStartDate(final LocalDateTime startDate)
    {
        this.startDate = startDate;
    }

    public LocalDateTime getFinishDate()
    {
        return finishDate;
    }

    public void setFinishDate(final LocalDateTime finishDate)
    {
        this.finishDate = finishDate;
    }

    public LocalDateTime getDeadlineDate()
    {
        return deadlineDate;
    }

    public void setDeadlineDate(final LocalDateTime deadlineDate)
    {
        this.deadlineDate = deadlineDate;
    }

    public Long getTaskColumnId()
    {
        return taskColumnId;
    }

    public void setTaskColumnId(final Long taskColumnId)
    {
        this.taskColumnId = taskColumnId;
    }

    public Long getOrganizationId()
    {
        return organizationId;
    }

    public void setOrganizationId(final Long organizationId)
    {
        this.organizationId = organizationId;
    }

    public Long getProjectId()
    {
        return projectId;
    }

    public void setProjectId(final Long projectId)
    {
        this.projectId = projectId;
    }

    public TaskColumn getTaskColumn()
    {
        return taskColumn;
    }

    public void setTaskColumn(
        final TaskColumn taskColumn)
    {
        this.taskColumn = taskColumn;
    }

    public Task getParentTask()
    {
        return parentTask;
    }

    public void setParentTask(
        final Task parentTask)
    {
        this.parentTask = parentTask;
    }

    public Set<Task> getSubTasks()
    {
        return subTasks;
    }

    public void setSubTasks(
        final Set<Task> subTasks)
    {
        this.subTasks = subTasks;
    }

    public Set<TaskMember> getMembers()
    {
        return members;
    }

    public void setMembers(final Set<TaskMember> members)
    {
        this.members = members;
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
        final Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
