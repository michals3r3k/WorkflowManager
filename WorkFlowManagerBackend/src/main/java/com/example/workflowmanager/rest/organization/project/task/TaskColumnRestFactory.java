package com.example.workflowmanager.rest.organization.project.task;

import com.example.workflowmanager.db.organization.project.task.TaskColumnRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRepository;
import com.example.workflowmanager.entity.organization.project.task.Task;
import com.example.workflowmanager.entity.organization.project.task.TaskColumn;
import com.example.workflowmanager.entity.organization.project.task.TaskPriority;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.rest.organization.project.task.TaskRestFactory.TaskMemberRest;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TaskColumnRestFactory
{
    private final TaskColumnRepository taskColumnRepository;
    private final TaskRepository taskRepository;

    public TaskColumnRestFactory(
        final TaskColumnRepository taskColumnRepository,
        final TaskRepository taskRepository)
    {
        this.taskColumnRepository = taskColumnRepository;
        this.taskRepository = taskRepository;
    }

    public List<TaskRest> getTaskListByIssueId(final Long issueId)
    {
        return getTasksRest(taskRepository.getListByIssueIds(Collections.singleton(issueId)));
    }

    public List<TaskColumnRest> getTaskColumnListByProjectId(final Long projectId)
    {
        final Multimap<Optional<Long>, Task> columnTasksMap = Multimaps.index(
            taskRepository.getListByProjectIds(Collections.singleton(projectId)),
            task -> Optional.ofNullable(task.getTaskColumnId()));
        return Stream.concat(
            Stream.of(new TaskColumnRest(null, "", getTasksRest(columnTasksMap.get(Optional.empty())))),
            taskColumnRepository.getListByProjectIdsWithTaskMembers(
                Collections.singleton(projectId)).stream()
                .sorted(Comparator.comparing(TaskColumn::getColumnOrder))
                .map(column -> new TaskColumnRest(column.getId(), column.getName(),
                    getTasksRest(columnTasksMap.get(Optional.of(column.getId())))))
            )
            .collect(Collectors.toList());
    }

    private static List<TaskRest> getTasksRest(final Collection<Task> tasks)
    {
        return tasks.stream()
            .sorted(Comparator.comparing(Task::getTaskOrder))
            .map(TaskColumnRestFactory::getTaskRest)
            .collect(Collectors.toList());
    }

    private static TaskRest getTaskRest(final Task task)
    {
        final Long taskId = task.getId();
        final String title = task.getTitle();
        final TaskPriority priority = task.getPriority();
        final List<TaskMemberRest> members = getMembers(task);
        final Task parentTaskOrNull = task.getParentTask();
        final Long parentTaskIdOrNull = ObjectUtils.accessNullable(parentTaskOrNull, Task::getId);
        final String parentTaskTitleOrNull = ObjectUtils.accessNullable(parentTaskOrNull, Task::getTitle);
        final String columnNameOrNull = ObjectUtils.accessNullable(task.getTaskColumn(), TaskColumn::getName);
        return new TaskRest(taskId, title, members, priority, parentTaskIdOrNull, parentTaskTitleOrNull, columnNameOrNull);
    }

    static List<TaskMemberRest> getMembers(final Task task)
    {
        return task.getMembers().stream()
            .map(member -> {
                final User user = member.getMember();
                final Long userId = user.getId();
                final String email = user.getEmail();
                return new TaskMemberRest(userId, email);
            })
            .sorted(Comparator.comparing(TaskMemberRest::getEmail, Comparator.naturalOrder())
                .thenComparing(TaskMemberRest::getUserId))
            .collect(Collectors.toList());
    }

    public static class TaskColumnRest
    {
        private final Long id;
        private final String name;
        private final List<TaskRest> tasks;

        private TaskColumnRest(final Long id, final String name,
            final List<TaskRest> tasks)
        {
            this.id = id;
            this.name = name;
            this.tasks = tasks;
        }

        public Long getId()
        {
            return id;
        }

        public String getName()
        {
            return name;
        }

        public List<TaskRest> getTasks()
        {
            return tasks;
        }

    }

    public static class TaskRest
    {
        private final Long taskId;
        private final String title;
        private final List<TaskMemberRest> members;
        private final TaskPriority priority;
        private final Long parentTaskIdOrNull;
        private final String parentTaskTitleOrNull;
        private final String columnNameOrNull;

        private TaskRest(final Long taskId, final String title,
            final List<TaskMemberRest> members, final TaskPriority priority,
            final Long parentTaskIdOrNull, final String parentTaskTitleOrNull,
            final String columnNameOrNull)
        {
            this.taskId = taskId;
            this.title = title;
            this.members = members;
            this.priority = priority;
            this.parentTaskIdOrNull = parentTaskIdOrNull;
            this.parentTaskTitleOrNull = parentTaskTitleOrNull;
            this.columnNameOrNull = columnNameOrNull;
        }

        public Long getTaskId()
        {
            return taskId;
        }

        public String getTitle()
        {
            return title;
        }

        public List<TaskMemberRest> getMembers()
        {
            return members;
        }

        public Long getParentTaskIdOrNull()
        {
            return parentTaskIdOrNull;
        }

        public TaskPriority getPriority()
        {
            return priority;
        }

        public String getParentTaskTitleOrNull()
        {
            return parentTaskTitleOrNull;
        }

        public String getColumnNameOrNull()
        {
            return columnNameOrNull;
        }

    }

}
