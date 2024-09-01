package com.example.workflowmanager.service.task;

import com.example.workflowmanager.db.organization.project.task.TaskMemberRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRepository;
import com.example.workflowmanager.entity.organization.project.task.Task;
import com.example.workflowmanager.entity.organization.project.task.TaskMember;
import com.example.workflowmanager.entity.organization.project.task.TaskMemberId;
import com.example.workflowmanager.rest.organization.project.task.TaskController.TaskRest;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskEditService
{
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_DATE_TIME;

    private final TaskRepository taskRepository;
    private final TaskMemberRepository taskMemberRepository;

    public TaskEditService(final TaskRepository taskRepository,
        final TaskMemberRepository taskMemberRepository)
    {
        this.taskRepository = taskRepository;
        this.taskMemberRepository = taskMemberRepository;
    }

    public ServiceResult<TaskEditError> save(final Long projectId, final TaskRest taskRest)
    {
        final Map<Long, Task> taskMap = Maps.uniqueIndex(taskRepository
            .getListByProjectIds(Collections.singleton(projectId)), Task::getId);
        final Task task = taskMap.get(taskRest.getTaskId());
        if(task == null)
        {
            return ServiceResult.error(TaskEditError.TASK_DOESNT_EXISTS);
        }
        final boolean duplicatedName = isDuplicatedName(taskRest, taskMap.values());
        if(duplicatedName)
        {
            return ServiceResult.error(TaskEditError.DUPLICATED_TITLE);
        }
        task.setTitle(taskRest.getTitle());
        task.setDescription(taskRest.getDescriptionOrNull());
        task.setStartDate(getLocalDateOrNull(taskRest.getStartDateOrNull()));
        task.setFinishDate(getLocalDateOrNull(taskRest.getFinishDateOrNull()));
        task.setDeadlineDate(getLocalDateOrNull(taskRest.getDeadlineDateOrNull()));
        updateMembers(taskRest, task);
//        task.setSubTasks();
        taskRepository.save(task);
        return ServiceResult.ok();
    }

    private static boolean isDuplicatedName(final TaskRest taskRest,
        final Collection<Task> tasks)
    {
        return tasks.stream().anyMatch(t -> !t.getId().equals(taskRest.getTaskId())
            && StringUtils.equalsIgnoreCase(t.getTitle(), taskRest.getTitle()));
    }

    private void updateMembers(final TaskRest taskRest, final Task task)
    {
        taskMemberRepository.deleteAll(task.getMembers());
        final Set<TaskMember> members = getMembers(taskRest, task);
        taskMemberRepository.saveAll(members);
        task.setMembers(members);
    }

    private static Set<TaskMember> getMembers(final TaskRest taskRest, final Task task)
    {
        return taskRest.getMembers().stream()
            .map(member -> new TaskMemberId(member.getUserId(),
                task.getOrganizationId(), task.getId()))
            .map(TaskMember::new)
            .collect(Collectors.toSet());
    }

    private static LocalDateTime getLocalDateOrNull(String dateOrNull)
    {
        return ObjectUtils.accessNullable(dateOrNull,
            date -> LocalDateTime.parse(date, DTF));
    }

    public enum TaskEditError
    {
        TASK_DOESNT_EXISTS,
        DUPLICATED_TITLE;

    }

}
