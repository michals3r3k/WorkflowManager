package com.example.workflowmanager.entity.organization.project.task;

import com.example.workflowmanager.entity.chat.Chat;
import com.example.workflowmanager.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
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
    @Column(name = "task_column_id", nullable = false)
    private Long taskColumnId;
    @Column(name = "project_id", nullable = false)
    private Long projectId;
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;
    @ManyToOne(optional = false)
    @JoinColumns(value = {
        @JoinColumn(name = "task_column_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable = false, updatable = false),
        @JoinColumn(name = "project_id", referencedColumnName = "project_id", insertable = false, updatable = false)
    })
    private TaskColumn taskColumn;
    @ManyToOne
    @JoinColumns(value = {
        @JoinColumn(name = "parent_task_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable = false, updatable = false),
        @JoinColumn(name = "project_id", referencedColumnName = "project_id", insertable = false, updatable = false)
    })
    private Task parentTask;
    @OneToMany(mappedBy = "parentTask")
    private Set<Task> subTasks;
    @OneToMany(mappedBy = "task")
    private Set<TaskMember> members;
    @OneToOne(optional = false)
    private Chat chat;
    @ManyToOne(optional = false)
    private User creator;

    public Task(final String title, final LocalDateTime createTime,
        final TaskColumn taskColumn,
        final Chat chat, User creator)
    {
        setTitle(title);
        setCreateTime(createTime);
        setTaskColumn(taskColumn);
        setSubTasks(new HashSet<>());
        setMembers(new HashSet<>());
        setChat(chat);
        setCreator(creator);
    }

    protected Task()
    {
        // for hibernate
    }

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

    protected Long getTaskColumnId()
    {
        return taskColumnId;
    }

    protected void setTaskColumnId(final Long taskColumnId)
    {
        this.taskColumnId = taskColumnId;
    }

    public Long getProjectId()
    {
        return projectId;
    }

    protected void setProjectId(final Long projectId)
    {
        this.projectId = projectId;
    }

    public Long getOrganizationId()
    {
        return organizationId;
    }

    protected void setOrganizationId(final Long organizationId)
    {
        this.organizationId = organizationId;
    }

    public TaskColumn getTaskColumn()
    {
        return taskColumn;
    }

    public void setTaskColumn(final TaskColumn taskColumn)
    {
        setTaskColumnId(taskColumn.getId());
        setProjectId(taskColumn.getProjectId());
        setOrganizationId(taskColumn.getOrganizationId());
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

    public Chat getChat()
    {
        return chat;
    }

    public void setChat(final Chat chat)
    {
        this.chat = chat;
    }

    public User getCreator()
    {
        return creator;
    }

    public void setCreator(final User creator)
    {
        this.creator = creator;
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
