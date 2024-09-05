package com.example.workflowmanager.db.organization.project.task;

import com.example.workflowmanager.entity.organization.project.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>
{
    @Query(
        "select " +
            "t " +
        "from " +
            "Task t " +
            "join fetch t.chat c " +
            "left join fetch t.subTasks " +
            "left join fetch t.parentTask " +
            "left join fetch t.members tm " +
            "left join fetch tm.member m " +
        "where " +
            "t.id in (?1)")
    List<Task> getListByIdsWithRelationalTasksAndMembers(Collection<Long> ids);

    @Query("select t from Task t where t.projectId in (?1)")
    List<Task> getListByProjectIds(Collection<Long> projectIds);

    @Query("select t from Task t where t.id in (?1)")
    List<Task> getList(Collection<Long> ids);


    @Query(
        "select distinct " +
            "t " +
        "from " +
            "Task t " +
            "join fetch t.members tm " +
            "left join fetch t.taskColumn c " +
            "join fetch t.project p " +
            "join fetch p.organization o " +
        "where " +
            "tm.id.userId in (?1)")
    List<Task> getListByMemberUserIdsWithMemberAndOrganization(final Collection<Long> userIds);

}
