package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.service.issue.IssueEditService;
import com.example.workflowmanager.service.issue.IssueEditService.IssueEditError;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class IssueFormController
{
    private final IssueEditService organizationIssueCreateService;
    private final IssueFormFactory issueFormFactory;

    public IssueFormController(
        final IssueEditService organizationIssueCreateService,
        final IssueFormFactory issueFormFactory)
    {
        this.organizationIssueCreateService = organizationIssueCreateService;
        this.issueFormFactory = issueFormFactory;
    }

    @GetMapping("/api/issue-form/{destinationOrganizationId}")
    @Transactional
    public IssueFormRest getForm(@PathVariable Long destinationOrganizationId)
    {
        return issueFormFactory.getEmptyForClient(destinationOrganizationId);
    }

    @PostMapping("/api/issue-form/{sourceOrganizationId}/create")
    @Transactional
    public ResponseEntity<ServiceResult<IssueEditError>> create(@PathVariable Long sourceOrganizationId,
        @RequestBody IssueFormRest form)
    {
        return ResponseEntity.ok(organizationIssueCreateService.create(sourceOrganizationId, form));
    }

    @PostMapping("/api/organization/{organizationId}/issue-form/edit")
    @Transactional
    public ResponseEntity<ServiceResult<IssueEditError>> edit(@PathVariable Long organizationId,
        @RequestBody IssueFormRest form)
    {
        return ResponseEntity.ok(organizationIssueCreateService.edit(form));
    }

}
