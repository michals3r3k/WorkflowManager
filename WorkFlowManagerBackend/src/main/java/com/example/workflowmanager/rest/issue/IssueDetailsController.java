package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.db.issue.IssueRepository;
import com.example.workflowmanager.entity.issue.Issue;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.project.Project;
import com.example.workflowmanager.service.utils.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;
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

    @GetMapping("/api/organization/{organizationId}/issue-details-for-organization/{issueId}")
    @Transactional
    public IssueDetailsRest getOrganizationIssueDetailsForOrganization(@PathVariable Long organizationId, @PathVariable Long issueId)
    {
        return getDetailsRest(issueId, false);
    }

    @GetMapping("/api/organization/{organizationId}/issue-details-for-client/{issueId}")
    @Transactional
    public IssueDetailsRest getOrganizationIssueDetailsForClient(@PathVariable Long organizationId, @PathVariable Long issueId)
    {
        return getDetailsRest(issueId, true);
    }

    private IssueDetailsRest getDetailsRest(final Long issueId, final boolean forClient)
    {
        final Issue issue = issueRepository.getReferenceById(issueId);
        final Organization source = issue.getSourceOrganization();
        final Organization destination = issue.getOrganization();
        final Long projectId = ObjectUtils.accessNullable(issue.getProject(), Project::getId);
        final String projectName = ObjectUtils.accessNullable(issue.getProject(), Project::getName);
        final IssueFormRest form = issueFormFactory.getForm(destination.getId(), issue, forClient);
        final Long chatId = issue.getChat().getId();
        return new IssueDetailsRest(issueId, issue.getTitle(), source.getName(),
            destination.getName(), projectId, projectName, source.getId(),
            destination.getId(), chatId, form);
    }

    public static class IssueDetailsRest
    {
        private Long id;
        private String title;
        private String sourceOrganizationName;
        private String destinationOrganizationName;
        private Long projectId;
        private String projectName;
        private Long sourceOrganizationId;
        private Long destinationOrganizationId;
        private Long chatId;
        private IssueFormRest form;

        private IssueDetailsRest(final Long id,
            final String title, final String sourceOrganizationName,
            final String destinationOrganizationName, final Long projectId,
            final String projectName, final Long sourceOrganizationId,
            final Long destinationOrganizationId, final Long chatId, final IssueFormRest form)
        {
            this.id = id;
            this.title = title;
            this.sourceOrganizationName = sourceOrganizationName;
            this.destinationOrganizationName = destinationOrganizationName;
            this.projectId = projectId;
            this.projectName = projectName;
            this.sourceOrganizationId = sourceOrganizationId;
            this.destinationOrganizationId = destinationOrganizationId;
            this.chatId = chatId;
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

        public void setDestinationOrganizationId(final Long destinationOrganizationId)
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

        public Long getProjectId()
        {
            return projectId;
        }

        public void setProjectId(final Long projectId)
        {
            this.projectId = projectId;
        }

        public Long getChatId()
        {
            return chatId;
        }

        public void setChatId(final Long chatId)
        {
            this.chatId = chatId;
        }

    }

}
