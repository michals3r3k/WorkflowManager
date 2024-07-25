package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.db.organization.role.OrganizationPermissionRepository;
import com.example.workflowmanager.db.organization.role.OrganizationRoleRepository;
import com.example.workflowmanager.entity.organization.role.*;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/api/organization/role/add")
    public ResponseEntity<OrganizationRoleCreateServiceResult> addRole(@RequestBody OrganizationRoleRequest request)
    {
        // TODO: add service with validation
        OrganizationRoleId organizationRoleId = new OrganizationRoleId(
            request.getOrganizationId(), request.getRole());
        OrganizationRole organizationRole = new OrganizationRole(organizationRoleId);
        organizationRoleRepository.save(organizationRole);
        return ResponseEntity.ok(new OrganizationRoleCreateServiceResult(true));
    }

    @GetMapping("api/organization/{organizationId}/roles")
    public ResponseEntity<List<OrganizationRoleRest>> getOrganizationRoleList(@PathVariable Long organizationId) {
        List<OrganizationRoleRest> roles = organizationRoleRepository
            .getListByOrganization(Collections.singleton(organizationId)).stream()
            .map(OrganizationRoleRest::new)
            .sorted(Comparator.comparing(OrganizationRoleRest::getRole, Comparator.naturalOrder()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    @GetMapping("api/organization/{organizationId}/role/{role}/available")
    public ResponseEntity<Boolean> isRoleNameAvailable(@PathVariable Long organizationId, @PathVariable String role) {
        boolean isTaken = organizationRoleRepository
                .getListByOrganization(Collections.singleton(organizationId)).stream()
                .anyMatch(r -> r.getId().getRole().equalsIgnoreCase(role));
        return ResponseEntity.ok(!isTaken);
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
        private Long organizationId;
        private String role;

        public OrganizationRoleRequest(Long organizationId, String role)
        {
            this.organizationId = organizationId;
            this.role = role;
        }

        public Long getOrganizationId()
        {
            return organizationId;
        }

        public void setOrganizationId(Long organizationId)
        {
            this.organizationId = organizationId;
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
