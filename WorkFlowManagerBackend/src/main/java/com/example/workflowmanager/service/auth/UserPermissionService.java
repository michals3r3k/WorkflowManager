package com.example.workflowmanager.service.auth;

import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.db.organization.role.OrganizationMemberRoleRepository;
import com.example.workflowmanager.db.organization.role.OrganizationPermissionRepository;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.OrganizationMemberId;
import com.example.workflowmanager.entity.organization.role.OrganizationPermissionId;
import com.example.workflowmanager.entity.organization.role.OrganizationRoleId;
import com.example.workflowmanager.entity.organization.role.Permission;
import com.example.workflowmanager.entity.user.User;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserPermissionService
{
    private final CurrentUserService cuService;
    private final OrganizationRepository oRepository;
    private final OrganizationMemberRoleRepository omrRepository;
    private final OrganizationPermissionRepository opRepository;

    public UserPermissionService(
        CurrentUserService cuService,
        OrganizationRepository oRepository,
        OrganizationMemberRoleRepository omrRepository,
        OrganizationPermissionRepository opRepository)
    {
        this.cuService = cuService;
        this.oRepository = oRepository;
        this.omrRepository = omrRepository;
        this.opRepository = opRepository;
    }

    public Set<Permission> getCurrentUserPermissions(Long organizationId)
    {
        return cuService.getCurrentUser()
            .map(User::getId)
            .map(userId -> getPermissions(userId, organizationId))
            .orElseGet(Collections::emptySet);
    }

    Set<GrantedAuthority> getAuthorities(Long userId, Long organizationIdOrNull)
    {
        return getPermissions(userId, organizationIdOrNull).stream()
            .map(Enum::name)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    }

    public Set<Permission> getPermissions(Long userId, Long organizationIdOrNull)
    {
        Preconditions.checkNotNull(userId);
        Organization organization = Optional.ofNullable(organizationIdOrNull)
            .map(oRepository::getByIdWithUser)
            .orElse(null);
        if(organization == null)
        {
            return Collections.emptySet();
        }
        if(organization.getUser().getId().equals(userId))
        {
            return Sets.immutableEnumSet(EnumSet.allOf(Permission.class));
        }
        Set<OrganizationRoleId> roleIds = omrRepository.getIdListByOrganizationMemberIds(
            Collections.singleton(new OrganizationMemberId(organizationIdOrNull, userId)));
        return opRepository.getListByRoleIds(roleIds)
            .stream()
            .map(OrganizationPermissionId::getPermission)
            .collect(Sets.toImmutableEnumSet());
    }

}
