package com.example.workflowmanager.db.organization.role;

import com.example.workflowmanager.entity.organization.role.OrganizationRole;
import com.example.workflowmanager.entity.organization.role.OrganizationRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRoleRepository extends JpaRepository<OrganizationRole, OrganizationRoleId>
{

}
