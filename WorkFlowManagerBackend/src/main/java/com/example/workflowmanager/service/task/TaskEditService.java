package com.example.workflowmanager.service.task;

import com.example.workflowmanager.db.organization.project.task.TaskMemberRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRelationRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRepository;
import com.example.workflowmanager.entity.organization.project.task.*;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.rest.organization.project.task.TaskRestFactory.SubTaskRest;
import com.example.workflowmanager.rest.organization.project.task.TaskRestFactory.TaskRelationRest;
import com.example.workflowmanager.rest.organization.project.task.TaskRestFactory.TaskRelationTypeRest;
import com.example.workflowmanager.rest.organization.project.task.TaskRestFactory.TaskRest;
import com.example.workflowmanager.service.task.TaskCreateService.TaskCreateServiceResult;
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
    private final TaskCreateService taskCreateService;

    public TaskEditService(final TaskRepository taskRepository,
        final TaskMemberRepository taskMemberRepository,
        final TaskRelationRepository taskRelationRepository,
        final TaskCreateService taskCreateService)
    {
        this.taskRepository = taskRepository;
        this.taskMemberRepository = taskMemberRepository;
        this.taskRelationRepository = taskRelationRepository;
        this.taskCreateService = taskCreateService;
    }

    public TaskEditServiceResult save(final Long organizationId,
        final Long projectId, final TaskRest taskRest, final User subTaskCreator)
    {
        final Map<Long, Task> taskMap = Maps.uniqueIndex(taskRepository
            .getListByProjectIds(Collections.singleton(projectId)), Task::getId);
        final Task task = taskMap.get(taskRest.getTaskId());
        final Set<TaskEditError> errors = EnumSet.noneOf(TaskEditError.class);
        if(task == null)
        {
            errors.add(TaskEditError.TASK_DOESNT_EXISTS);
            return new TaskEditServiceResult(taskRest, errors);
        }
        final boolean duplicatedName = isDuplicatedName(taskRest, taskMap.values());
        if(duplicatedName)
        {
            errors.add(TaskEditError.DUPLICATED_TITLE);
            return new TaskEditServiceResult(taskRest, errors);
        }
        errors.addAll(getSubTaskErrors(taskRest, task));
        errors.addAll(getTaskRelationErrors(taskRest, taskMap));
        if(!errors.isEmpty())
        {
            return new TaskEditServiceResult(taskRest, errors);
        }
        task.setTitle(taskRest.getTitle());
        task.setDescription(taskRest.getDescriptionOrNull());
        task.setStartDate(getLocalDateOrNull(taskRest.getStartDateOrNull()));
        task.setFinishDate(getLocalDateOrNull(taskRest.getFinishDateOrNull()));
        task.setDeadlineDate(getLocalDateOrNull(taskRest.getDeadlineDateOrNull()));
        task.setPriority(taskRest.getPriority());
        taskRepository.save(task);
        updateSubTasks(taskRest, task, subTaskCreator, organizationId, projectId);
        updateMembers(taskRest, task);
        updateTaskRelations(taskRest, task);
        return new TaskEditServiceResult(taskRest, errors);
    }

    private void updateSubTasks(final TaskRest taskRest, final Task task,
        final User subTaskCreator, Long organizationId, final Long projectId)
    {
        final Set<Long> notDeletedSubTaskIds = taskRest.getSubTasks().stream()
            .map(SubTaskRest::getSubTaskId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        final Set<Task> subTasksToDelete = task.getSubTasks().stream()
            .filter(subTask -> !notDeletedSubTaskIds.contains(subTask.getId()))
            .collect(Collectors.toSet());
        taskRepository.deleteAll(subTasksToDelete);
        for(final SubTaskRest subTaskRest : taskRest.getSubTasks())
        {
            if(subTaskRest.getSubTaskId() == null)
            {
                final Long columnId = ObjectUtils.accessNullable(task.getTaskColumn(), TaskColumn::getId);
                final TaskCreateServiceResult taskCreateServiceResult = taskCreateService.create(
                    organizationId, projectId, subTaskRest.getTitle(), null, columnId, subTaskCreator);
                if(!taskCreateServiceResult.isSuccess())
                {
                    throw new IllegalStateException("Unknown error: " + taskCreateServiceResult.getErrors());
                }
                final Task subTask = taskCreateServiceResult.getTaskOrNull();
                subTask.setParentTaskId(task.getId());
                taskRepository.save(subTask);
                subTaskRest.setSubTaskId(subTask.getId());
            }
        }
    }

    private static boolean isDuplicatedName(final TaskRest taskRest,
        final Collection<Task> tasks)
    {
        return tasks.stream().anyMatch(t -> !t.getId().equals(taskRest.getTaskId())
            && StringUtils.equalsIgnoreCase(t.getTitle(), taskRest.getTitle()));
    }

    private static Set<TaskEditError> getSubTaskErrors(final TaskRest taskRest, final Task task)
    {
        final Set<TaskEditError> errors = EnumSet.noneOf(TaskEditError.class);
        if(task.getParentTaskId() != null)
        {
            if(!taskRest.getSubTasks().isEmpty())
            {
                errors.add(TaskEditError.SUBTASK_WITH_SUBTASKS);
            }
            if(!taskRest.getTaskRelations().isEmpty())
            {
                errors.add(TaskEditError.SUBTASK_WITH_RELATION);
            }
        }
        return errors;
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

    public static class TaskEditServiceResult extends ServiceResult<TaskEditError>
    {
        private final TaskRest taskRest;

        public TaskEditServiceResult(final TaskRest taskRest, final Collection<TaskEditError> errors)
        {
            super(errors);
            this.taskRest = taskRest;
        }

        public TaskRest getTaskRest()
        {
            return taskRest;
        }

    }

    public enum TaskEditError
    {
        TASK_DOESNT_EXISTS,
        DUPLICATED_TITLE,
        DUPLICATED_RELATION,
        CONNECTED_TASK_DOESNT_EXISTS,
        SUBTASK_WITH_SUBTASKS,
        SUBTASK_WITH_RELATION
    }

}
