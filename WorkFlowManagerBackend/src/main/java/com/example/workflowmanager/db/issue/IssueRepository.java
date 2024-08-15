package com.example.workflowmanager.db.issue;

import com.example.workflowmanager.entity.issue.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>
{
    @Query("select i from Issue i join fetch i.organization d join fetch i.sourceOrganization s where i.project is null and (d.id in (?1) or s.id in (?1))")
    List<Issue> getAllOrganizationIssues(Collection<Long> organizationIds);
}
