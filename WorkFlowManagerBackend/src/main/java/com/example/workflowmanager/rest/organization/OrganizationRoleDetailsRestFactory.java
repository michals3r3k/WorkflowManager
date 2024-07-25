package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.db.organization.role.OrganizationMemberRoleRepository;
import com.example.workflowmanager.db.organization.role.OrganizationPermissionRepository;
import com.example.workflowmanager.db.user.UserRepository;
import com.example.workflowmanager.entity.organization.OrganizationMember;
import com.example.workflowmanager.entity.organization.OrganizationMemberId;
import com.example.workflowmanager.entity.organization.role.*;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.rest.organization.OrganizationRoleDetailsRest.OrganizationMemberRest;
import com.example.workflowmanager.rest.organization.OrganizationRoleDetailsRest.PermissionRest;
import com.example.workflowmanager.rest.organization.OrganizationRoleDetailsRest.PermissionSectionRest;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
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
        final List<PermissionSectionRest> sections =
            getPermissionSections(organizationRoleId);
        final List<OrganizationMemberRest> members =
            getMemberRestList(organizationRoleId);
        return new OrganizationRoleDetailsRest(sections, members);
    }

    private List<OrganizationMemberRest> getMemberRestList(
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
            .map(user -> new OrganizationMemberRest(user,
                userIdsSelected.contains(user.getId())))
            .collect(Collectors.toList());
    }

    private List<PermissionSectionRest> getPermissionSections(final OrganizationRoleId organizationRoleId)
    {
        final Set<Permission> selectedPermissions = getSelectedPermissions(organizationRoleId);
        final ListMultimap<PermissionSection, Permission> permissionSectionMap =
            Multimaps.index(EnumSet.allOf(Permission.class), Permission::getSection);
        return EnumSet.allOf(PermissionSection.class).stream()
            .map(section -> {
                final List<PermissionRest> permissions = permissionSectionMap
                    .get(section).stream()
                    .map(permission -> new PermissionRest(permission,
                        selectedPermissions.contains(permission)))
                    .collect(Collectors.toList());
                return new PermissionSectionRest(section, permissions);
            })
            .collect(Collectors.toList());
    }

    private Set<Permission> getSelectedPermissions(final OrganizationRoleId organizationRoleId)
    {
        return permissionRepository.getListByRoleIds(Collections.singleton(organizationRoleId)).stream()
            .map(OrganizationPermissionId::getPermission)
            .collect(Sets.toImmutableEnumSet());
    }


}
