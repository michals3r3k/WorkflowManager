package com.example.workflowmanager.service.organization;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.OrganizationMember;
import com.example.workflowmanager.entity.organization.OrganizationMemberInvitationStatus;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserOrganizationService
{
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    public UserOrganizationService(final OrganizationRepository organizationRepository,
        final OrganizationMemberRepository organizationMemberRepository)
    {
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
    }

    public Set<Organization> getSet(final Collection<Long> userIds)
    {
        return Stream.concat(
            organizationMemberRepository
                .getListByUserIdsWithOrganization(userIds, Collections.singleton(OrganizationMemberInvitationStatus.ACCEPTED))
                .stream()
                .map(OrganizationMember::getOrganization),
            organizationRepository.getListByUserIds(userIds).stream())
            .collect(Collectors.toSet());
    }
}
