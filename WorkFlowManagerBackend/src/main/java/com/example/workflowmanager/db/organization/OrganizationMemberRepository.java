package com.example.workflowmanager.db.organization;

import com.example.workflowmanager.entity.organization.OrganizationMember;
import com.example.workflowmanager.entity.organization.OrganizationMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, OrganizationMemberId>
{
    @Query("select om from OrganizationMember om where om.id.organizationId in (?1)")
    List<OrganizationMember> getListByOrganization(Collection<Long> organizationIds);
}
