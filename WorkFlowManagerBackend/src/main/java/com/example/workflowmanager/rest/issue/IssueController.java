package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.db.issue.IssueFieldDefinitionRepository;
import com.example.workflowmanager.db.issue.IssueFieldRepository;
import com.example.workflowmanager.db.issue.IssueRepository;
import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.entity.issue.*;
import com.example.workflowmanager.rest.issue.IssueDefinitionController.IssueFieldDefinitionRest;
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

    private final OrganizationRepository organizationRepository;
    private final IssueRepository issueRepository;
    private final IssueFieldRepository fieldRepository;
    private final IssueFieldDefinitionRepository ifdRepository;

    public IssueController(OrganizationRepository organizationRepository,
        final IssueRepository issueRepository,
        final IssueFieldRepository fieldRepository,
        final IssueFieldDefinitionRepository ifdRepository)
    {
        this.organizationRepository = organizationRepository;
        this.issueRepository = issueRepository;
        this.fieldRepository = fieldRepository;
        this.ifdRepository = ifdRepository;
    }

    //TODO: Zastanowić się nad URLem, bo przedrostek api/organization/{organizationId} jest uzywany do sprawdzania uprawnien zalogowanego uzytkownika
    @GetMapping("/api/organization/{organizationId}/issue-template")
    public List<IssueFieldEditRest> getTemplate(@PathVariable Long organizationId)
    {
        return ifdRepository.getListByOrganizationId(
                Collections.singleton(organizationId)).stream()
            .sorted(Comparator.comparing(field -> field.getId().getCol()))
            .map(field -> new IssueFieldEditRest(organizationId, null,
                field.getId().getRow(),
                field.getName(), field.getId().getCol(), field.getType(),
                field.isRequired(), field.isClientVisible()))
            .collect(Collectors.toList());
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

    private static <T> T accessNullable(String value, Function<String, T> mapper)
    {
        if(value == null)
        {
            return null;
        }
        return mapper.apply(value);
    }

    public static class IssueFieldEditRest extends IssueFieldDefinitionRest
    {
        private Long organizationId;
        private String value;
        private Short row;

        private IssueFieldEditRest(Long organizationId, String value, Short row,
            final String name, final Byte column, final IssueFieldType type,
            final boolean required, final boolean clientVisible)
        {
            super(name, column, type, required, clientVisible);
            this.organizationId = organizationId;
            this.value = value;
            this.row = row;
        }

        public IssueFieldEditRest()
        {
            // for Spring
        }

        public Long getOrganizationId()
        {
            return organizationId;
        }

        public void setOrganizationId(final Long organizationId)
        {
            this.organizationId = organizationId;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue(final String value)
        {
            this.value = value;
        }

        public Short getRow()
        {
            return row;
        }

        public void setRow(final Short row)
        {
            this.row = row;
        }

    }

}
