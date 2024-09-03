package com.example.workflowmanager.entity.organization.project.task;

import com.example.workflowmanager.entity.organization.project.Project;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "column_order"})})
public class TaskColumn
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Short columnOrder;
    @Column(name = "project_id", nullable = false)
    private Long projectId;
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;
    @ManyToOne(optional = false)
    @JoinColumns(value = {
        @JoinColumn(name = "project_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable = false, updatable = false)
    })
    private Project project;
    @OneToMany(mappedBy = "taskColumn")
    private Set<Task> tasks;

    public Long getId()
    {
        return id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Short getColumnOrder()
    {
        return columnOrder;
    }

    public void setColumnOrder(final Short columnOrder)
    {
        this.columnOrder = columnOrder;
    }

    protected Long getProjectId()
    {
        return projectId;
    }

    protected void setProjectId(final Long projectId)
    {
        this.projectId = projectId;
    }

    protected Long getOrganizationId()
    {
        return organizationId;
    }

    protected void setOrganizationId(final Long organizationId)
    {
        this.organizationId = organizationId;
    }

    public Project getProject()
    {
        return project;
    }

    public void setProject(final Project project)
    {
        setProjectId(project.getId());
        setOrganizationId(project.getOrganization().getId());
        this.project = project;
    }

    public Set<Task> getTasks()
    {
        return tasks;
    }

    public void setTasks(final Set<Task> tasks)
    {
        this.tasks = tasks;
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
        final TaskColumn that = (TaskColumn) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
