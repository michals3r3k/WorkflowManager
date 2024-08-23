package com.example.workflowmanager.service.auth;

import com.example.workflowmanager.db.user.UserRepository;
import com.example.workflowmanager.entity.user.User;
import com.google.common.collect.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
    private final UserRepository uRepository;
    private final UserPermissionService permissionService;

    public CustomUserDetailsService(UserRepository uRepository,
        UserPermissionService permissionService)
    {
        this.uRepository = uRepository;
        this.permissionService = permissionService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        return loadUserByUsernameAndOrganizationId(email, null);
    }

    @Transactional
    public UserDetails loadUserByUsernameAndOrganizationId(String email, Long organizationIdOrNull) throws UsernameNotFoundException
    {
        User user = getUser(email);
        Set<GrantedAuthority> authorities =
            permissionService.getAuthorities(user.getId(), organizationIdOrNull);
        System.out.println("User: " + email + ", Authorities: " + authorities);
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), Sets.union(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), authorities));
    }

    private User getUser(String email)
    {
        return uRepository.findByEmail(email)
            .orElseThrow(NoSuchElementException::new);
    }

}
