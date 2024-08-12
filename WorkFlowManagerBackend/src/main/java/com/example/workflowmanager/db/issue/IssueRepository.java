package com.example.workflowmanager.db.issue;

import com.example.workflowmanager.entity.issue.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>
{
}
