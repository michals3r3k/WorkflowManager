package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.db.issue.IssueCategoryRepository;
import com.example.workflowmanager.db.issue.IssueFieldDefinitionRepository;
import com.example.workflowmanager.db.issue.IssueStatusRepository;
import com.example.workflowmanager.entity.issue.*;
import com.example.workflowmanager.rest.issue.IssueFormRest.IssueFieldEditRest;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class IssueFormFactory
{
    private static final DateTimeFormatter DTF_VAL = DateTimeFormatter.ISO_DATE_TIME;

    private final IssueFieldDefinitionRepository ifdRepository;
    private final IssueStatusRepository isRepository;
    private final IssueCategoryRepository icRepository;

    public IssueFormFactory(final IssueFieldDefinitionRepository ifdRepository,
        final IssueStatusRepository isRepository,
        final IssueCategoryRepository icRepository)
    {
        this.ifdRepository = ifdRepository;
        this.isRepository = isRepository;
        this.icRepository = icRepository;
    }

    IssueFormRest getEmptyForClient(final Long organizationId)
    {
        return getForm(organizationId, null, true);
    }

    IssueFormRest getForm(final Long organizationId, final Issue issueOrNull, final boolean forClient)
    {
        final List<IssueFieldDefinition> definitions = ifdRepository.getListByOrganizationId(
            Collections.singleton(organizationId));
        final List<String> statusOptions = getStatusOptions(organizationId);
        final List<String> categoryOptions = getCategoryOptions(organizationId);
        final List<IssueFieldEditRest> fields = getFields(definitions, issueOrNull, forClient);
        final Long issueId = ObjectUtils.accessNullable(issueOrNull, Issue::getId);
        final String title = getStringOrEmpty(issueOrNull, Issue::getTitle);
        final String description = getStringOrEmpty(issueOrNull, Issue::getDescription);
        final String status = getStringOrEmpty(issueOrNull, Issue::getStatus);
        final String category = getStringOrEmpty(issueOrNull, Issue::getCategory);
        return new IssueFormRest(issueId, title, description, status, category, fields, statusOptions, categoryOptions);
    }

    private List<String> getStatusOptions(final Long organizationId)
    {
        return isRepository.getListByOrganizationIds(
                Collections.singleton(organizationId)).stream()
            .map(IssueStatus::getId)
            .map(IssueStatusId::getStatus)
            .collect(Collectors.toList());
    }

    private List<String> getCategoryOptions(final Long organizationId)
    {
        return icRepository.getListByOrganizationIds(
                Collections.singleton(organizationId)).stream()
            .map(IssueCategory::getId)
            .map(IssueCategoryId::getCategory)
            .collect(Collectors.toList());
    }

    private List<IssueFieldEditRest> getFields(final List<IssueFieldDefinition> definitions,
        final Issue issueOrNull, final boolean forClient)
    {
        final Map<Long, IssueField> issueFieldMap = Maps.uniqueIndex(
            getFields(issueOrNull), field -> field.getDefinition().getId());
        return definitions.stream()
            .filter(field -> !forClient || field.isClientVisible())
            .sorted(Comparator.comparing(IssueFieldDefinition::getCol))
            .map(definition ->
            {
                final IssueField fieldOrNull = issueFieldMap.get(definition.getId());
                final Long organizationId = definition.getOrganizationId();
                final Short row = definition.getRow();
                final Byte col = definition.getCol();
                return new IssueFieldEditRest(
                    definition.getId(),
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

    private Set<IssueField> getFields(final Issue issueOrNull)
    {
        if(issueOrNull == null)
        {
            return Collections.emptySet();
        }
        return issueOrNull.getFields();
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
            return "";
        }
        return switch(definition.getType())
            {
                case TEXT -> getStringOrEmpty(fieldOrNull.getTextValue(), Function.identity());
                case DATE -> getStringOrEmpty(fieldOrNull.getDateValue(), date -> date.format(DTF_VAL));
                case NUMBER -> getStringOrEmpty(fieldOrNull.getNumberValue(), BigDecimal::toString);
                case FLAG -> fieldOrNull.isFlagValue();
            };
    }

    private static <T> String getStringOrEmpty(T obj, Function<T, String> mapper)
    {
        if(obj == null)
        {
            return "";
        }
        final String value = mapper.apply(obj);
        if(value == null)
        {
            return "";
        }
        return value;
    }

}
