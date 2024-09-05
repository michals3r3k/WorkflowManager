package com.example.workflowmanager.service.organization;

import com.example.workflowmanager.db.issue.IssueStatusRepository;
import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.db.user.UserRepository;
import com.example.workflowmanager.entity.issue.IssueStatus;
import com.example.workflowmanager.entity.issue.IssueStatusId;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

@Service
public class OrganizationService
{
    private final OrganizationRepository organizationRepository;
    private final IssueStatusRepository issueStatusRepository;
    private final UserRepository userRepository;

    public OrganizationService(final OrganizationRepository organizationRepository,
        final IssueStatusRepository issueStatusRepository, final UserRepository userRepository)
    {
        this.organizationRepository = organizationRepository;
        this.issueStatusRepository = issueStatusRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrganizationServiceResult create(String email, String name, String description)
    {
        final User user = userRepository.findByEmail(email).orElseThrow();
        Organization organization = new Organization(name, description, user);
        organizationRepository.save(organization);
        IssueStatus.CONST_STATUSES.stream()
            .map(status -> new IssueStatusId(organization.getId(), status))
            .map(IssueStatus::new)
            .forEachOrdered(issueStatusRepository::save);
        return new OrganizationServiceResult(Collections.emptySet());
    }

    public enum OrganizationCreateError
    {
        // TODO: add possible errors
    }

    public static class OrganizationServiceResult
    {
        private final Set<OrganizationCreateError> errors;

        private OrganizationServiceResult(Set<OrganizationCreateError> errors)
        {
            this.errors = errors;
        }

        public Set<OrganizationCreateError> getErrors()
        {
            return errors;
        }

        public boolean isSuccess()
        {
            return errors.isEmpty();
        }

    }

}

