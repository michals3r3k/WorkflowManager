package com.example.workflowmanager.db.issue;

import com.example.workflowmanager.entity.issue.IssueField;
import com.example.workflowmanager.entity.issue.IssueFieldId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueFieldRepository extends JpaRepository<IssueField, IssueFieldId>
{

}
