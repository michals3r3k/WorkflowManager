package com.example.workflowmanager.db.issue;

import com.example.workflowmanager.entity.issue.IssueField;
import com.example.workflowmanager.entity.issue.IssueFieldId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface IssueFieldRepository extends JpaRepository<IssueField, IssueFieldId>
{
    @Query("select if from IssueField if where if.id in (?1)")
    List<IssueField> getList(final Collection<IssueFieldId> ids);

}
