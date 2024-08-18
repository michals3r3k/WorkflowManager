package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.db.issue.IssueFieldDefinitionRepository;
import com.example.workflowmanager.db.issue.IssueRepository;
import com.example.workflowmanager.db.organization.project.ProjectRepository;
import com.example.workflowmanager.entity.issue.*;
import com.example.workflowmanager.entity.organization.OrganizationInProjectId;
import com.example.workflowmanager.entity.organization.OrganizationInProjectRole;
import com.example.workflowmanager.entity.organization.OrganizationInvitationStatus;
import com.example.workflowmanager.service.issue.OrganizationIssueCreateService;
import com.example.workflowmanager.service.organization.OrganizationInProjectService;
import com.example.workflowmanager.service.project.ProjectCreateRest;
import com.example.workflowmanager.service.project.ProjectCreateService;
import com.example.workflowmanager.service.project.ProjectCreateService.ProjectCreateResult;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.Maps;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class IssueFieldsController
{
    private static final DateTimeFormatter DTF_VAL = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final IssueRepository issueRepository;
    private final IssueFieldDefinitionRepository ifdRepository;
    private final ProjectRepository projectRepository;
    private final ProjectCreateService projectService;
    private final OrganizationInProjectService oipService;
    private final OrganizationIssueCreateService organizationIssueCreateService;

    public IssueFieldsController(
        final IssueRepository issueRepository,
        final IssueFieldDefinitionRepository ifdRepository,
        final ProjectRepository projectRepository,
        final ProjectCreateService projectService,
        final OrganizationInProjectService oipService,
        final OrganizationIssueCreateService organizationIssueCreateService)
    {
        this.issueRepository = issueRepository;
        this.ifdRepository = ifdRepository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.oipService = oipService;
        this.organizationIssueCreateService = organizationIssueCreateService;
    }

    @GetMapping("/api/organization/{organizationId}/issue/{issueId}/to-existing-project/{projectId}")
    public ResponseEntity<ServiceResult<?>> addToProject(@PathVariable Long organizationId,
        @PathVariable Long issueId, @PathVariable Long projectId)
    {
        addToProject(issueId, projectId);
        return ResponseEntity.ok(ServiceResult.ok());
    }

    @PostMapping("/api/organization/{organizationId}/issue/{issueId}/to-new-project")
    public ResponseEntity<ServiceResult<?>> addToProject(@PathVariable Long organizationId,
        @PathVariable Long issueId, @RequestBody ProjectCreateRest projectRest)
    {
        final ProjectCreateResult result = projectService.create(organizationId, projectRest);
        if(!result.isSuccess())
        {
            return ResponseEntity.ok(result);
        }
        addToProject(issueId, result.getProjectId());
        return ResponseEntity.ok(ServiceResult.ok());
    }

    //TODO: Zastanowić się nad URLem, bo przedrostek api/organization/{organizationId} jest uzywany do sprawdzania uprawnien zalogowanego uzytkownika
    @GetMapping("/api/organization/{organizationId}/issue-template")
    public List<IssueFieldEditRest> getTemplate(@PathVariable Long organizationId)
    {
        final List<IssueFieldDefinition> definitions = ifdRepository.getListByOrganizationId(
            Collections.singleton(organizationId));
        return getFields(organizationId, definitions, Collections.emptySet());
    }

    @GetMapping("/api/organization/{organizationId}/client-issue/{issueId}")
    public IssueFieldsRest getClientIssueDetails(@PathVariable Long organizationId, @PathVariable Long issueId)
    {
        final Issue issue = issueRepository.getReferenceById(issueId);
        final String organizationName = issue.getSourceOrganization().getName();
        final List<IssueFieldDefinition> definitions = issue.getFields().stream()
            .map(IssueField::getDefinition)
            .collect(Collectors.toList());
        final List<IssueFieldEditRest> fields =
            getFields(organizationId, definitions, issue.getFields());
        return getIssueFieldsRest(issue.getId(), organizationName, fields);
    }

    @PostMapping("/api/organization/{sourceOrganizationId}/issue-send-report")
    public ResponseEntity<ServiceResult<?>> sendReport(@PathVariable Long sourceOrganizationId,
        @RequestBody List<IssueFieldEditRest> fields)
    {
        final LocalDateTime created = LocalDateTime.now();
        return ResponseEntity.ok(organizationIssueCreateService.create(sourceOrganizationId, fields, created));
    }

    private void addToProject(final Long issueId, Long projectId)
    {
        final Issue issue = issueRepository.getReferenceById(issueId);
        final Long organizationId = issue.getSourceOrganization().getId();
        final OrganizationInProjectId oipId =
            new OrganizationInProjectId(organizationId, projectId);
        oipService.create(oipId, OrganizationInProjectRole.REPORTER, OrganizationInvitationStatus.ACCEPTED);
        issue.setProject(projectRepository.getReferenceById(projectId));
        issueRepository.save(issue);
    }

    private List<IssueFieldEditRest> getFields(final Long organizationId,
        final List<IssueFieldDefinition> definitions, final Set<IssueField> issueFields)
    {
        Map<IssueFieldDefinitionId, IssueField> issueFieldMap = Maps.uniqueIndex(issueFields, field -> field.getDefinition().getId());
        return definitions.stream()
            .sorted(Comparator.comparing(field -> field.getId().getCol()))
            .map(definition ->
            {
                final IssueField fieldOrNull = issueFieldMap.get(definition.getId());
                final Short row = definition.getId().getRow();
                final Byte col = definition.getId().getCol();
                return new IssueFieldEditRest(
                    organizationId,
                    getValue(definition, fieldOrNull),
                    row,
                    organizationId + "-" + row + "-" + col,
                    definition.getName(),
                    col,
                    definition.getType(),
                    definition.isRequired(),
                    definition.isClientVisible());
            })
            .collect(Collectors.toList());
    }

    private static Object getValue(final IssueFieldDefinition definition,
        final IssueField fieldOrNull)
    {
        if(definition.isRequired() && definition.getType() == IssueFieldType.FLAG && fieldOrNull == null)
        {
            return false;
        }
        if(fieldOrNull == null)
        {
            return null;
        }
        return switch(definition.getType())
            {
                case TEXT -> fieldOrNull.getTextValue();
                case DATE -> ObjectUtils.accessNullable(fieldOrNull.getDateValue(), date -> date.format(DTF_VAL));
                case NUMBER -> Objects.toString(fieldOrNull.getNumberValue());
                case FLAG -> fieldOrNull.isFlagValue();
            };
    }

    private static IssueFieldsRest getIssueFieldsRest(Long issueId,
        final String organizationName, final List<IssueFieldEditRest> fields)
    {
        final List<IssueFieldEditRest> col1Fields = fields.stream()
            .filter(field -> field.getColumn() == (byte) 1)
            .collect(Collectors.toList());
        final List<IssueFieldEditRest> col2Fields = fields.stream()
            .filter(field -> field.getColumn() == (byte) 2)
            .collect(Collectors.toList());
        return new IssueFieldsRest(issueId, organizationName, col1Fields, col2Fields);
    }

    public static class IssueFieldsRest
    {
        private final Long id;
        private final String organizationName;
        private final List<IssueFieldEditRest> col1Fields;
        private final List<IssueFieldEditRest> col2Fields;

        private IssueFieldsRest(final Long id,
            final String organizationName,
            final List<IssueFieldEditRest> col1Fields,
            final List<IssueFieldEditRest> col2Fields)
        {
            this.id = id;
            this.organizationName = organizationName;
            this.col1Fields = col1Fields;
            this.col2Fields = col2Fields;
        }

        public Long getId()
        {
            return id;
        }

        public String getOrganizationName()
        {
            return organizationName;
        }

        public List<IssueFieldEditRest> getCol1Fields()
        {
            return col1Fields;
        }

        public List<IssueFieldEditRest> getCol2Fields()
        {
            return col2Fields;
        }

    }

}
