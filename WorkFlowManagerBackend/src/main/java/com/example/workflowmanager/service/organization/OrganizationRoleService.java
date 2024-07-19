package com.example.workflowmanager.service.organization;

import com.example.workflowmanager.db.organization.role.OrganizationMemberRoleRepository;
import com.example.workflowmanager.db.organization.role.OrganizationPermissionRepository;
import com.example.workflowmanager.db.organization.role.OrganizationRoleRepository;
import com.example.workflowmanager.entity.organization.role.*;
import com.example.workflowmanager.rest.organization.OrganizationRoleDetailsRest;
import com.example.workflowmanager.rest.organization.OrganizationRoleDetailsRest.OrganizationMemberRest;
import com.example.workflowmanager.rest.organization.OrganizationRoleDetailsRest.PermissionRest;
import com.example.workflowmanager.rest.organization.OrganizationRoleDetailsRest.PermissionSectionRest;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrganizationRoleService
{
    private final OrganizationPermissionRepository permissionRepository;
    private final OrganizationMemberRoleRepository memberRoleRepository;
    private final OrganizationRoleRepository roleRepository;

    public OrganizationRoleService(OrganizationPermissionRepository permissionRepository,
        OrganizationMemberRoleRepository memberRoleRepository,
        OrganizationRoleRepository roleRepository)
    {
        this.permissionRepository = permissionRepository;
        this.memberRoleRepository = memberRoleRepository;
        this.roleRepository = roleRepository;
    }

    public ServiceResult<OrganizationRoleError> delete(OrganizationRoleId roleId)
    {
        if(!isRoleExists(roleId))
        {
            return ServiceResult.error(OrganizationRoleError.ROLE_DOESNT_EXIST);
        }
        final Set<OrganizationPermissionId> permissionsExisting = getPermissionsExisting(roleId);
        final Set<OrganizationMemberRoleId> membersExisting = getMembersExisting(roleId);
        permissionRepository.deleteAllById(permissionsExisting);
        memberRoleRepository.deleteAllById(membersExisting);
        roleRepository.deleteById(roleId);
        return ServiceResult.ok();
    }

    public ServiceResult<OrganizationRoleError> create(OrganizationRoleId roleId)
    {
        if(isRoleExists(roleId))
        {
            return ServiceResult.error(OrganizationRoleError.ROLE_ALREADY_EXISTS);
        }
        roleRepository.save(new OrganizationRole(roleId));
        return ServiceResult.ok();
    }


    public ServiceResult<OrganizationRoleError> edit(OrganizationRoleId roleId, OrganizationRoleDetailsRest roleRest)
    {
        if(!isRoleExists(roleId))
        {
            return ServiceResult.error(OrganizationRoleError.ROLE_DOESNT_EXIST);
        }
        updatePermissions(roleId, roleRest);
        updateMembers(roleId, roleRest);
        return ServiceResult.ok();
    }

    private ServiceResult<OrganizationRoleError> updatePermissions(final OrganizationRoleId roleId,
        final OrganizationRoleDetailsRest role)
    {
        if(!isRoleExists(roleId))
        {
            return ServiceResult.error(OrganizationRoleError.ROLE_DOESNT_EXIST);
        }
        final Set<OrganizationPermissionId> permissionsExisting = getPermissionsExisting(roleId);
        final Set<OrganizationPermissionId> permissionsGiven = role.getPermissionSections().stream()
            .map(PermissionSectionRest::getPermissions)
            .flatMap(Collection::stream)
            .filter(PermissionRest::isSelected)
            .map(PermissionRest::getPermission)
            .map(Permission::valueOf)
            .map(permission -> new OrganizationPermissionId(permission, roleId))
            .collect(Collectors.toSet());
        final Set<OrganizationPermissionId> permissionsToDelete =
            Sets.difference(permissionsExisting, permissionsGiven);
        permissionRepository.deleteAllById(permissionsToDelete);
        Sets.difference(permissionsGiven, permissionsExisting).stream()
            .map(OrganizationPermission::new)
            .forEach(permissionRepository::save);
        return ServiceResult.ok();
    }

    private boolean isRoleExists(final OrganizationRoleId roleId)
    {
        return roleRepository.findById(roleId).isPresent();
    }

    private Set<OrganizationPermissionId> getPermissionsExisting(final OrganizationRoleId roleId)
    {
        return ImmutableSet.copyOf(permissionRepository.getListByRoleIds(Collections.singleton(roleId)));
    }

    private void updateMembers(final OrganizationRoleId roleId,
        final OrganizationRoleDetailsRest role)
    {
        final Set<OrganizationMemberRoleId> membersExisting = getMembersExisting(roleId);
        final Set<OrganizationMemberRoleId> membersGiven = role.getMembers().stream()
            .filter(OrganizationMemberRest::isSelected)
            .map(OrganizationMemberRest::getUserId)
            .map(userId -> new OrganizationMemberRoleId(userId, roleId))
            .collect(Collectors.toSet());
        final Set<OrganizationMemberRoleId> membersToDelete =
            Sets.difference(membersExisting, membersGiven);
        memberRoleRepository.deleteAllById(membersToDelete);
        Sets.difference(membersGiven, membersExisting).stream()
            .map(OrganizationMemberRole::new)
            .forEach(memberRoleRepository::save);
    }

    private Set<OrganizationMemberRoleId> getMembersExisting(final OrganizationRoleId roleId)
    {
        return ImmutableSet.copyOf(memberRoleRepository.getIdListByRoleIds(
            Collections.singleton(roleId)));
    }

    public enum OrganizationRoleError
    {
        ROLE_DOESNT_EXIST,
        ROLE_ALREADY_EXISTS,
    }

}
