package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.db.issue.IssueRepository;
import com.example.workflowmanager.db.organization.OrganizationInProjectRepository;
import com.example.workflowmanager.db.organization.project.ProjectRepository;
import com.example.workflowmanager.entity.issue.Issue;
import com.example.workflowmanager.entity.issue.IssueStatus;
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
import com.google.common.base.Predicates;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
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
    public ResponseEntity<List<IssueRest>> getOrganizationIssueList(@PathVariable Long organizationId)
    {
        final List<Issue> issues = issueRepository.getOrganizationIssues(Collections.singleton(organizationId));
        return ResponseEntity.ok(getIssueRestList(issues,
            issue -> organizationId.equals(issue.getSourceOrganization().getId())));
    }

    @GetMapping("/api/organization/{organizationId}/issues-incoming/project/{projectId}")
    @Transactional
    public ResponseEntity<List<IssueGroupRest>> getProjectIssueIncomingList(@PathVariable Long organizationId, @PathVariable Long projectId)
    {
        final List<IssueGroupRest> issueGroupList = issueRepository.getProjectIssues(
            Collections.singleton(projectId)).stream()
            .collect(Collectors.groupingBy(Issue::getSourceOrganization))
            .entrySet().stream()
            .map(entry -> new IssueGroupRest(entry.getKey(),
                getIssueRestList(entry.getValue(), Predicates.alwaysFalse())))
            .sorted(Comparator.comparing(IssueGroupRest::getOrganizationName,
                    Comparator.naturalOrder())
                .thenComparing(IssueGroupRest::getOrganizationId))
            .collect(Collectors.toList());
        return ResponseEntity.ok(issueGroupList);
    }

    @GetMapping("/api/organization/{organizationId}/issues-outgoing/project/{projectId}")
    @Transactional
    public ResponseEntity<IssueGroupRest> getProjectIssueOutgoingList(
        @PathVariable Long organizationId, @PathVariable Long projectId)
    {
        final Organization organization = projectRepository.getReferenceById(
            projectId).getOrganization();
        final List<Issue> issues = issueRepository.getProjectIssues(Collections.singleton(projectId));
        return ResponseEntity.ok(new IssueGroupRest(organization, getIssueRestList(issues, Predicates.alwaysTrue())));
    }

    private static List<IssueRest> getIssueRestList(final Collection<Issue> issues,
        final Predicate<Issue> forClientPredicate)
    {
        return issues.stream()
            .map(issue -> new IssueRest(issue, forClientPredicate.test(issue)))
            .sorted(Comparator.comparing((IssueRest issue) -> issue.getStatus().equals(IssueStatus.NEW))
                .thenComparing(IssueRest::getId))
            .collect(Collectors.toList());
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

    public static class IssueRest
    {
        private final Long id;
        private final Long organizationId;
        private final String organizationName;
        private final String projectName;
        private final boolean forClient;
        private final String status;
        private final String category;
        private final String created;
        private final String title;

        private IssueRest(final Issue issue, final boolean forClient) {
            final Organization organizationToDisplay = forClient ? issue.getOrganization() : issue.getSourceOrganization();
            this.id = issue.getId();
            this.organizationId = organizationToDisplay.getId();
            this.organizationName = organizationToDisplay.getName();
            this.projectName = ObjectUtils.accessNullable(issue.getProject(), Project::getName);
            this.forClient = forClient;
            this.status = issue.getStatus();
            this.category = issue.getCategory();
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

        public String getStatus()
        {
            return status;
        }

        public String getCategory()
        {
            return category;
        }

        public String getCreated()
        {
            return created;
        }

        public String getTitle()
        {
            return title;
        }

        public boolean isForClient()
        {
            return forClient;
        }

    }

    public static class IssueGroupRest
    {
        private Organization organization;
        private List<IssueRest> issueRestList;

        public IssueGroupRest(final Organization organization,
            final List<IssueRest> issueRestList)
        {
            this.organization = organization;
            this.issueRestList = issueRestList;
        }

        private Long getOrganizationId()
        {
            return organization.getId();
        }

        public String getOrganizationName()
        {
            return organization.getName();
        }

        public List<IssueRest> getIssueRestList()
        {
            return issueRestList;
        }

    }

}
