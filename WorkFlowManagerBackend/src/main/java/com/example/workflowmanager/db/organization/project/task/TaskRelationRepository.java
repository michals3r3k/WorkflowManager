package com.example.workflowmanager.db.organization.project.task;

import com.example.workflowmanager.entity.organization.project.task.TaskRelation;
import com.example.workflowmanager.entity.organization.project.task.TaskRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TaskRelationRepository extends JpaRepository<TaskRelation, TaskRelationId>
{
    @Query("select tr from TaskRelation tr where tr.sourceTask.id in (?1) or tr.targetTask.id in (?1)")
    List<TaskRelation> getListByTaskIds(Collection<Long> taskIds);

}
