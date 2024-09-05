package com.example.workflowmanager.db.issue;

import com.example.workflowmanager.entity.issue.IssueStatus;
import com.example.workflowmanager.entity.issue.IssueStatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface IssueStatusRepository extends JpaRepository<IssueStatus, IssueStatusId>
{
    @Query("select is from IssueStatus is where is.id.organizationId in (?1)")
    List<IssueStatus> getListByOrganizationIds(Collection<Long> organizationIds);

}
