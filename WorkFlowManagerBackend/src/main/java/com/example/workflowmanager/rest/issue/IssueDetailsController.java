package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.db.issue.IssueRepository;
import com.example.workflowmanager.entity.issue.Issue;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.project.Project;
import com.example.workflowmanager.service.utils.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class IssueDetailsController
{
    private final IssueRepository issueRepository;
    private final IssueFormFactory issueFormFactory;

    public IssueDetailsController(final IssueRepository issueRepository,
        final IssueFormFactory issueFormFactory)
    {
        this.issueRepository = issueRepository;
        this.issueFormFactory = issueFormFactory;
    }

    @GetMapping("/api/organization/{organizationId}/issue-details/{issueId}")
    public IssueDetailsRest getOrganizationIssueDetails(@PathVariable Long organizationId, @PathVariable Long issueId)
    {
        final Issue issue = issueRepository.getReferenceById(issueId);
        final Organization source = issue.getSourceOrganization();
        final Organization destination = issue.getOrganization();
        final String projectName = ObjectUtils.accessNullable(issue.getProject(), Project::getName);
        final IssueFormRest form = issueFormFactory.getForm(destination.getId(), issue);
        return new IssueDetailsRest(issueId, issue.getTitle(), source.getName(), destination.getName(),
            projectName, source.getId(), destination.getId(), form);
    }

    public static class IssueDetailsRest
    {
        private Long id;
        private String title;
        private String sourceOrganizationName;
        private String destinationOrganizationName;
        private String projectName;
        private Long sourceOrganizationId;
        private Long destinationOrganizationId;
        private IssueFormRest form;

        public IssueDetailsRest(final Long id,
            final String title, final String sourceOrganizationName,
            final String destinationOrganizationName,
            final String projectName, final Long sourceOrganizationId,
            final Long destinationOrganizationId, final IssueFormRest form)
        {
            this.id = id;
            this.title = title;
            this.sourceOrganizationName = sourceOrganizationName;
            this.destinationOrganizationName = destinationOrganizationName;
            this.projectName = projectName;
            this.sourceOrganizationId = sourceOrganizationId;
            this.destinationOrganizationId = destinationOrganizationId;
            this.form = form;
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

        public String getSourceOrganizationName()
        {
            return sourceOrganizationName;
        }

        public void setSourceOrganizationName(
            final String sourceOrganizationName)
        {
            this.sourceOrganizationName = sourceOrganizationName;
        }

        public String getDestinationOrganizationName()
        {
            return destinationOrganizationName;
        }

        public void setDestinationOrganizationName(
            final String destinationOrganizationName)
        {
            this.destinationOrganizationName = destinationOrganizationName;
        }

        public String getProjectName()
        {
            return projectName;
        }

        public void setProjectName(final String projectName)
        {
            this.projectName = projectName;
        }

        public Long getSourceOrganizationId()
        {
            return sourceOrganizationId;
        }

        public void setSourceOrganizationId(final Long sourceOrganizationId)
        {
            this.sourceOrganizationId = sourceOrganizationId;
        }

        public Long getDestinationOrganizationId()
        {
            return destinationOrganizationId;
        }

        public void setDestinationOrganizationId(
            final Long destinationOrganizationId)
        {
            this.destinationOrganizationId = destinationOrganizationId;
        }

        public IssueFormRest getForm()
        {
            return form;
        }

        public void setForm(
            final IssueFormRest form)
        {
            this.form = form;
        }

    }

}
