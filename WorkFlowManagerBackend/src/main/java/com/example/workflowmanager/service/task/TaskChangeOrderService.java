package com.example.workflowmanager.service.task;

import com.example.workflowmanager.db.organization.project.task.TaskColumnRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRepository;
import com.example.workflowmanager.entity.organization.project.task.Task;
import com.example.workflowmanager.entity.organization.project.task.TaskColumn;
import com.example.workflowmanager.rest.organization.project.task.TaskColumnController.TaskOrderRest;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskChangeOrderService
{
    private final TaskRepository taskRepository;
    private final TaskColumnRepository taskColumnRepository;

    public TaskChangeOrderService(final TaskRepository taskRepository,
        final TaskColumnRepository taskColumnRepository)
    {
        this.taskRepository = taskRepository;
        this.taskColumnRepository = taskColumnRepository;
    }

    public ServiceResult<TaskChangeOrderError> changeOrder(final Long projectId,
        final List<TaskOrderRest> taskOrders)
    {
        final Map<Long, Task> taskMap = getTaskMap(projectId);
        final Map<Long, TaskColumn> taskColumnMap = getTaskColumnMap(projectId);
        final Set<Long> taskIds = getTaskIds(taskOrders);
        final Set<Long> columnIds = getColumnIds(taskOrders);
        final Set<TaskChangeOrderError> errors = EnumSet.noneOf(TaskChangeOrderError.class);
        if(!taskMap.keySet().equals(taskIds))
        {
            errors.add(TaskChangeOrderError.TASKS_OUT_OF_DATE);
        }
        if(!taskColumnMap.keySet().containsAll(columnIds))
        {
            errors.add(TaskChangeOrderError.COLUMNS_OUT_OF_DATE);
        }
        if(!errors.isEmpty())
        {
            return new ServiceResult<>(errors);
        }
        final Set<Task> tasksToSave = new HashSet<>();
        taskOrders.stream()
            .sorted(Comparator.comparing(TaskOrderRest::getOrder))
            .collect(Collectors.groupingBy(taskOrder -> Optional.ofNullable(taskOrder.getTaskColumnId())))
            .values()
            .forEach(tasks -> {
                for(int i = 0; i < tasks.size(); ++i)
                {
                    final TaskOrderRest taskOrderRest = tasks.get(i);
                    final Task task = taskMap.get(taskOrderRest.getTaskId());
                    task.setTaskColumnId(taskOrderRest.getTaskColumnId());
                    task.setTaskOrder((short) i);
                    tasksToSave.add(task);
                }
            });
        taskRepository.saveAll(tasksToSave);
        return ServiceResult.ok();
    }

    private Map<Long, Task> getTaskMap(final Long projectId)
    {
        return Maps.uniqueIndex(
            taskRepository.getListByProjectIds(Collections.singleton(projectId)),
            Task::getId);
    }

    private Map<Long, TaskColumn> getTaskColumnMap(
        final Long projectId)
    {
        return Maps.uniqueIndex(
            taskColumnRepository.getListByProjectIds(Collections.singleton(projectId)),
            TaskColumn::getId);
    }

    private static Set<Long> getTaskIds(final List<TaskOrderRest> taskOrders)
    {
        return taskOrders.stream()
            .map(TaskOrderRest::getTaskId)
            .collect(Collectors.toSet());
    }

    private static Set<Long> getColumnIds(final List<TaskOrderRest> taskOrders)
    {
        return taskOrders.stream()
            .map(TaskOrderRest::getTaskColumnId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    public enum TaskChangeOrderError
    {
        TASKS_OUT_OF_DATE,
        COLUMNS_OUT_OF_DATE
    }

}
