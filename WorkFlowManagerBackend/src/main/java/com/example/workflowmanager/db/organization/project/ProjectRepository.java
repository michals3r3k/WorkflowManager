package com.example.workflowmanager.db.organization.project;

import com.example.workflowmanager.entity.organization.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>
{
    @Query("select p from Project p join p.organization o where o.id in (?1)")
    List<Project> getListByOrganizationIds(Collection<Long> organizationIds);

}
