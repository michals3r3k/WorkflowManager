package com.example.workflowmanager.db.organization.project.task;

import com.example.workflowmanager.entity.organization.project.task.TaskColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TaskColumnRepository extends JpaRepository<TaskColumn, Long>
{
    @Query(
        "select " +
            "tc " +
        "from " +
            "TaskColumn tc " +
            "join tc.project p " +
            "join fetch tc.tasks t " +
            "join fetch t.members tm " +
            "join fetch tm.member om " +
            "join fetch om.user " +
        "where " +
            "p.id in (?1)")
    List<TaskColumn> getListByProjectIdsWithTaskMembers(Collection<Long> projectIds);

}
