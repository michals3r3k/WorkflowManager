package com.example.workflowmanager.entity.organization.project.task;

import com.example.workflowmanager.entity.chat.Chat;
import com.example.workflowmanager.entity.issue.Issue;
import com.example.workflowmanager.entity.organization.project.Project;
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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;
    @Column(nullable = false)
    private Short taskOrder;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
    private LocalDateTime deadlineDate;
    @Column(name = "parent_task_id")
    private Long parentTaskId;
    @Column(name = "issue_id")
    private Long issueId;
    @Column(name = "task_column_id")
    private Long taskColumnId;
    @Column(name = "project_id", nullable = false)
    private Long projectId;
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;
    @ManyToOne()
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
    @ManyToOne(optional = false)
    @JoinColumns(value = {
        @JoinColumn(name = "project_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable = false, updatable = false)
    })
    private Project project;
    @ManyToOne()
    @JoinColumns(value = {
        @JoinColumn(name = "issue_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable = false, updatable = false),
        @JoinColumn(name = "project_id", referencedColumnName = "project_id", insertable = false, updatable = false),
    })
    private Issue issue;

    public Task(final String title, final LocalDateTime createTime, final Chat chat,
        final Long organizationId, final Long projectId, final Long issueId,
        final Long taskColumnId, final User creator, final TaskPriority priority,
        final Short taskOrder)
    {
        setTitle(title);
        setCreateTime(createTime);
        setSubTasks(new HashSet<>());
        setMembers(new HashSet<>());
        setProjectId(projectId);
        setOrganizationId(organizationId);
        setTaskColumnId(taskColumnId);
        setIssueId(issueId);
        setChat(chat);
        setCreator(creator);
        setPriority(priority);
        setTaskOrder(taskOrder);
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

    public Long getTaskColumnId()
    {
        return taskColumnId;
    }

    public void setTaskColumnId(final Long taskColumnId)
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

    public void setParentTask(final Task parentTask)
    {
        this.parentTask = parentTask;
    }

    public Set<Task> getSubTasks()
    {
        return subTasks;
    }

    public void setSubTasks(final Set<Task> subTasks)
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

    public Long getParentTaskId()
    {
        return parentTaskId;
    }

    public void setParentTaskId(final Long parentTaskId)
    {
        this.parentTaskId = parentTaskId;
    }

    public TaskPriority getPriority()
    {
        return priority;
    }

    public void setPriority(
        final TaskPriority priority)
    {
        this.priority = priority;
    }

    public Short getTaskOrder()
    {
        return taskOrder;
    }

    public void setTaskOrder(final Short taskOrder)
    {
        this.taskOrder = taskOrder;
    }

    public Project getProject()
    {
        return project;
    }

    protected void setProject(final Project project)
    {
        this.project = project;
    }

    public Issue getIssue()
    {
        return issue;
    }

    public void setIssue(final Issue issue)
    {
        this.issue = issue;
    }

    public Long getIssueId()
    {
        return issueId;
    }

    public void setIssueId(final Long issueId)
    {
        this.issueId = issueId;
    }

}
