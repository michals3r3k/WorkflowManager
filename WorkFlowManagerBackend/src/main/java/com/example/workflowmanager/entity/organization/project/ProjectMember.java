package com.example.workflowmanager.entity.organization.project;

import com.example.workflowmanager.entity.organization.OrganizationMember;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class ProjectMember
{
    @EmbeddedId
    private ProjectMemberId id;

    @ManyToOne()
    @JoinColumns({
        @JoinColumn(name = "organizationId", referencedColumnName = "organizationId", insertable=false, updatable=false),
        @JoinColumn(name = "userId", referencedColumnName = "userId", insertable=false, updatable=false)
    })
    private OrganizationMember member;
    @ManyToOne()
    @JoinColumn(name = "projectId",
        referencedColumnName = "id",
        insertable = false,
        updatable = false)
    private Project project;

    public ProjectMember(ProjectMemberId id)
    {
        this.id = id;
    }

    protected ProjectMember()
    {
        // for hibernate
    }

    public ProjectMemberId getId()
    {
        return id;
    }

    protected void setId(
        ProjectMemberId id)
    {
        this.id = id;
    }

    public OrganizationMember getMember()
    {
        return member;
    }

    protected void setMember(
        OrganizationMember member)
    {
        this.member = member;
    }

    public Project getProject()
    {
        return project;
    }

    protected void setProject(
        Project project)
    {
        this.project = project;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        ProjectMember that = (ProjectMember) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
