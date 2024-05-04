package com.example.workflowmanager.db.user.db.organization;

import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long>
{

}
