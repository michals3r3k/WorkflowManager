package com.example.workflowmanager.db.organization.role;

import com.example.workflowmanager.entity.organization.OrganizationMemberId;
import com.example.workflowmanager.entity.organization.role.OrganizationMemberRole;
import com.example.workflowmanager.entity.organization.role.OrganizationMemberRoleId;
import com.example.workflowmanager.entity.organization.role.OrganizationRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface OrganizationMemberRoleRepository extends JpaRepository<OrganizationMemberRole, OrganizationMemberRoleId>
{
    @Query(
        "select " +
            "omr.id " +
        "from " +
            "OrganizationMemberRole omr " +
            "join omr.role r " +
            "join omr.member m " +
        "where " +
            "m.id in (?1)")
    Set<OrganizationRoleId> getIdListByOrganizationMemberIds(Collection<OrganizationMemberId> organizationMemberIds);
}
