package com.example.workflowmanager.db.issue;

import com.example.workflowmanager.entity.issue.IssueCategory;
import com.example.workflowmanager.entity.issue.IssueCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface IssueCategoryRepository extends JpaRepository<IssueCategory, IssueCategoryId>
{
    @Query("select ic from IssueCategory ic where ic.id.organizationId in (?1)")
    List<IssueCategory> getListByOrganizationIds(Collection<Long> organizationIds);

}
