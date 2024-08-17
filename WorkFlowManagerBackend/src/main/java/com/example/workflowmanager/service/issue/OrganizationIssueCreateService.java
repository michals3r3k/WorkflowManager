package com.example.workflowmanager.service.issue;

import com.example.workflowmanager.db.issue.IssueFieldDefinitionRepository;
import com.example.workflowmanager.db.issue.IssueFieldRepository;
import com.example.workflowmanager.db.issue.IssueRepository;
import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.entity.issue.*;
import com.example.workflowmanager.rest.issue.IssueFieldEditRest;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrganizationIssueCreateService
{
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final OrganizationRepository organizationRepository;
    private final IssueRepository issueRepository;
    private final IssueFieldDefinitionRepository ifdRepository;
    private final IssueFieldRepository fieldRepository;

    public OrganizationIssueCreateService(final OrganizationRepository organizationRepository,
        final IssueRepository issueRepository,
        final IssueFieldDefinitionRepository ifdRepository,
        final IssueFieldRepository fieldRepository)
    {
        this.organizationRepository = organizationRepository;
        this.issueRepository = issueRepository;
        this.ifdRepository = ifdRepository;
        this.fieldRepository = fieldRepository;
    }

    public ServiceResult<IssueCreateError> create(Long sourceOrganizationId,
        List<IssueFieldEditRest> fields)
    {
        if(fields.isEmpty())
        {
            return ServiceResult.error(IssueCreateError.FIELDS_EMPTY);
        }
        boolean requiredFieldEmpty = fields.stream()
            .anyMatch(field -> field.isRequired() && field.getValue() == null);
        if(requiredFieldEmpty)
        {
            return ServiceResult.error(IssueCreateError.REQUIRED_FIELD_EMPTY);
        }
        final Set<Long> destinationOrganizationIds = fields.stream()
            .map(IssueFieldEditRest::getOrganizationId)
            .collect(Collectors.toSet());
        if(destinationOrganizationIds.size() != 1)
        {
            return ServiceResult.error(IssueCreateError.MULTIPLE_DESTINATIONS);
        }
        final Long destinationOrganizationId =
            Iterables.getOnlyElement(destinationOrganizationIds);
        final Issue issue = new Issue();
        issue.setSourceOrganization(organizationRepository.getReferenceById(sourceOrganizationId));
        issue.setOrganization(organizationRepository.getReferenceById(destinationOrganizationId));
        issueRepository.save(issue);
        saveFields(fields, destinationOrganizationId, issue);
        return ServiceResult.ok();
    }

    private void saveFields(final List<IssueFieldEditRest> fields,
        final Long destinationOrganizationId, final Issue issue)
    {
        final Map<IssueFieldDefinitionId, IssueFieldDefinition> fieldDefinitionMap =
            getFieldDefinitionMap(destinationOrganizationId);
        for(final IssueFieldEditRest field : fields)
        {
            final IssueFieldDefinitionId definitionId = new IssueFieldDefinitionId(
                field.getOrganizationId(), field.getRow(), field.getColumn());
            final IssueFieldDefinition definition = Preconditions.checkNotNull(
                fieldDefinitionMap.get(definitionId));
            final IssueField issueField = new IssueField(new IssueFieldId(issue.getId(), definitionId));
            setValue(field, definition, issueField);
            fieldRepository.save(issueField);
        }
    }

    private Map<IssueFieldDefinitionId, IssueFieldDefinition> getFieldDefinitionMap(
        final Long destinationOrganizationId)
    {
        final List<IssueFieldDefinition> definitions = ifdRepository
            .getListByOrganizationId(Collections.singleton(destinationOrganizationId));
        return Maps.uniqueIndex(definitions, IssueFieldDefinition::getId);
    }

    private static void setValue(final IssueFieldEditRest field,
        final IssueFieldDefinition definition, final IssueField issueField)
    {
        switch(definition.getType())
        {
            case TEXT -> issueField.setTextValue(Objects.toString(field.getValue()));
            case DATE -> issueField.setDateValue(ObjectUtils.accessNullable(
                field.getValue(), value -> LocalDateTime.parse(Objects.toString(field.getValue()), DTF)));
            case NUMBER -> issueField.setNumberValue(ObjectUtils.accessNullable(
                Objects.toString(field.getValue()), BigDecimal::new));
            case FLAG -> issueField.setFlagValue(ObjectUtils.accessNullable(
                Objects.toString(field.getValue()), Boolean::valueOf));
        }
    }

    public enum IssueCreateError
    {
        FIELDS_EMPTY,
        MULTIPLE_DESTINATIONS,
        REQUIRED_FIELD_EMPTY

    }

}
