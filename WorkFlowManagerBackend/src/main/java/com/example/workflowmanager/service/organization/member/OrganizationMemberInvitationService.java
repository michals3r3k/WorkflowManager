package com.example.workflowmanager.service.organization.member;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.entity.organization.OrganizationMember;
import com.example.workflowmanager.entity.organization.OrganizationMemberId;
import com.example.workflowmanager.entity.organization.OrganizationMemberInvitationStatus;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.Iterables;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;

@Service
public class OrganizationMemberInvitationService
{
    private final OrganizationMemberRepository omRepository;

    public OrganizationMemberInvitationService(
        final OrganizationMemberRepository omRepository)
    {
        this.omRepository = omRepository;
    }

    public ServiceResult<OrganizationMemberInvitationError> invite(OrganizationMemberId id,
        LocalDateTime now)
    {
        final OrganizationMember memberOrNull = Iterables.getFirst(
            omRepository.getListByIds(Collections.singleton(id)), null);
        final ServiceResult<OrganizationMemberInvitationError> result =
            validate(memberOrNull);
        if(result.isSuccess())
        {
            final OrganizationMember memberToSave = getOrCreate(id, memberOrNull);
            memberToSave.setInvitationTime(now);
            memberToSave.setInvitationStatus(OrganizationMemberInvitationStatus.INVITED);
            omRepository.save(memberToSave);
        }
        return result;

    }

    private static ServiceResult<OrganizationMemberInvitationError> validate(
        final OrganizationMember memberOrNull)
    {
        final OrganizationMemberInvitationStatus statusOrNull = ObjectUtils.accessNullable(
            memberOrNull, OrganizationMember::getInvitationStatus);
        final EnumSet<OrganizationMemberInvitationError> errors =
            EnumSet.noneOf(OrganizationMemberInvitationError.class);
        if(statusOrNull == OrganizationMemberInvitationStatus.INVITED)
        {
            errors.add(OrganizationMemberInvitationError.ALREADY_INVITED);
        }
        if(statusOrNull == OrganizationMemberInvitationStatus.ACCEPTED)
        {
            errors.add(OrganizationMemberInvitationError.ALREADY_ACCEPTED);
        }
        return new ServiceResult<>(errors);
    }

    private OrganizationMember getOrCreate(OrganizationMemberId id,
        OrganizationMember existingMemberOrNull)
    {
        if(existingMemberOrNull == null)
        {
            return new OrganizationMember(id);
        }
        return existingMemberOrNull;
    }

    public enum OrganizationMemberInvitationError
    {
        ALREADY_INVITED,
        ALREADY_ACCEPTED;
    }

}
