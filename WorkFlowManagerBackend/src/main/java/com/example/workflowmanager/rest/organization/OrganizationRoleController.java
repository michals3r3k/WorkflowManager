package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.db.organization.role.OrganizationRoleRepository;
import com.example.workflowmanager.entity.organization.role.OrganizationRole;
import com.example.workflowmanager.entity.organization.role.OrganizationRoleId;
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
    private final OrganizationRoleRepository organizationRoleRepository;

    public OrganizationRoleController(
        OrganizationRoleRepository organizationRoleRepository)
    {
        this.organizationRoleRepository = organizationRoleRepository;
    }

    @PostMapping("/api/organization/{organizationId}/role/add")
    @PreAuthorize("hasAuthority('ROLE_C')")
    public ResponseEntity<OrganizationRoleCreateServiceResult> addRole(
        @PathVariable Long organizationId, @RequestBody OrganizationRoleRequest request)
    {
        // TODO: add service with validation
        OrganizationRoleId organizationRoleId = new OrganizationRoleId(
            organizationId, request.getRole());
        OrganizationRole organizationRole = new OrganizationRole(organizationRoleId);
        organizationRoleRepository.save(organizationRole);
        return ResponseEntity.ok(new OrganizationRoleCreateServiceResult(true));
    }

    @GetMapping("/api/organization/{organizationId}/role/list")
    @PreAuthorize("hasAuthority('ROLE_R')")
    public ResponseEntity<List<OrganizationRoleRest>> getOrganizationRoleList(@PathVariable Long organizationId) {
        List<OrganizationRoleRest> roles = organizationRoleRepository
            .getListByOrganization(Collections.singleton(organizationId)).stream()
            .map(OrganizationRoleRest::new)
            .sorted(Comparator.comparing(OrganizationRoleRest::getRole, Comparator.naturalOrder()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
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

    public static class OrganizationRoleRequest
    {
        private String role;

        public OrganizationRoleRequest()
        {
            // for spring
        }

        public OrganizationRoleRequest(String role)
        {
            this.role = role;
        }

        public String getRole()
        {
            return role;
        }

        public void setRole(String role)
        {
            this.role = role;
        }
    }

    public static class OrganizationRoleRest
    {
        private final OrganizationRole role;

        private OrganizationRoleRest(OrganizationRole role)
        {
            this.role = role;
        }

        public String getRole()
        {
            return this.role.getId().getRole();
        }
    }
}
