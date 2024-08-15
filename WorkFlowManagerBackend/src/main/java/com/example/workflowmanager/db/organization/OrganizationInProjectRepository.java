package com.example.workflowmanager.db.organization;

import com.example.workflowmanager.entity.organization.OrganizationInProject;
import com.example.workflowmanager.entity.organization.OrganizationInProjectId;
import com.example.workflowmanager.entity.organization.OrganizationInProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrganizationInProjectRepository extends JpaRepository<OrganizationInProject, OrganizationInProjectId>
{
    @Query("select oip.id from OrganizationInProject oip join oip.project p where p.id in (?1)")
    List<OrganizationInProjectId> getIdListByProjectIds(Collection<Long> projectIds);

    @Query("select oip from OrganizationInProject oip join oip.organization o where o.id in (?1) and oip.role in (?2)")
    List<OrganizationInProject> getListByOrganizationIds(Collection<Long> organizationIds, Collection<OrganizationInProjectRole> roles);
}
