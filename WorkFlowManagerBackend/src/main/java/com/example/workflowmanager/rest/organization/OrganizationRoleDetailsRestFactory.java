package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.db.organization.role.OrganizationMemberRoleRepository;
import com.example.workflowmanager.db.organization.role.OrganizationPermissionRepository;
import com.example.workflowmanager.db.user.UserRepository;
import com.example.workflowmanager.entity.organization.OrganizationMember;
import com.example.workflowmanager.entity.organization.OrganizationMemberId;
import com.example.workflowmanager.entity.organization.role.OrganizationMemberRoleId;
import com.example.workflowmanager.entity.organization.role.OrganizationPermissionId;
import com.example.workflowmanager.entity.organization.role.OrganizationRoleId;
import com.example.workflowmanager.entity.organization.role.Permission;
import com.example.workflowmanager.entity.user.User;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class OrganizationRoleDetailsRestFactory
{
    private final OrganizationPermissionRepository permissionRepository;
    private final OrganizationMemberRepository memberRepository;
    private final OrganizationMemberRoleRepository memberRoleRepository;
    private final UserRepository userRepository;

    public OrganizationRoleDetailsRestFactory(
        OrganizationPermissionRepository permissionRepository,
        OrganizationMemberRepository memberRepository,
        OrganizationMemberRoleRepository memberRoleRepository,
        UserRepository userRepository)
    {
        this.permissionRepository = permissionRepository;
        this.memberRepository = memberRepository;
        this.memberRoleRepository = memberRoleRepository;
        this.userRepository = userRepository;
    }

    public OrganizationRoleDetailsRest getOrganizationRoleDetails(
        final OrganizationRoleId organizationRoleId)
    {
        final List<OrganizationRoleDetailsRest.PermissionRest> permissions =
            getPermissionRestList(organizationRoleId);
        final List<OrganizationRoleDetailsRest.OrganizationMemberRest> members =
            getMemberRestList(organizationRoleId);
        return new OrganizationRoleDetailsRest(permissions, members);
    }

    private List<OrganizationRoleDetailsRest.OrganizationMemberRest> getMemberRestList(
        final OrganizationRoleId organizationRoleId)
    {
        final Set<Long> userIdsSelected = memberRoleRepository
            .getIdListByRoleIds(Collections.singleton(organizationRoleId)).stream()
            .map(OrganizationMemberRoleId::getUserId)
            .collect(Collectors.toSet());
        final Set<Long> userIdsAll = memberRepository.getListByOrganization(
                Collections.singleton(organizationRoleId.getOrganizationId())).stream()
            .map(OrganizationMember::getId)
            .map(OrganizationMemberId::getUserId)
            .collect(Collectors.toSet());
        return userRepository.getListByIds(userIdsAll).stream()
            .sorted(Comparator.comparing(User::getEmail)
                .thenComparing(User::getId))
            .map(user -> new OrganizationRoleDetailsRest.OrganizationMemberRest(user,
                userIdsSelected.contains(user.getId())))
            .collect(Collectors.toList());
    }

    private List<OrganizationRoleDetailsRest.PermissionRest> getPermissionRestList(
        final OrganizationRoleId organizationRoleId)
    {
        final Set<Permission> selectedPermissions = permissionRepository.getListByRoleIds(
                Collections.singleton(organizationRoleId)).stream()
            .map(OrganizationPermissionId::getPermission)
            .collect(Sets.toImmutableEnumSet());
        return EnumSet.allOf(Permission.class).stream()
            .map(permission -> new OrganizationRoleDetailsRest.PermissionRest(
                permission, selectedPermissions.contains(permission)))
            .collect(Collectors.toList());
    }

}
