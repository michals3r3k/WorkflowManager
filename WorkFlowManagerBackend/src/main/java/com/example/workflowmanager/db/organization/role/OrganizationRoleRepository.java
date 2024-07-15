package com.example.workflowmanager.db.organization.role;

import com.example.workflowmanager.entity.organization.role.OrganizationRole;
import com.example.workflowmanager.entity.organization.role.OrganizationRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrganizationRoleRepository extends JpaRepository<OrganizationRole, OrganizationRoleId>
{
    @Query("select or from OrganizationRole or where or.id.organizationId in (?1)")
    List<OrganizationRole> getListByOrganization(Collection<Long> organizationIds);

}
