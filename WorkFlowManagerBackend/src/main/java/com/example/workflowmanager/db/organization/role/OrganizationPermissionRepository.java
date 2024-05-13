package com.example.workflowmanager.db.organization.role;

import com.example.workflowmanager.entity.organization.role.OrganizationPermission;
import com.example.workflowmanager.entity.organization.role.OrganizationPermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationPermissionRepository extends JpaRepository<OrganizationPermission, OrganizationPermissionId>
{

}
