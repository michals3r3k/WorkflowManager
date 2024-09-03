package com.example.workflowmanager.db.organization.project.task;

import com.example.workflowmanager.entity.organization.project.task.TaskMember;
import com.example.workflowmanager.entity.organization.project.task.TaskMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskMemberRepository extends JpaRepository<TaskMember, TaskMemberId>
{

}
