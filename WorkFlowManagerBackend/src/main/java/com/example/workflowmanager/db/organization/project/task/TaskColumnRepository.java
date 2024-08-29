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
            "left join fetch tc.tasks t " +
            "left join fetch t.chat c " +
            "left join fetch t.members tm " +
            "left join fetch tm.member om " +
            "left join fetch om.user " +
        "where " +
            "p.id in (?1)")
    List<TaskColumn> getListByProjectIdsWithTaskMembers(Collection<Long> projectIds);

    @Query(
        "select " +
            "tc " +
        "from " +
            "TaskColumn tc " +
            "join tc.project p " +
        "where " +
            "p.id in (?1)")
    List<TaskColumn> getListByProjectIds(Collection<Long> projectIds);

    @Query("select tc from TaskColumn tc where tc.id in (?1)")
    List<TaskColumn> getListByIds(Collection<Long> taskColumnIds);

}
