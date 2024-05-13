package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.db.organization.role.OrganizationPermissionRepository;
import com.example.workflowmanager.db.organization.role.OrganizationRoleRepository;
import com.example.workflowmanager.entity.organization.role.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class OrganizationRoleController
{
    private final OrganizationRoleRepository organizationRoleRepository;
    private final OrganizationPermissionRepository organizationPermissionRepository;

    public OrganizationRoleController(
        OrganizationRoleRepository organizationRoleRepository,
        OrganizationPermissionRepository organizationPermissionRepository)
    {
        this.organizationRoleRepository = organizationRoleRepository;
        this.organizationPermissionRepository = organizationPermissionRepository;
    }

    @PostMapping("/api/organization/role/add")
    public ResponseEntity<OrganizationRoleCreateServiceResult> addRole(@RequestBody OrganizationRoleRequest request)
    {
        // TODO: add service with validation
        OrganizationRoleId organizationRoleId = new OrganizationRoleId(
            request.getOrganizationId(), request.getRole());
        OrganizationRole organizationRole = new OrganizationRole(organizationRoleId);
        organizationRoleRepository.save(organizationRole);
        request.getPermissions().stream()
            .map(Permission::valueOf)
            .map(permission -> new OrganizationPermissionId(permission, organizationRoleId))
            .map(OrganizationPermission::new)
            .forEachOrdered(organizationPermissionRepository::save);
        return ResponseEntity.ok(new OrganizationRoleCreateServiceResult(true));
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
        private List<String> permissions;

        public OrganizationRoleRequest(Long organizationId, String role,
            List<String> permissions)
        {
            this.organizationId = organizationId;
            this.role = role;
            this.permissions = permissions;
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

        public List<String> getPermissions()
        {
            return permissions;
        }

        public void setPermissions(List<String> permissions)
        {
            this.permissions = permissions;
        }

    }

}
