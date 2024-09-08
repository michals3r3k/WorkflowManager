package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.db.issue.IssueCategoryRepository;
import com.example.workflowmanager.db.issue.IssueFieldDefinitionRepository;
import com.example.workflowmanager.db.issue.IssueStatusRepository;
import com.example.workflowmanager.entity.issue.*;
import com.example.workflowmanager.service.issue.IssueDefinitionService;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class IssueDefinitionController
{
    private final IssueFieldDefinitionRepository ifdRepository;
    private final IssueStatusRepository isRepository;
    private final IssueCategoryRepository icRepository;
    private final IssueDefinitionService service;

    public IssueDefinitionController(final IssueFieldDefinitionRepository ifdRepository,
        final IssueStatusRepository isRepository,
        final IssueCategoryRepository icRepository,
        final IssueDefinitionService service)
    {
        this.ifdRepository = ifdRepository;
        this.isRepository = isRepository;
        this.icRepository = icRepository;
        this.service = service;
    }

    @GetMapping("/api/organization/{organizationId}/issue-definition")
    @Transactional
    public ResponseEntity<IssueDefinitionRest> get(@PathVariable Long organizationId)
    {
        final List<String> statuses = getStatuses(organizationId);
        final List<String> categories = getCategories(organizationId);
        final List<IssueFieldDefinitionRest> fieldDefinitions = getFieldDefinitions(organizationId);
        return ResponseEntity.ok(new IssueDefinitionRest(statuses, categories, fieldDefinitions, IssueStatus.CONST_STATUSES));
    }

    @PostMapping("/api/organization/{organizationId}/issue-definition/create")
    @Transactional
    public ResponseEntity<ServiceResult<?>> save(@PathVariable Long organizationId,
        @RequestBody IssueDefinitionRest issueDefinition)
    {
        return ResponseEntity.ok(service.save(organizationId, issueDefinition));
    }

    private List<String> getCategories(final Long organizationId)
    {
        return icRepository.getListByOrganizationIds(
            Collections.singleton(organizationId)).stream()
            .map(IssueCategory::getId)
            .map(IssueCategoryId::getCategory)
            .collect(Collectors.toList());
    }

    private List<String> getStatuses(final Long organizationId)
    {
        return isRepository.getListByOrganizationIds(
            Collections.singleton(organizationId)).stream()
            .map(IssueStatus::getId)
            .map(IssueStatusId::getStatus)
            .collect(Collectors.toList());
    }

    private List<IssueFieldDefinitionRest> getFieldDefinitions(final Long organizationId)
    {
        return ifdRepository.getListByOrganizationId(
            Collections.singleton(organizationId)).stream()
            .sorted(Comparator.comparing(IssueFieldDefinition::getCol))
            .map(field -> new IssueFieldDefinitionRest(field.getId(), field.getName(),
                field.getCol(), field.getType(), field.isRequired(),
                field.isClientVisible()))
            .collect(Collectors.toList());
    }

}
