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
    @Query("select i from Issue i join fetch i.organization d where d.id in (?1)")
    List<Issue> getAllByOrganizationIds(Collection<Long> organizationIds);

    @Query("select i from Issue i join fetch i.organization d join fetch i.sourceOrganization s left join fetch i.project p where p is null and (d.id in (?1) or s.id in (?1))")
    List<Issue> getOrganizationIssues(Collection<Long> organizationIds);

    @Query("select i from Issue i join fetch i.organization d join fetch i.sourceOrganization s join fetch i.project p where p.id in (?1)")
    List<Issue> getProjectIssues(Collection<Long> projectIds);
    
    @Query("select i from Issue i join fetch i.organization d join fetch i.sourceOrganization s join fetch i.project p where p.id in (?2) and s.id in (?1)")
    List<Issue> getProjectIssues(Collection<Long> sourceOrganizationIds, Collection<Long> projectIds);

}
