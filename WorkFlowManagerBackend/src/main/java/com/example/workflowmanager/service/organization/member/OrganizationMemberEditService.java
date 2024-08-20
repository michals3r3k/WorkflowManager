package com.example.workflowmanager.service.organization.member;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.db.organization.role.OrganizationMemberRoleRepository;
import com.example.workflowmanager.db.organization.role.OrganizationRoleRepository;
import com.example.workflowmanager.entity.organization.OrganizationMember;
import com.example.workflowmanager.entity.organization.OrganizationMemberId;
import com.example.workflowmanager.entity.organization.OrganizationMemberInvitationStatus;
import com.example.workflowmanager.entity.organization.role.OrganizationMemberRole;
import com.example.workflowmanager.entity.organization.role.OrganizationMemberRoleId;
import com.example.workflowmanager.entity.organization.role.OrganizationRole;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.Iterables;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
public class OrganizationMemberEditService
{
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationRoleRepository organizationRoleRepository;
    private final OrganizationMemberRoleRepository organizationMemberRoleRepository;

    public OrganizationMemberEditService(
        final OrganizationMemberRepository organizationMemberRepository,
        final OrganizationRoleRepository organizationRoleRepository,
        final OrganizationMemberRoleRepository organizationMemberRoleRepository)
    {
        this.organizationMemberRepository = organizationMemberRepository;
        this.organizationRoleRepository = organizationRoleRepository;
        this.organizationMemberRoleRepository = organizationMemberRoleRepository;
    }

    public ServiceResult<OrganizationMemberEditError> changeInvitationStatus(
        final OrganizationMemberId id, final OrganizationMemberInvitationStatus status)
    {
        final OrganizationMember memberOrNull = getOrganizationMemberOrNull(id);
        final Set<OrganizationMemberEditError> errors = validate(memberOrNull);
        if(errors.isEmpty())
        {
            edit(memberOrNull, status);
            if(status == OrganizationMemberInvitationStatus.ACCEPTED)
            {
                addDefaultRoles(memberOrNull);
            }
        }
        return new ServiceResult<>(errors);
    }

    private OrganizationMember getOrganizationMemberOrNull(final OrganizationMemberId id)
    {
        return Iterables.getFirst(organizationMemberRepository.getListByIds(Collections.singleton(id)), null);
    }

    private Set<OrganizationMemberEditError> validate(OrganizationMember memberOrNull)
    {
        if(memberOrNull == null)
        {
            return Collections.singleton(OrganizationMemberEditError.NOT_EXISTS);
        }
        if(memberOrNull.getInvitationStatus() != OrganizationMemberInvitationStatus.INVITED)
        {
            return Collections.singleton(OrganizationMemberEditError.NOT_INVITED);
        }
        return Collections.emptySet();
    }

    private void edit(OrganizationMember member,
        OrganizationMemberInvitationStatus status)
    {
        member.setInvitationStatus(status);
        organizationMemberRepository.save(member);
    }

    private void addDefaultRoles(OrganizationMember member)
    {
        organizationRoleRepository.getListByOrganization(
                Collections.singleton(member.getId().getOrganizationId())).stream()
            .filter(OrganizationRole::isAddToNewMembers)
            .map(OrganizationRole::getId)
            .map(id -> new OrganizationMemberRoleId(member.getId().getUserId(), id))
            .map(OrganizationMemberRole::new)
            .forEachOrdered(organizationMemberRoleRepository::save);
    }

    public enum OrganizationMemberEditError
    {
        NOT_EXISTS,
        NOT_INVITED
    }

}
