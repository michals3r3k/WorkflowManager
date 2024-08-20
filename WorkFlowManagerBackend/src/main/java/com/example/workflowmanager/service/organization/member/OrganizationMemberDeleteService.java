package com.example.workflowmanager.service.organization.member;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.db.organization.role.OrganizationMemberRoleRepository;
import com.example.workflowmanager.entity.organization.OrganizationMember;
import com.example.workflowmanager.entity.organization.OrganizationMemberId;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.Iterables;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class OrganizationMemberDeleteService
{
    private final OrganizationMemberRepository memberRepository;
    private final OrganizationMemberRoleRepository memberRoleRepository;
    public OrganizationMemberDeleteService(
        final OrganizationMemberRepository memberRepository,
        final OrganizationMemberRoleRepository memberRoleRepository)
    {
        this.memberRepository = memberRepository;
        this.memberRoleRepository = memberRoleRepository;
    }

    public ServiceResult<OrganizationMemberDeleteError> delete(final OrganizationMemberId id)
    {
        final OrganizationMember memberOrNull = Iterables.getFirst(
            memberRepository.getListByIds(Collections.singleton(id)), null);
        if(memberOrNull == null)
        {
            return ServiceResult.error(OrganizationMemberDeleteError.NOT_EXISTS);
        }
        memberRoleRepository.deleteAll(memberOrNull.getRoles());
        memberRepository.delete(memberOrNull);
        return ServiceResult.ok();
    }

    public enum OrganizationMemberDeleteError
    {
        NOT_EXISTS,
    }

}
