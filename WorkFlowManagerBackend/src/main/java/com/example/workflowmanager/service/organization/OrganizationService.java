package com.example.workflowmanager.service.organization;

import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.db.user.UserRepository;
import com.example.workflowmanager.entity.organization.Organization;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
public class OrganizationService
{
    private OrganizationRepository organizationRepository;
    private UserRepository userRepository;

    public OrganizationService(OrganizationRepository organizationRepository,
        UserRepository userRepository)
    {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    public OrganizationServiceResult create(String email, String name, String description)
    {
        Organization organization = new Organization();
        organization.setName(name);
        organization.setDescription(description);
        userRepository.findByEmail(email)
            .ifPresent(organization::setUser);
        organizationRepository.save(organization);
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

