package com.example.workflowmanager.service.issue;

import com.example.workflowmanager.db.issue.IssueCategoryRepository;
import com.example.workflowmanager.db.issue.IssueFieldDefinitionRepository;
import com.example.workflowmanager.db.issue.IssueRepository;
import com.example.workflowmanager.db.issue.IssueStatusRepository;
import com.example.workflowmanager.entity.issue.*;
import com.example.workflowmanager.rest.issue.IssueDefinitionRest;
import com.example.workflowmanager.rest.issue.IssueFieldDefinitionRest;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.base.Predicates;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class IssueDefinitionService
{
    private final IssueFieldDefinitionRepository ifdRepository;
    private final IssueStatusRepository isRepository;
    private final IssueCategoryRepository icRepository;
    private final IssueRepository issueRepository;

    public IssueDefinitionService(final IssueFieldDefinitionRepository ifdRepository,
        final IssueStatusRepository isRepository,
        final IssueCategoryRepository icRepository,
        final IssueRepository issueRepository)
    {
        this.ifdRepository = ifdRepository;
        this.isRepository = isRepository;
        this.icRepository = icRepository;
        this.issueRepository = issueRepository;
    }

    public ServiceResult<IssueDefinitionError> save(final Long organizationId,
        final IssueDefinitionRest issueDefinition)
    {
        final Set<IssueDefinitionError> errors = EnumSet.noneOf(IssueDefinitionError.class);
        if(!issueRepository.getAllByOrganizationIds(Collections.singleton(organizationId)).isEmpty())
        {
            errors.add(IssueDefinitionError.EXISTS_ISSUES);
            return new ServiceResult<>(errors);
        }
        if(issueDefinition.getCategories().isEmpty())
        {
            errors.add(IssueDefinitionError.EMPTY_CATEGORIES);
        }
        if(issueDefinition.getFields().isEmpty())
        {
            errors.add(IssueDefinitionError.EMPTY_FIELD_DEFINITIONS);
        }
        if(issueDefinition.getStatuses().isEmpty())
        {
            errors.add(IssueDefinitionError.EMPTY_STATUSES);
        }
        if(!errors.isEmpty())
        {
            return new ServiceResult<>(errors);
        }
        final Set<IssueStatus> statusesToSave = getStatusesToSave(organizationId, issueDefinition);
        final Set<IssueCategory> categoriesToSave = getCategoriesToSave(organizationId, issueDefinition);
        final List<IssueFieldDefinition> fieldsToSave = getFieldsToSave(organizationId, issueDefinition);
        final Set<IssueStatus> statusesToDelete = getStatusesToDelete(organizationId, statusesToSave);
        final List<IssueCategory> categoriesToDelete = getCategoriesToDelete(organizationId);
        final List<IssueFieldDefinition> fieldsToDelete = getFieldsToDelete(organizationId);
        isRepository.deleteAll(statusesToDelete);
        icRepository.deleteAll(categoriesToDelete);
        ifdRepository.deleteAll(fieldsToDelete);
        isRepository.saveAll(statusesToSave);
        icRepository.saveAll(categoriesToSave);
        ifdRepository.saveAll(fieldsToSave);
        return ServiceResult.ok();
    }

    private static Set<IssueStatus> getStatusesToSave(final Long organizationId,
        final IssueDefinitionRest issueDefinition)
    {
        return Stream.concat(IssueStatus.CONST_STATUSES.stream(),
            issueDefinition.getStatuses().stream())
            .map(status -> new IssueStatusId(organizationId, status))
            .map(IssueStatus::new)
            .collect(Collectors.toSet());
    }

    private Set<IssueStatus> getStatusesToDelete(final Long organizationId,
        final Set<IssueStatus> statusesToSave)
    {
        return isRepository.getListByOrganizationIds(
            Collections.singleton(organizationId)).stream()
            .filter(Predicates.not(statusesToSave::contains))
            .collect(Collectors.toSet());
    }

    public static Set<IssueCategory> getCategoriesToSave(final Long organizationId,
        final IssueDefinitionRest issueDefinition)
    {
        return issueDefinition.getCategories().stream()
            .map(category -> new IssueCategoryId(organizationId, category))
            .map(IssueCategory::new)
            .collect(Collectors.toSet());
    }

    private List<IssueCategory> getCategoriesToDelete(final Long organizationId)
    {
        return icRepository.getListByOrganizationIds(Collections.singleton(organizationId));
    }

    private static List<IssueFieldDefinition> getFieldsToSave(
        final Long organizationId, final IssueDefinitionRest issueDefinition)
    {
        return issueDefinition.getFields().stream()
            .collect(Collectors.groupingBy(IssueFieldDefinitionRest::getColumn))
            .values().stream()
            .flatMap(colFields -> IntStream.range(0, colFields.size())
                .mapToObj(i -> getIssueFieldDefinition(organizationId, colFields.get(i), i)))
            .collect(Collectors.toList());
    }

    private List<IssueFieldDefinition> getFieldsToDelete(
        final Long organizationId)
    {
        return ifdRepository.getListByOrganizationId(
            Collections.singleton(organizationId));
    }

    private static IssueFieldDefinition getIssueFieldDefinition(final Long organizationId,
        final IssueFieldDefinitionRest field, final int row)
    {
        final IssueFieldDefinitionId id = new IssueFieldDefinitionId(
            organizationId, (short) row, field.getColumn());
        return new IssueFieldDefinition(id, field.getName(), field.getType(),
            field.isRequired(), field.isClientVisible());
    }

    public enum IssueDefinitionError
    {
        EXISTS_ISSUES,
        EMPTY_FIELD_DEFINITIONS,
        EMPTY_CATEGORIES,
        EMPTY_STATUSES,
    }

}
