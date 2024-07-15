package com.example.workflowmanager.db.organization.role;

import com.example.workflowmanager.entity.organization.role.OrganizationPermission;
import com.example.workflowmanager.entity.organization.role.OrganizationPermissionId;
import com.example.workflowmanager.entity.organization.role.OrganizationRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrganizationPermissionRepository extends JpaRepository<OrganizationPermission, OrganizationPermissionId>
{
    @Query("select op.id from OrganizationPermission op join op.role r where r.id in (?1)")
    List<OrganizationPermissionId> getListByRoleIds(Collection<OrganizationRoleId> roleIds);
}
