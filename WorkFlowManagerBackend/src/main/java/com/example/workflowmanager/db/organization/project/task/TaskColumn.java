package com.example.workflowmanager.db.organization.project.task;

import com.example.workflowmanager.entity.organization.project.Project;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "order"})})
public class TaskColumn
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Short order;
    @ManyToOne(optional = false)
    private Project project;

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

    public Short getOrder()
    {
        return order;
    }

    public void setOrder(final Short order)
    {
        this.order = order;
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
