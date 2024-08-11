package com.example.workflowmanager.entity.issue;

import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.project.Project;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
public class Issue
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "organizationId")
    private Organization organization;
    @ManyToOne()
    @JoinColumn(name = "projectId")
    private Project project;
    @OneToMany(mappedBy = "issue")
    private Set<IssueField> fields;

    public Long getId()
    {
        return id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    public Organization getOrganization()
    {
        return organization;
    }

    public void setOrganization(
        final Organization organization)
    {
        this.organization = organization;
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

    public Set<IssueField> getFields()
    {
        return fields;
    }

    public void setFields(
        final Set<IssueField> fields)
    {
        this.fields = fields;
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
        final Issue issue = (Issue) o;
        return Objects.equals(id, issue.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
