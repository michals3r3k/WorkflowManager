package com.example.workflowmanager.service.task;

import com.example.workflowmanager.db.organization.project.task.TaskMemberRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRelationRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRepository;
import com.example.workflowmanager.entity.organization.project.task.*;
import com.example.workflowmanager.rest.organization.project.task.TaskController.TaskRelationRest;
import com.example.workflowmanager.rest.organization.project.task.TaskController.TaskRelationTypeRest;
import com.example.workflowmanager.rest.organization.project.task.TaskController.TaskRest;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskEditService
{
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_DATE_TIME;

    private final TaskRepository taskRepository;
    private final TaskMemberRepository taskMemberRepository;
    private final TaskRelationRepository taskRelationRepository;

    public TaskEditService(final TaskRepository taskRepository,
        final TaskMemberRepository taskMemberRepository,
        final TaskRelationRepository taskRelationRepository)
    {
        this.taskRepository = taskRepository;
        this.taskMemberRepository = taskMemberRepository;
        this.taskRelationRepository = taskRelationRepository;
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
        final Set<TaskEditError> errors = getTaskRelationErrors(taskRest, taskMap);
        if(!errors.isEmpty())
        {
            return new ServiceResult<>(errors);
        }
        task.setTitle(taskRest.getTitle());
        task.setDescription(taskRest.getDescriptionOrNull());
        task.setStartDate(getLocalDateOrNull(taskRest.getStartDateOrNull()));
        task.setFinishDate(getLocalDateOrNull(taskRest.getFinishDateOrNull()));
        task.setDeadlineDate(getLocalDateOrNull(taskRest.getDeadlineDateOrNull()));
        updateMembers(taskRest, task);
        updateTaskRelations(taskRest, task);
        taskRepository.save(task);
        return ServiceResult.ok();
    }

    private static boolean isDuplicatedName(final TaskRest taskRest,
        final Collection<Task> tasks)
    {
        return tasks.stream().anyMatch(t -> !t.getId().equals(taskRest.getTaskId())
            && StringUtils.equalsIgnoreCase(t.getTitle(), taskRest.getTitle()));
    }

    private Set<TaskEditError> getTaskRelationErrors(final TaskRest taskRest, final Map<Long, Task> taskMap)
    {
        final Set<TaskEditError> errors = EnumSet.noneOf(TaskEditError.class);
        if(isConnectedTaskDoesntExists(taskMap, taskRest))
        {
            errors.add(TaskEditError.CONNECTED_TASK_DOESNT_EXISTS);
        }
        if(isDuplicatedRelation(taskRest))
        {
            errors.add(TaskEditError.DUPLICATED_RELATION);
        }
        return errors;
    }

    private static boolean isConnectedTaskDoesntExists(
        final Map<Long, Task> taskMap, final TaskRest task)
    {
        return task.getTaskRelations().stream()
            .map(TaskRelationRest::getTaskId)
            .map(taskMap::get)
            .anyMatch(Objects::isNull);
    }

    private static boolean isDuplicatedRelation(final TaskRest taskRest)
    {
        return taskRest.getTaskRelations().size() != taskRest
            .getTaskRelations().stream()
            .map(TaskRelationRest::getTaskId)
            .collect(Collectors.toSet())
            .size();
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

    private void updateTaskRelations(final TaskRest taskRest, final Task task)
    {
        taskRelationRepository.deleteAll(taskRelationRepository.getListByTaskIds(Collections.singleton(task.getId())));
        final List<TaskRelation> taskRelations = taskRest.getTaskRelations().stream()
            .map(taskRelationRest -> getTaskRelationId(task.getId(), taskRelationRest))
            .map(id -> new TaskRelation(id, task.getOrganizationId(), task.getProjectId()))
            .collect(Collectors.toList());
        taskRelationRepository.saveAll(taskRelations);
    }

    private TaskRelationId getTaskRelationId(Long taskId, TaskRelationRest taskRelationRest)
    {
        final TaskRelationTypeRest relationType = taskRelationRest.getRelationType();
        if(relationType.isReversed())
        {
            return new TaskRelationId(taskRelationRest.getTaskId(), taskId, relationType.getType());
        }
        return new TaskRelationId(taskId, taskRelationRest.getTaskId(), relationType.getType());
    }

    private static LocalDateTime getLocalDateOrNull(String dateOrNull)
    {
        return ObjectUtils.accessNullable(dateOrNull,
            date -> LocalDateTime.parse(date, DTF));
    }

    public enum TaskEditError
    {
        TASK_DOESNT_EXISTS,
        DUPLICATED_TITLE,
        DUPLICATED_RELATION,
        CONNECTED_TASK_DOESNT_EXISTS;

    }

}
