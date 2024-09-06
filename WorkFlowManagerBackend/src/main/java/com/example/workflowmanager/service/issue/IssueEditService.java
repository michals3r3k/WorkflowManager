package com.example.workflowmanager.service.issue;

import com.example.workflowmanager.db.issue.*;
import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.entity.issue.*;
import com.example.workflowmanager.rest.issue.IssueFormRest;
import com.example.workflowmanager.rest.issue.IssueFormRest.IssueFieldEditRest;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueEditService
{
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_DATE_TIME;

    private final OrganizationRepository organizationRepository;
    private final IssueRepository issueRepository;
    private final IssueFieldDefinitionRepository ifdRepository;
    private final IssueFieldRepository fieldRepository;
    private final IssueStatusRepository issueStatusRepository;
    private final IssueCategoryRepository issueCategoryRepository;

    public IssueEditService(final OrganizationRepository organizationRepository,
        final IssueRepository issueRepository,
        final IssueFieldDefinitionRepository ifdRepository,
        final IssueFieldRepository fieldRepository,
        final IssueStatusRepository issueStatusRepository,
        final IssueCategoryRepository issueCategoryRepository)
    {
        this.organizationRepository = organizationRepository;
        this.issueRepository = issueRepository;
        this.ifdRepository = ifdRepository;
        this.fieldRepository = fieldRepository;
        this.issueStatusRepository = issueStatusRepository;
        this.issueCategoryRepository = issueCategoryRepository;
    }

    public ServiceResult<IssueEditError> edit(final IssueFormRest form)
    {
        Preconditions.checkNotNull(form.getIssueId());
        final Issue issue = issueRepository.getById(form.getIssueId());
        if(issue == null)
        {
            return ServiceResult.error(IssueEditError.ISSUE_NOT_EXISTS);
        }
        return edit(issue, form);
    }

    public ServiceResult<IssueEditError> create(final Long sourceOrganizationId, final IssueFormRest form)
    {
        final Issue issue = new Issue();
        issue.setCreated(LocalDateTime.now());
        issue.setSourceOrganization(organizationRepository.getReferenceById(sourceOrganizationId));
        return edit(issue, form);
    }

    private ServiceResult<IssueEditError> edit(final Issue issue, final IssueFormRest form)
    {
        final List<IssueFieldEditRest> fields = form.getFields();
        final Set<IssueEditError> errors = EnumSet.noneOf(IssueEditError.class);
        if(fields.isEmpty())
        {
            errors.add(IssueEditError.FIELDS_EMPTY);
            return new ServiceResult<>(errors);
        }
        if(StringUtils.isBlank(form.getStatus()))
        {
            errors.add(IssueEditError.STATUS_EMPTY);
        }
        if(StringUtils.isBlank(form.getCategory()))
        {
            errors.add(IssueEditError.CATEGORY_EMPTY);
        }
        boolean requiredFieldEmpty = fields.stream()
            .anyMatch(field -> field.isRequired() && StringUtils.isBlank(Objects.toString(field.getValue())));
        if(requiredFieldEmpty)
        {
            errors.add(IssueEditError.REQUIRED_FIELD_EMPTY);
        }
        final Set<Long> destinationOrganizationIds = fields.stream()
            .map(IssueFieldEditRest::getOrganizationId)
            .collect(Collectors.toSet());
        if(destinationOrganizationIds.size() != 1)
        {
            errors.add(IssueEditError.MULTIPLE_DESTINATIONS);
            return new ServiceResult<>(errors);
        }
        final Long destinationOrganizationId = Iterables.getOnlyElement(destinationOrganizationIds);
        if(isUnknownStatus(form, destinationOrganizationId))
        {
            errors.add(IssueEditError.UNKNOWN_STATUS);
        }
        if(isUnknownCategory(form, destinationOrganizationId))
        {
            errors.add(IssueEditError.UNKNOWN_CATEGORY);
        }
        if(errors.isEmpty())
        {
            issue.setOrganizationId(destinationOrganizationId);
            issue.setTitle(form.getTitle());
            issue.setDescription(form.getDescription());
            issue.setStatus(form.getStatus());
            issue.setCategory(form.getCategory());
            issueRepository.save(issue);
            saveFields(issue, form.getFields());
        }
        return new ServiceResult<>(errors);
    }

    private boolean isUnknownStatus(final IssueFormRest form,
        final Long destinationOrganizationId)
    {
        return issueStatusRepository.getListByOrganizationIds(
            Collections.singleton(destinationOrganizationId)).stream()
            .map(IssueStatus::getId)
            .map(IssueStatusId::getStatus)
            .noneMatch(form.getStatus()::equals);
    }

    private boolean isUnknownCategory(final IssueFormRest form,
        final Long destinationOrganizationId)
    {
        return issueCategoryRepository.getListByOrganizationIds(
            Collections.singleton(destinationOrganizationId)).stream()
            .map(IssueCategory::getId)
            .map(IssueCategoryId::getCategory)
            .noneMatch(form.getCategory()::equals);
    }

    private void saveFields(final Issue issue, final List<IssueFieldEditRest> fields)
    {
        final Map<IssueFieldDefinitionId, IssueFieldDefinition> fieldDefinitionMap =
            getFieldDefinitionMap(issue.getOrganizationId());
        final Map<IssueFieldId, IssueField> issueFieldMap =
            getIssueFieldMap(issue.getId(), fieldDefinitionMap.keySet());
        for(final IssueFieldEditRest field : fields)
        {
            final IssueFieldDefinitionId definitionId = new IssueFieldDefinitionId(
                field.getOrganizationId(), field.getRow(), field.getColumn());
            final IssueFieldDefinition definition = Preconditions.checkNotNull(
                fieldDefinitionMap.get(definitionId));
            final IssueFieldId issueFieldId = new IssueFieldId(issue.getId(), definitionId);
            final IssueField issueField = Optional
                .ofNullable(issueFieldMap.get(issueFieldId))
                .orElseGet(() -> new IssueField(issueFieldId));
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

    private Map<IssueFieldId, IssueField> getIssueFieldMap(
        final Long issueId, final Set<IssueFieldDefinitionId> definitionIds)
    {
        final Set<IssueFieldId> issueFieldIds = definitionIds.stream()
            .map(definitionId -> new IssueFieldId(issueId, definitionId))
            .collect(Collectors.toSet());
        return Maps.uniqueIndex(fieldRepository.getList(issueFieldIds), IssueField::getId);
    }

    private static void setValue(final IssueFieldEditRest field,
        final IssueFieldDefinition definition, final IssueField issueField)
    {
        String valueStrOrNull = ObjectUtils.accessNullable(field.getValue(), Objects::toString);
        final String value = StringUtils.isBlank(valueStrOrNull) ? null : valueStrOrNull;
        switch(definition.getType())
        {
            case TEXT -> issueField.setTextValue(value);
            case DATE -> issueField.setDateValue(ObjectUtils.accessNullable(
                value, dateStr -> LocalDateTime.parse(dateStr, DTF)));
            case NUMBER -> issueField.setNumberValue(ObjectUtils.accessNullable(
                value, BigDecimal::new));
            case FLAG -> issueField.setFlagValue(ObjectUtils.accessNullable(
                value, Boolean::valueOf));
        }
    }

    public enum IssueEditError
    {
        ISSUE_NOT_EXISTS,
        FIELDS_EMPTY,
        MULTIPLE_DESTINATIONS,
        REQUIRED_FIELD_EMPTY,
        CATEGORY_EMPTY,
        STATUS_EMPTY,
        UNKNOWN_STATUS,
        UNKNOWN_CATEGORY

    }

}
