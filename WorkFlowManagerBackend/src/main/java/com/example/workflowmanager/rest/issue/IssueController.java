package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.db.issue.IssueRepository;
import com.example.workflowmanager.db.organization.OrganizationInProjectRepository;
import com.example.workflowmanager.db.organization.project.ProjectRepository;
import com.example.workflowmanager.entity.issue.Issue;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.OrganizationInProjectId;
import com.example.workflowmanager.entity.organization.OrganizationInvitationStatus;
import com.example.workflowmanager.entity.organization.project.Project;
import com.example.workflowmanager.service.organization.OrganizationInProjectService;
import com.example.workflowmanager.service.project.ProjectCreateRest;
import com.example.workflowmanager.service.project.ProjectCreateService;
import com.example.workflowmanager.service.project.ProjectCreateService.ProjectCreateResult;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class IssueController
{
    private static final DateTimeFormatter DTF_VAL = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final IssueRepository issueRepository;
    private final OrganizationInProjectRepository oipRepository;
    private final ProjectRepository projectRepository;
    private final ProjectCreateService projectService;
    private final OrganizationInProjectService oipService;

    public IssueController(final IssueRepository issueRepository,
        final OrganizationInProjectRepository oipRepository,
        final ProjectRepository projectRepository,
        final ProjectCreateService projectService,
        final OrganizationInProjectService oipService)
    {
        this.issueRepository = issueRepository;
        this.oipRepository = oipRepository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.oipService = oipService;
    }

    @GetMapping("/api/organization/{organizationId}/issue/{issueId}/to-existing-project/{projectId}")
    @Transactional
    public ResponseEntity<ServiceResult<?>> addToProject(@PathVariable Long organizationId,
        @PathVariable Long issueId, @PathVariable Long projectId)
    {
        addToProject(issueId, projectId);
        return ResponseEntity.ok(ServiceResult.ok());
    }

    @PostMapping("/api/organization/{organizationId}/issue/{issueId}/to-new-project")
    @Transactional
    public ResponseEntity<ProjectCreateResult> addToProject(@PathVariable Long organizationId,
        @PathVariable Long issueId, @RequestBody ProjectCreateRest projectRest)
    {
        final ProjectCreateResult result = projectService.create(organizationId, projectRest);
        if(result.isSuccess())
        {
            addToProject(issueId, result.getProjectId());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/organization/{organizationId}/issues")
    @Transactional
    public List<IssueRest> getOrganizationIssueList(@PathVariable Long organizationId)
    {
        return getIssueRest(organizationId,
            issueRepository.getOrganizationIssues(Collections.singleton(organizationId)));
    }

    @GetMapping("/api/organization/{organizationId}/issues/project/{projectId}")
    @Transactional
    public List<IssueRest> getProjectIssueList(@PathVariable Long organizationId, @PathVariable Long projectId)
    {
        return getIssueRest(organizationId,
            getProjectIssues(organizationId, projectId));
    }

    private void addToProject(final Long issueId, final Long projectId)
    {
        final Issue issue = issueRepository.getReferenceById(issueId);
        final Long organizationId = issue.getSourceOrganization().getId();
        final OrganizationInProjectId oipId =
            new OrganizationInProjectId(organizationId, projectId);
        oipService.create(oipId, OrganizationInvitationStatus.ACCEPTED);
        issue.setProjectId(projectId);
        issueRepository.save(issue);
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
        final Project project = projectRepository.getReferenceById(projectId);
        return project.getOrganization().getId().equals(organizationId);
    }

    private static List<IssueRest> getIssueRest(final Long organizationId,
        final List<Issue> issues)
    {
        return issues.stream()
            .sorted(Comparator.comparing(Issue::getId))
            .map(issue -> new IssueRest(issue,
                !issue.getSourceOrganization().getId().equals(organizationId)))
            .collect(Collectors.toList());
    }

    public static class IssueRest
    {
        private final Long id;
        private final Long organizationId;
        private final String organizationName;
        private final String projectName;
        private final boolean fromClient;
        private final String status;
        private final String created;
        private final String title;

        private IssueRest(final Issue issue, final boolean fromClient) {
            final Organization organization = fromClient ? issue.getSourceOrganization() : issue.getOrganization();
            this.id = issue.getId();
            this.organizationId = organization.getId();
            this.organizationName = organization.getName();
            this.projectName = ObjectUtils.accessNullable(issue.getProject(), Project::getName);
            this.fromClient = fromClient;
            this.status = "NEW";
            this.created = issue.getCreated().format(DTF_VAL);
            this.title = issue.getTitle();
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

        public String getTitle()
        {
            return title;
        }

    }

}
