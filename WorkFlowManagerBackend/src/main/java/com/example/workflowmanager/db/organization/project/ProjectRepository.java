package com.example.workflowmanager.db.organization.project;

import com.example.workflowmanager.entity.organization.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>
{

}
