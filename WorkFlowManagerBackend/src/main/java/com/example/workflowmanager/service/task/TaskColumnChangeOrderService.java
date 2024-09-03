package com.example.workflowmanager.service.task;

import com.example.workflowmanager.db.organization.project.task.TaskColumnRepository;
import com.example.workflowmanager.entity.organization.project.task.TaskColumn;
import com.example.workflowmanager.rest.organization.project.task.TaskColumnController.TaskColumnOrderRest;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.mutable.MutableShort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskColumnChangeOrderService
{
    private final TaskColumnRepository taskColumnRepository;

    public TaskColumnChangeOrderService(
        final TaskColumnRepository taskColumnRepository)
    {
        this.taskColumnRepository = taskColumnRepository;
    }
    
    void changeOrder(Long projectId)
    {
        final Map<Long, TaskColumn> existingColumnMap =
            getExistingColumnMap(projectId);
        final List<TaskColumnOrderRest> taskColumnOrders =
            getTaskColumnOrders(existingColumnMap.values());
        final ServiceResult<TaskColumnChangeOrderError> result =
            changeOrder(taskColumnOrders, existingColumnMap);
        if(!result.isSuccess())
        {
            throw new IllegalStateException("Unexpected errros while reordering " + result.getErrors());
        }
    }

    private static List<TaskColumnOrderRest> getTaskColumnOrders(
        final Collection<TaskColumn> columns)
    {
        final MutableShort mutableShort = new MutableShort(0);
        return columns.stream()
            .sorted(Comparator.comparing(TaskColumn::getColumnOrder)
                .thenComparing(TaskColumn::getId))
            .map(column -> new TaskColumnOrderRest(column.getId(), mutableShort.getAndIncrement()))
            .collect(Collectors.toList());
    }

    public ServiceResult<TaskColumnChangeOrderError> changeOrder(final Long projectId,
        final List<TaskColumnOrderRest> taskColumnOrders)
    {
        final Map<Long, TaskColumn> existingColumnMap = getExistingColumnMap(projectId);
        return changeOrder(taskColumnOrders, existingColumnMap);
    }

    private Map<Long, TaskColumn> getExistingColumnMap(final Long projectId)
    {
        return Maps.uniqueIndex(taskColumnRepository.getListByProjectIds(
            Collections.singleton(projectId)), TaskColumn::getId);
    }

    private ServiceResult<TaskColumnChangeOrderError> changeOrder(
        final List<TaskColumnOrderRest> taskColumnOrders,
        final Map<Long, TaskColumn> existingColumnMap)
    {
        final Set<Long> columnIds = taskColumnOrders.stream()
            .map(TaskColumnOrderRest::getTaskColumnId)
            .collect(Collectors.toSet());
        if(!existingColumnMap.keySet().equals(columnIds))
        {
            return ServiceResult.error(TaskColumnChangeOrderError.OUT_OF_DATE);
        }
        for(final TaskColumnOrderRest taskColumnOrder : taskColumnOrders)
        {
            final TaskColumn taskColumn =
                existingColumnMap.get(taskColumnOrder.getTaskColumnId());
            taskColumn.setColumnOrder(taskColumnOrder.getOrder());
            taskColumnRepository.save(taskColumn);
        }
        return ServiceResult.ok();
    }

    public enum TaskColumnChangeOrderError
    {
        OUT_OF_DATE;
    }

}
