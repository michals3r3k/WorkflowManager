package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.db.issue.IssueRepository;
import com.example.workflowmanager.db.organization.OrganizationInProjectRepository;
import com.example.workflowmanager.entity.issue.Issue;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.OrganizationInProjectId;
import com.example.workflowmanager.entity.organization.OrganizationInProjectRole;
import com.example.workflowmanager.entity.organization.project.Project;
import com.example.workflowmanager.service.utils.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class IssueDetailsController
{
    private static final DateTimeFormatter DTF_VAL = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final IssueRepository issueRepository;
    private final OrganizationInProjectRepository oipRepository;

    public IssueDetailsController(final IssueRepository issueRepository,
        final OrganizationInProjectRepository oipRepository)
    {
        this.issueRepository = issueRepository;
        this.oipRepository = oipRepository;
    }

    @GetMapping("/api/organization/{organizationId}/issue-details")
    public List<IssueDetailsRest> getOrganizationIssueList(@PathVariable Long organizationId)
    {
        return getIssueDetailsRest(organizationId,
            issueRepository.getOrganizationIssues(Collections.singleton(organizationId)));
    }

    @GetMapping("/api/organization/{organizationId}/issue-details/project/{projectId}")
    public List<IssueDetailsRest> getProjectIssueList(@PathVariable Long organizationId, @PathVariable Long projectId)
    {
        return getIssueDetailsRest(organizationId,
            getProjectIssues(organizationId, projectId));
    }

    private List<Issue> getProjectIssues(Long organizationId, Long projectId)
    {
        if(isOwnProject(organizationId, projectId))
        {
            return issueRepository.getProjectIssues(Collections.singleton(projectId));
        }
        return issueRepository.getProjectIssues(Collections.singleton(organizationId),
            Collections.singleton(projectId));
    }

    private boolean isOwnProject(final Long organizationId, final Long projectId)
    {
        return oipRepository.getReferenceById(new OrganizationInProjectId(organizationId, projectId))
            .getRole() == OrganizationInProjectRole.OWNER;
    }

    private static List<IssueDetailsRest> getIssueDetailsRest(final Long organizationId,
        final List<Issue> issues)
    {
        return issues.stream()
            .sorted(Comparator.comparing(Issue::getId))
            .map(issue -> new IssueDetailsRest(issue,
                !issue.getSourceOrganization().getId().equals(organizationId)))
            .collect(Collectors.toList());
    }

    public static class IssueDetailsRest
    {
        private final Long id;
        private final Long organizationId;
        private final String organizationName;
        private final String projectName;
        private final boolean fromClient;
        private final String status;
        private final String created;

        private IssueDetailsRest(final Issue issue, final boolean fromClient) {
            this.id = issue.getId();
            Organization organization = fromClient ? issue.getSourceOrganization() : issue.getOrganization();
            this.organizationId = organization.getId();
            this.organizationName = organization.getName();
            this.projectName = ObjectUtils.accessNullable(issue.getProject(), Project::getName);
            this.fromClient = fromClient;
            this.status = "NEW";
            this.created = issue.getCreated().format(DTF_VAL);
        }

        public Long getId()
        {
            return id;
        }

        public Long getOrganizationId()
        {
            return organizationId;
        }

        public String getOrganizationName()
        {
            return organizationName;
        }

        public String getProjectName()
        {
            return projectName;
        }

        public boolean isFromClient()
        {
            return fromClient;
        }

        public String getStatus()
        {
            return status;
        }

        public String getCreated()
        {
            return created;
        }

    }

}
