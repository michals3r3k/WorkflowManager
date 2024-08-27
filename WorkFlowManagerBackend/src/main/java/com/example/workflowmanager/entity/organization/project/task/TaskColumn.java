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
    @Column(nullable = false, name = "organization_id", insertable = false, updatable = false)
    private Long organizationId;
    @Column(nullable = false, name = "project_id", insertable = false, updatable = false)
    private Long projectId;
    @ManyToOne(optional = false)
    @JoinColumns({
        @JoinColumn(name = "project_id", referencedColumnName = "id"),
        @JoinColumn(name = "organization_id", referencedColumnName = "organization_id")
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

    public Project getProject()
    {
        return project;
    }

    public void setProject(
        final Project project)
    {
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
