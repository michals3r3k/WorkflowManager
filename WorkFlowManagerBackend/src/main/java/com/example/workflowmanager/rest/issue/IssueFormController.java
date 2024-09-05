package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.service.issue.OrganizationIssueCreateService;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin
@RestController
public class IssueFormController
{
    private final OrganizationIssueCreateService organizationIssueCreateService;
    private final IssueFormFactory issueFormFactory;

    public IssueFormController(
        final OrganizationIssueCreateService organizationIssueCreateService,
        final IssueFormFactory issueFormFactory)
    {
        this.organizationIssueCreateService = organizationIssueCreateService;
        this.issueFormFactory = issueFormFactory;
    }

    @GetMapping("/api/organization/{organizationId}/issue-form/{destinationOrganizationId}")
    @Transactional
    public IssueFormRest getForm(@PathVariable Long organizationId, @PathVariable Long destinationOrganizationId)
    {
        return issueFormFactory.getEmptyForClient(destinationOrganizationId);
    }

    @PostMapping("/api/organization/{organizationId}/issue-form/send")
    @Transactional
    public ResponseEntity<ServiceResult<?>> sendForm(@PathVariable Long organizationId,
        @RequestBody IssueFormRest form)
    {
        final LocalDateTime created = LocalDateTime.now();
        return ResponseEntity.ok(organizationIssueCreateService.create(organizationId, form, created));
    }

}
