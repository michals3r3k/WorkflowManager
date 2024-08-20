package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.db.organization.role.OrganizationRoleRepository;
import com.example.workflowmanager.entity.organization.role.OrganizationRole;
import com.example.workflowmanager.entity.organization.role.OrganizationRoleId;
import com.example.workflowmanager.rest.utils.RestServiceResult;
import com.example.workflowmanager.service.organization.OrganizationRoleService;
import com.example.workflowmanager.service.organization.OrganizationRoleService.OrganizationRoleError;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class OrganizationRoleController
{
    private final OrganizationRoleRepository roleRepository;
    private final OrganizationRoleDetailsRestFactory roleDetailsRestFactory;
    private final OrganizationRoleService organizationRoleService;

    public OrganizationRoleController(
        final OrganizationRoleRepository roleRepository,
        final OrganizationRoleDetailsRestFactory roleDetailsRestFactory,
        final OrganizationRoleService organizationRoleService)
    {
        this.roleRepository = roleRepository;
        this.roleDetailsRestFactory = roleDetailsRestFactory;
        this.organizationRoleService = organizationRoleService;
    }

    @PostMapping("/api/organization/{organizationId}/role/create")
    @PreAuthorize("hasAuthority('ROLE_C')")
    public ResponseEntity<RestServiceResult> addRole(
        @PathVariable Long organizationId, @RequestBody String role)
    {
        final ServiceResult<OrganizationRoleError> result = organizationRoleService
            .create(new OrganizationRoleId(organizationId, role));
        return ResponseEntity.ok(RestServiceResult.fromEnum(result));
    }

    @GetMapping("/api/organization/{organizationId}/role/list")
    @PreAuthorize("hasAuthority('ROLE_R')")
    public ResponseEntity<List<OrganizationRoleRest>> getRoleList(@PathVariable Long organizationId)
    {
        List<OrganizationRoleRest> roles = roleRepository
            .getListByOrganization(Collections.singleton(organizationId)).stream()
            .map(OrganizationRoleRest::new)
            .sorted(Comparator.comparing(OrganizationRoleRest::getName, Comparator.naturalOrder()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    @GetMapping("api/organization/{organizationId}/role/{role}/available")
    public ResponseEntity<Boolean> isRoleNameAvailable(@PathVariable Long organizationId, @PathVariable String role) {
        boolean isTaken = roleRepository
                .getListByOrganization(Collections.singleton(organizationId)).stream()
                .anyMatch(r -> r.getId().getRole().equalsIgnoreCase(role));
        return ResponseEntity.ok(!isTaken);
    }

    @GetMapping("/api/organization/{organizationId}/role/{role}")
    @PreAuthorize("hasAuthority('ROLE_U')")
    public ResponseEntity<OrganizationRoleDetailsRest> getRoleDetails(
        @PathVariable Long organizationId, @PathVariable String role)
    {
        final OrganizationRoleDetailsRest roleDetails = roleDetailsRestFactory
            .getOrganizationRoleDetails(new OrganizationRoleId(organizationId, role));
        return ResponseEntity.ok(roleDetails);
    }

    @PostMapping("/api/organization/{organizationId}/role/{role}/edit")
    @PreAuthorize("hasAuthority('ROLE_U')")
    public ResponseEntity<RestServiceResult> editRole(@PathVariable Long organizationId,
        @PathVariable String role, @RequestBody OrganizationRoleDetailsRest roleDetails)
    {
        final ServiceResult<OrganizationRoleError> result = organizationRoleService
            .edit(new OrganizationRoleId(organizationId, role), roleDetails);
        return ResponseEntity.ok(RestServiceResult.fromEnum(result));
    }

    @GetMapping("/api/organization/{organizationId}/role/{role}/delete")
    @PreAuthorize("hasAuthority('ROLE_D')")
    public ResponseEntity<RestServiceResult> deleteRole(@PathVariable Long organizationId,
        @PathVariable String role)
    {
        final ServiceResult<OrganizationRoleError> result = organizationRoleService
            .delete(new OrganizationRoleId(organizationId, role));
        return ResponseEntity.ok(RestServiceResult.fromEnum(result));
    }

    public static class OrganizationRoleCreateServiceResult
    {
        private final boolean success;

        private OrganizationRoleCreateServiceResult(boolean success)
        {
            this.success = success;
        }

        public boolean isSuccess()
        {
            return success;
        }

    }

    public static class OrganizationRoleRest
    {
        private final OrganizationRole role;

        private OrganizationRoleRest(OrganizationRole role)
        {
            this.role = role;
        }

        public String getName()
        {
            return role.getId().getRole();
        }
    }

}
