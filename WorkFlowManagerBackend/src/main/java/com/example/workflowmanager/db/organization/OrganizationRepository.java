package com.example.workflowmanager.db.organization;

import com.example.workflowmanager.entity.organization.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long>
{

}
