package com.example.workflowmanager.service.task;

import com.example.workflowmanager.db.organization.project.task.TaskColumnRepository;
import com.example.workflowmanager.entity.organization.project.task.TaskColumn;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;

@Service
public class TaskColumnDeleteService
{
    private final TaskColumnRepository taskColumnRepository;
    private final TaskColumnChangeOrderService changeOrderService;

    public TaskColumnDeleteService(
        final TaskColumnRepository taskColumnRepository,
        final TaskColumnChangeOrderService changeOrderService)
    {
        this.taskColumnRepository = taskColumnRepository;
        this.changeOrderService = changeOrderService;
    }

    public ServiceResult<TaskColumnDeleteError> delete(final Long projectId, final Long taskColumnId)
    {
        final Map<Long, TaskColumn> taskColumnMap = Maps.uniqueIndex(taskColumnRepository
            .getListByProjectIds(Collections.singleton(projectId)), TaskColumn::getId);
        final TaskColumn taskColumnOrNull = taskColumnMap.get(taskColumnId);
        final EnumSet<TaskColumnDeleteError> errors = EnumSet.noneOf(TaskColumnDeleteError.class);
        if(taskColumnOrNull != null)
        {
            if(!taskColumnOrNull.getTasks().isEmpty())
            {
                errors.add(TaskColumnDeleteError.HAS_TASKS);
            }
            if(taskColumnMap.size() == 1)
            {
                errors.add(TaskColumnDeleteError.ONLY_COLUMN);
            }
            if(errors.isEmpty())
            {
                taskColumnRepository.delete(taskColumnOrNull);
            }
        }
        if(!errors.isEmpty())
        {
            return new ServiceResult<>(errors);
        }
        changeOrderService.changeOrder(projectId);
        return ServiceResult.ok();
    }

    public enum TaskColumnDeleteError
    {
        HAS_TASKS,
        ONLY_COLUMN;
    }

}
