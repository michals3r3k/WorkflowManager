package com.example.workflowmanager.service.issue;

import com.example.workflowmanager.db.issue.*;
import com.example.workflowmanager.entity.issue.*;
import com.example.workflowmanager.rest.issue.IssueDefinitionRest;
import com.example.workflowmanager.rest.issue.IssueFieldDefinitionRest;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class IssueDefinitionService
{
    private final IssueFieldDefinitionRepository ifdRepository;
    private final IssueStatusRepository isRepository;
    private final IssueCategoryRepository icRepository;
    private final IssueRepository issueRepository;
    private final IssueFieldRepository issueFieldRepository;

    public IssueDefinitionService(final IssueFieldDefinitionRepository ifdRepository,
        final IssueStatusRepository isRepository,
        final IssueCategoryRepository icRepository,
        final IssueRepository issueRepository,
        final IssueFieldRepository issueFieldRepository)
    {
        this.ifdRepository = ifdRepository;
        this.isRepository = isRepository;
        this.icRepository = icRepository;
        this.issueRepository = issueRepository;
        this.issueFieldRepository = issueFieldRepository;
    }

    public ServiceResult<IssueDefinitionError> save(final Long organizationId,
        final IssueDefinitionRest issueDefinition)
    {
        final Set<IssueDefinitionError> errors = EnumSet.noneOf(IssueDefinitionError.class);
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
        final Set<IssueStatus> statusesExisting = ImmutableSet.copyOf(isRepository.getListByOrganizationIds(
            Collections.singleton(organizationId)));
        final Set<IssueStatus> statusesEdited = issueDefinition.getStatuses().stream()
            .map(status -> new IssueStatusId(organizationId, status))
            .map(IssueStatus::new)
            .collect(Collectors.toSet());
        if(isUsedStatusDelete(organizationId, statusesEdited))
        {
            errors.add(IssueDefinitionError.USED_STATUS_DELETE);
        }
        final Set<IssueCategory> categoriesExisting = ImmutableSet.copyOf(icRepository.getListByOrganizationIds(
            Collections.singleton(organizationId)));
        final Set<IssueCategory> categoriesEdited = issueDefinition.getCategories().stream()
            .map(status -> new IssueCategoryId(organizationId, status))
            .map(IssueCategory::new)
            .collect(Collectors.toSet());
        if(isUsedCategoryDelete(organizationId, categoriesEdited))
        {
            errors.add(IssueDefinitionError.USED_CATEGORY_DELETE);
        }
        final Map<Long, IssueFieldDefinition> existingFieldsMap = Maps.uniqueIndex(
            ifdRepository.getListByOrganizationId(Collections.singleton(organizationId)),
            IssueFieldDefinition::getId);
        final Set<Long> definitionIdsToEdit = issueDefinition.getFields().stream()
            .map(IssueFieldDefinitionRest::getDefinitionId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        if(!existingFieldsMap.keySet().containsAll(definitionIdsToEdit))
        {
            errors.add(IssueDefinitionError.OUT_OF_DATE);
        }
        if(errors.isEmpty())
        {
            updateStatuses(statusesExisting, statusesEdited);
            updateCategories(categoriesExisting, categoriesEdited);
            updateFieldDefinitions(organizationId, issueDefinition, existingFieldsMap, definitionIdsToEdit);
        }
        return new ServiceResult<>(errors);
    }

    private boolean isUsedStatusDelete(final Long organizationId,
        final Set<IssueStatus> statusesEdited)
    {
        final Set<IssueStatus> statusesUsed = issueRepository.getOrganizationIssues(
                Collections.singleton(organizationId)).stream()
            .map(Issue::getIssueStatus)
            .collect(Collectors.toSet());
        return !statusesEdited.containsAll(statusesUsed);
    }

    private boolean isUsedCategoryDelete(final Long organizationId,
        final Set<IssueCategory> categoriesEdited)
    {
        final Set<IssueCategory> categoriesUsed = issueRepository.getOrganizationIssues(
            Collections.singleton(organizationId)).stream()
            .map(Issue::getIssueCategory)
            .collect(Collectors.toSet());
        return !categoriesEdited.containsAll(categoriesUsed);
    }

    private void updateCategories(final Set<IssueCategory> categoriesExisting,
        final Set<IssueCategory> categoriesEdited)
    {
        final Set<IssueCategory> categoriesToDelete =
            Sets.difference(categoriesExisting, categoriesEdited);
        final Set<IssueCategory> categoriesToSave =
            Sets.difference(categoriesEdited, categoriesExisting);
        icRepository.deleteAll(categoriesToDelete);
        icRepository.saveAll(categoriesToSave);
    }

    private void updateStatuses(final Set<IssueStatus> statusesExisting,
        final Set<IssueStatus> statusesEdited)
    {
        final Set<IssueStatus> statusesToDelete =
            Sets.difference(statusesExisting, statusesEdited);
        final Set<IssueStatus> statusesToSave =
            Sets.difference(statusesEdited, statusesExisting);
        isRepository.deleteAll(statusesToDelete);
        isRepository.saveAll(statusesToSave);
    }

    private void updateFieldDefinitions(final Long organizationId,
        final IssueDefinitionRest issueDefinition,
        final Map<Long, IssueFieldDefinition> existingFieldsMap,
        final Set<Long> definitionIdsToEdit)
    {
        final Set<Long> definitionIdsToDelete =
            Sets.difference(existingFieldsMap.keySet(), definitionIdsToEdit);
        deleteIssueFields(issueDefinition, existingFieldsMap, definitionIdsToDelete);
        deleteIssueFieldDefinitions(definitionIdsToDelete);
        saveIssueFieldsDefinitions(organizationId, issueDefinition, existingFieldsMap);
    }

    private void deleteIssueFields(final IssueDefinitionRest issueDefinition,
        final Map<Long, IssueFieldDefinition> existingFieldsMap,
        final Set<Long> definitionIdsToDelete)
    {
        final Set<Long> definitionIdsWithChangedType = issueDefinition.getFields().stream()
            .filter(fieldRest -> Optional.ofNullable(fieldRest.getDefinitionId())
                .map(existingFieldsMap::get)
                .map(IssueFieldDefinition::getType)
                .filter(type -> fieldRest.getType() != type)
                .isPresent())
            .map(IssueFieldDefinitionRest::getDefinitionId)
            .collect(Collectors.toSet());
        issueFieldRepository.deleteAll(issueFieldRepository.getListByDefinitionIds(
            Sets.union(definitionIdsToDelete, definitionIdsWithChangedType)));
    }

    private void deleteIssueFieldDefinitions(final Set<Long> definitionIdsToDelete)
    {
        ifdRepository.deleteAllById(definitionIdsToDelete);
    }

    private void saveIssueFieldsDefinitions(final Long organizationId,
        final IssueDefinitionRest issueDefinition,
        final Map<Long, IssueFieldDefinition> existingFieldsMap)
    {
        final List<IssueFieldDefinition> definitionsToSave = issueDefinition.getFields().stream()
            .collect(Collectors.groupingBy(IssueFieldDefinitionRest::getColumn))
            .values().stream()
            .flatMap(colFields -> IntStream.range(0, colFields.size())
                .mapToObj(i -> {
                    final IssueFieldDefinitionRest fieldDefinitionRest = colFields.get(i);
                    if(fieldDefinitionRest.getDefinitionId() == null)
                    {
                        return getIssueFieldDefinition(organizationId, colFields.get(i), i);
                    }
                    final IssueFieldDefinition fieldDefinition =
                        existingFieldsMap.get(fieldDefinitionRest.getDefinitionId());
                    fieldDefinition.setName(fieldDefinitionRest.getName());
                    fieldDefinition.setType(fieldDefinitionRest.getType());
                    fieldDefinition.setRequired(
                        fieldDefinitionRest.isRequired());
                    fieldDefinition.setClientVisible(
                        fieldDefinitionRest.isClientVisible());
                    fieldDefinition.setCol(fieldDefinitionRest.getColumn());
                    fieldDefinition.setRow((short) i);
                    return fieldDefinition;
                }))
            .collect(Collectors.toList());
        ifdRepository.saveAll(definitionsToSave);
    }

    private static IssueFieldDefinition getIssueFieldDefinition(final Long organizationId,
        final IssueFieldDefinitionRest field, final int row)
    {
        return new IssueFieldDefinition(organizationId, (short) row, field.getColumn(),
            field.getName(), field.getType(), field.isRequired(), field.isClientVisible());
    }

    public enum IssueDefinitionError
    {
        EMPTY_FIELD_DEFINITIONS,
        EMPTY_CATEGORIES,
        EMPTY_STATUSES,
        USED_STATUS_DELETE,
        USED_CATEGORY_DELETE,
        OUT_OF_DATE
    }

}
