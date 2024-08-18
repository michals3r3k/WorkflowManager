package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.db.issue.IssueFieldDefinitionRepository;
import com.example.workflowmanager.entity.issue.*;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class IssueFormFactory
{
    private static final DateTimeFormatter DTF_VAL = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final IssueFieldDefinitionRepository ifdRepository;

    public IssueFormFactory(final IssueFieldDefinitionRepository ifdRepository)
    {
        this.ifdRepository = ifdRepository;
    }

    IssueFormRest getEmpty(final Long organizationId)
    {
        return getForm(organizationId, null);
    }

    IssueFormRest getForm(final Long organizationId, final Issue issueOrNull)
    {
        final List<IssueFieldDefinition> definitions = ifdRepository.getListByOrganizationId(
            Collections.singleton(organizationId));
        final List<IssueFormRest.IssueFieldEditRest> fields = getFields(
            definitions, issueOrNull);
        return new IssueFormRest(ObjectUtils.accessNullable(issueOrNull, Issue::getTitle), fields);
    }

    private List<IssueFormRest.IssueFieldEditRest> getFields(
        final List<IssueFieldDefinition> definitions, final Issue issueOrNull)
    {
        final Map<IssueFieldDefinitionId, IssueField> issueFieldMap = Maps.uniqueIndex(
            getFields(issueOrNull), field -> field.getDefinition().getId());
        return definitions.stream()
            .sorted(Comparator.comparing(field -> field.getId().getCol()))
            .map(definition ->
            {
                final IssueField fieldOrNull = issueFieldMap.get(
                    definition.getId());
                final Long organizationId = definition.getId().getOrganizationId();
                final Short row = definition.getId().getRow();
                final Byte col = definition.getId().getCol();
                return new IssueFormRest.IssueFieldEditRest(
                    definition.getId().getOrganizationId(),
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

    private Set<IssueField> getFields(Issue issueOrNull)
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
}
