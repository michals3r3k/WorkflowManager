package com.example.workflowmanager.db.issue;

import com.example.workflowmanager.entity.issue.IssueFieldDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface IssueFieldDefinitionRepository extends JpaRepository<IssueFieldDefinition, Long>
{
    @Query("select ifd from IssueFieldDefinition ifd where ifd.organizationId in (?1)")
    List<IssueFieldDefinition> getListByOrganizationId(Collection<Long> organizationIds);
    @Query("select distinct ifd.organization.id from IssueFieldDefinition ifd")
    Set<Long> getOrganizationIdsWithFieldDefinitions();
}
