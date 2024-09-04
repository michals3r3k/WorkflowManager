package com.example.workflowmanager.service.auth;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.db.organization.role.OrganizationMemberRoleRepository;
import com.example.workflowmanager.db.organization.role.OrganizationPermissionRepository;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.OrganizationMemberId;
import com.example.workflowmanager.entity.organization.role.OrganizationPermissionId;
import com.example.workflowmanager.entity.organization.role.OrganizationRoleId;
import com.example.workflowmanager.entity.organization.role.Permission;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.organization.UserOrganizationService;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserPermissionService
{
    private final CurrentUserService cuService;
    private final OrganizationRepository oRepository;
    private final OrganizationMemberRoleRepository omrRepository;
    private final OrganizationPermissionRepository opRepository;
    private final UserOrganizationService userOrganizationService;

    public UserPermissionService(
        final CurrentUserService cuService,
        final OrganizationRepository oRepository,
        final OrganizationMemberRepository omRepository,
        final OrganizationMemberRoleRepository omrRepository,
        final OrganizationPermissionRepository opRepository,
        final UserOrganizationService userOrganizationService)
    {
        this.cuService = cuService;
        this.oRepository = oRepository;
        this.omrRepository = omrRepository;
        this.opRepository = opRepository;
        this.userOrganizationService = userOrganizationService;
    }

    public Map<Long, Collection<Permission>> getCurrentUserPermissions()
    {
        final Long userId = cuService.getCurrentUser()
            .map(User::getId)
            .orElse(null);
        if(userId == null)
        {
            return Collections.emptyMap();
        }
        final ListMultimap<Long, Permission> result = ArrayListMultimap.create();
        for(final Organization organization : userOrganizationService.getSet(Collections.singleton(userId)))
        {
            final Long organizationId = organization.getId();
            final Set<Permission> permissions = getPermissions(userId,
                organizationId);
            result.putAll(organizationId, permissions);
        }
        return result.asMap();
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
        Set<OrganizationRoleId> roleIds = omrRepository.getRoleIdListByIds(
            Collections.singleton(new OrganizationMemberId(organizationIdOrNull, userId)));
        return opRepository.getListByRoleIds(roleIds)
            .stream()
            .map(OrganizationPermissionId::getPermission)
            .collect(Sets.toImmutableEnumSet());
    }

}
