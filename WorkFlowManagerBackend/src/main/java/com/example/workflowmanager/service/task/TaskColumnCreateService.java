package com.example.workflowmanager.service.task;

import com.example.workflowmanager.db.organization.project.ProjectRepository;
import com.example.workflowmanager.db.organization.project.task.TaskColumnRepository;
import com.example.workflowmanager.entity.organization.project.Project;
import com.example.workflowmanager.entity.organization.project.task.TaskColumn;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
public class TaskColumnCreateService
{
    private final TaskColumnRepository taskColumnRepository;
    private final ProjectRepository projectRepository;

    public TaskColumnCreateService(
        final TaskColumnRepository taskColumnRepository,
        final ProjectRepository projectRepository)
    {
        this.taskColumnRepository = taskColumnRepository;
        this.projectRepository = projectRepository;
    }

    public ServiceResult<TaskColumnCreateError> create(
        final Long projectId, final String columnName)
    {
        final List<TaskColumn> columns = getColumns(projectId);
        if(isExists(columnName, columns))
        {
            return ServiceResult.error(TaskColumnCreateError.EXISTS);
        }
        final TaskColumn taskColumn = new TaskColumn();
        taskColumn.setName(columnName);
        taskColumn.setColumnOrder(getNewColumnOrder(columns));
        taskColumn.setTasks(new HashSet<>());
        final Project project = Iterables.getOnlyElement(
            projectRepository.getListByIdsWithOrganization(Collections.singleton(projectId)));
        taskColumn.setProject(project);
        taskColumnRepository.save(taskColumn);
        return ServiceResult.ok();
    }

    private static short getNewColumnOrder(final List<TaskColumn> columns)
    {
        return (short) (columns.stream()
            .mapToInt(TaskColumn::getColumnOrder)
            .max()
            .orElse(0) + 1);
    }

    private static boolean isExists(final String columnName,
        final List<TaskColumn> columns)
    {
        return columns.stream()
            .map(TaskColumn::getName)
            .anyMatch(colName -> StringUtils.equalsIgnoreCase(columnName, colName));
    }

    private List<TaskColumn> getColumns(final Long projectId)
    {
        return taskColumnRepository.getListByProjectIdsWithTaskMembers(
            Collections.singleton(projectId));
    }

    public enum TaskColumnCreateError
    {
        EXISTS,
    }

}
