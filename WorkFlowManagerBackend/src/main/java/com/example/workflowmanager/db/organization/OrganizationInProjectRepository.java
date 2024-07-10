package com.example.workflowmanager.db.organization;

import com.example.workflowmanager.entity.organization.OrganizationInProject;
import com.example.workflowmanager.entity.organization.OrganizationInProjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationInProjectRepository extends JpaRepository<OrganizationInProject, OrganizationInProjectId>
{

}
