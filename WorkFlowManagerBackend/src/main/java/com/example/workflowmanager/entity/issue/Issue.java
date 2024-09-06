package com.example.workflowmanager.entity.issue;

import com.example.workflowmanager.entity.chat.Chat;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.project.Project;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
public class Issue
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    private String description;
    @Column(nullable = false)
    private LocalDateTime created;
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;
    @Column(name = "project_id")
    private Long projectId;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "category", nullable = false)
    private String category;
    @ManyToOne(optional = false)
    @JoinColumn(name = "sourceOrganization_id", referencedColumnName = "id")
    private Organization sourceOrganization;
    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Organization organization;
    @ManyToOne()
    @JoinColumn(name = "project_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Project project;
    @ManyToOne(optional = false)
    @JoinColumns(value = {
        @JoinColumn(name = "status", referencedColumnName = "status", insertable = false, updatable = false),
        @JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable = false, updatable = false),
    })
    private IssueStatus issueStatus;
    @ManyToOne(optional = false)
    @JoinColumns(value = {
        @JoinColumn(name = "category", referencedColumnName = "category", insertable = false, updatable = false),
        @JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable = false, updatable = false),
    })
    private IssueCategory issueCategory;
    @OneToMany(mappedBy = "issue")
    private Set<IssueField> fields;
    @OneToOne(optional = false)
    private Chat chat;

    public Issue()
    {
        // for hibernate
    }

    public Long getId()
    {
        return id;
    }

    protected void setId(final Long id)
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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public LocalDateTime getCreated()
    {
        return created;
    }

    public void setCreated(final LocalDateTime created)
    {
        this.created = created;
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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(final String status)
    {
        this.status = status;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(final String category)
    {
        this.category = category;
    }

    public Organization getSourceOrganization()
    {
        return sourceOrganization;
    }

    public void setSourceOrganization(
        final Organization sourceOrganization)
    {
        this.sourceOrganization = sourceOrganization;
    }

    public Organization getOrganization()
    {
        return organization;
    }

    protected void setOrganization(
        final Organization organization)
    {
        this.organization = organization;
    }

    public Project getProject()
    {
        return project;
    }

    protected void setProject(final Project project)
    {
        this.project = project;
    }

    public IssueStatus getIssueStatus()
    {
        return issueStatus;
    }

    protected void setIssueStatus(final IssueStatus issueStatus)
    {
        this.issueStatus = issueStatus;
    }

    public IssueCategory getIssueCategory()
    {
        return issueCategory;
    }

    protected void setIssueCategory(final IssueCategory issueCategory)
    {
        this.issueCategory = issueCategory;
    }

    public Set<IssueField> getFields()
    {
        return fields;
    }

    protected void setFields(final Set<IssueField> fields)
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

    public Chat getChat()
    {
        return chat;
    }

    public void setChat(final Chat chat)
    {
        this.chat = chat;
    }

}
