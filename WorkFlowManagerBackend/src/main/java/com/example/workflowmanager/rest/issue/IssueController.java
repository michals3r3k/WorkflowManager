package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.db.issue.IssueFieldDefinitionRepository;
import com.example.workflowmanager.db.issue.IssueFieldRepository;
import com.example.workflowmanager.db.issue.IssueRepository;
import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.db.organization.project.ProjectRepository;
import com.example.workflowmanager.entity.issue.*;
import com.example.workflowmanager.entity.organization.OrganizationInProjectId;
import com.example.workflowmanager.entity.organization.OrganizationInProjectRole;
import com.example.workflowmanager.entity.organization.OrganizationInvitationStatus;
import com.example.workflowmanager.service.organization.OrganizationInProjectService;
import com.example.workflowmanager.service.project.ProjectCreateRest;
import com.example.workflowmanager.service.project.ProjectCreateService;
import com.example.workflowmanager.service.project.ProjectCreateService.ProjectCreateResult;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class IssueController
{
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DTF_VAL = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final OrganizationRepository organizationRepository;
    private final IssueRepository issueRepository;
    private final IssueFieldRepository fieldRepository;
    private final IssueFieldDefinitionRepository ifdRepository;
    private final ProjectRepository projectRepository;
    private final ProjectCreateService projectService;
    private final OrganizationInProjectService oipService;

    public IssueController(OrganizationRepository organizationRepository,
        final IssueRepository issueRepository,
        final IssueFieldRepository fieldRepository,
        final IssueFieldDefinitionRepository ifdRepository,
        final ProjectRepository projectRepository,
        final ProjectCreateService projectService,
        final OrganizationInProjectService oipService)
    {
        this.organizationRepository = organizationRepository;
        this.issueRepository = issueRepository;
        this.fieldRepository = fieldRepository;
        this.ifdRepository = ifdRepository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.oipService = oipService;
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

    //TODO: Zastanowić się nad URLem, bo przedrostek api/organization/{organizationId} jest uzywany do sprawdzania uprawnien zalogowanego uzytkownika
    @GetMapping("/api/organization/{organizationId}/issue-template")
    public List<IssueFieldEditRest> getTemplate(@PathVariable Long organizationId)
    {
        final List<IssueFieldDefinition> definitions = ifdRepository.getListByOrganizationId(
            Collections.singleton(organizationId));
        return getFields(organizationId, definitions, Collections.emptySet());
    }

    @GetMapping("/api/organization/{organizationId}/issue-names")
    public List<IssueNameRest> getList(@PathVariable Long organizationId)
    {
        return issueRepository.getAllOrganizationIssues(Collections.singleton(organizationId)).stream()
            .sorted(Comparator.comparing(Issue::getId))
            .map(issue -> new IssueNameRest(issue,
                issue.getSourceOrganization().getId().equals(organizationId)))
            .collect(Collectors.toList());
    }

    @GetMapping("/api/organization/{organizationId}/client-issue/{issueId}")
    public IssueDetailsRest getClientIssueDetails(@PathVariable Long organizationId, @PathVariable Long issueId)
    {
        final Issue issue = issueRepository.getReferenceById(issueId);
        final String organizationName = issue.getSourceOrganization().getName();
        final List<IssueFieldDefinition> definitions = issue.getFields().stream()
            .map(IssueField::getDefinition)
            .collect(Collectors.toList());
        final List<IssueFieldEditRest> fields =
            getFields(organizationId, definitions, issue.getFields());
        return getIssueDetailsRest(issue.getId(), organizationName, fields);
    }

    @PostMapping("/api/organization/{sourceOrganizationId}/issue-send-report")
    public ResponseEntity<ServiceResult<?>> sendReport(@PathVariable Long sourceOrganizationId,
        @RequestBody List<IssueFieldEditRest> fields)
    {
        if(fields.isEmpty())
        {
            return ResponseEntity.ok(ServiceResult.ok());
        }
        final Set<Long> destinationOrganizationIds = fields.stream()
            .map(IssueFieldEditRest::getOrganizationId)
            .collect(Collectors.toSet());
        if(destinationOrganizationIds.size() != 1)
        {
            // TODO: ERROR
            return ResponseEntity.ok(ServiceResult.ok());
        }
        final Long destinationOrganizationId =
            Iterables.getOnlyElement(destinationOrganizationIds);
        final Issue issue = new Issue();
        issue.setSourceOrganization(organizationRepository.getReferenceById(sourceOrganizationId));
        issue.setOrganization(organizationRepository.getReferenceById(destinationOrganizationId));
        issueRepository.save(issue);
        final Map<IssueFieldDefinitionId, IssueFieldDefinition> fieldDefinitionMap = Maps.uniqueIndex(
            ifdRepository.getListByOrganizationId(Collections.singleton(destinationOrganizationId)),
            IssueFieldDefinition::getId);
        for(IssueFieldEditRest field : fields)
        {
            final IssueFieldDefinitionId definitionId = new IssueFieldDefinitionId(
                field.getOrganizationId(), field.getRow(), field.getColumn());
            final IssueFieldDefinition definition = Preconditions.checkNotNull(
                fieldDefinitionMap.get(definitionId));
            final IssueField issueField = new IssueField(new IssueFieldId(issue.getId(), definitionId));
            switch(definition.getType())
            {
                case TEXT -> issueField.setTextValue(field.getValue());
                case DATE -> issueField.setDateValue(accessNullable(
                    field.getValue(), value -> LocalDateTime.parse(field.getValue(), DTF)));
                case NUMBER -> issueField.setNumberValue(accessNullable(
                    field.getValue(), BigDecimal::new));
                case FLAG -> issueField.setFlagValue(accessNullable(
                    field.getValue(), Boolean::valueOf));
            }
            fieldRepository.save(issueField);
        }
        return ResponseEntity.ok(ServiceResult.ok());
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
                return new IssueFieldEditRest(
                    organizationId,
                    getValue(definition, fieldOrNull),
                    definition.getId().getRow(),
                    definition.getName(),
                    definition.getId().getCol(),
                    definition.getType(),
                    definition.isRequired(),
                    definition.isClientVisible());
            })
            .collect(Collectors.toList());
    }

    private static String getValue(final IssueFieldDefinition definition,
        final IssueField fieldOrNull)
    {
        if(fieldOrNull == null)
        {
            return null;
        }
        return switch(definition.getType())
            {
                case TEXT -> fieldOrNull.getTextValue();
                case DATE -> fieldOrNull.getDateValue().format(DTF_VAL);
                case NUMBER -> fieldOrNull.getNumberValue().toString();
                case FLAG -> Boolean.toString(fieldOrNull.isFlagValue());
            };
    }

    private static IssueDetailsRest getIssueDetailsRest(Long issueId,
        final String organizationName, final List<IssueFieldEditRest> fields)
    {
        final List<IssueFieldEditRest> col1Fields = fields.stream()
            .filter(field -> field.getColumn() == (byte) 1)
            .collect(Collectors.toList());
        final List<IssueFieldEditRest> col2Fields = fields.stream()
            .filter(field -> field.getColumn() == (byte) 2)
            .collect(Collectors.toList());
        return new IssueDetailsRest(issueId, organizationName, col1Fields, col2Fields);
    }

    private static <T> T accessNullable(String value, Function<String, T> mapper)
    {
        if(value == null)
        {
            return null;
        }
        return mapper.apply(value);
    }

    public static class IssueNameRest
    {
        private final Long id;
        private final String organizationName;
        private final boolean myIssue;

        private IssueNameRest(Issue issue, boolean myIssue) {
            this.id = issue.getId();
            this.organizationName = (myIssue ? issue.getOrganization() : issue.getSourceOrganization()).getName();
            this.myIssue = myIssue;
        }

        public Long getId()
        {
            return id;
        }

        public String getOrganizationName()
        {
            return organizationName;
        }

        public boolean isMyIssue()
        {
            return myIssue;
        }

    }

    public static class IssueDetailsRest
    {
        private final Long id;
        private final String organizationName;
        private final List<IssueFieldEditRest> col1Fields;
        private final List<IssueFieldEditRest> col2Fields;

        private IssueDetailsRest(final Long id,
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
