package com.example.workflowmanager.rest.organization.project.task;

import com.example.workflowmanager.db.organization.project.task.TaskColumnRepository;
import com.example.workflowmanager.entity.organization.project.task.Task;
import com.example.workflowmanager.entity.organization.project.task.TaskColumn;
import com.example.workflowmanager.entity.organization.project.task.TaskPriority;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.rest.organization.project.task.TaskRestFactory.TaskMemberRest;
import com.example.workflowmanager.service.utils.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskColumnRestFactory
{
    private final TaskColumnRepository taskColumnRepository;

    public TaskColumnRestFactory(
        final TaskColumnRepository taskColumnRepository)
    {
        this.taskColumnRepository = taskColumnRepository;
    }

    public List<TaskColumnRest> getList(final Long projectId)
    {
        return taskColumnRepository.getListByProjectIdsWithTaskMembers(
            Collections.singleton(projectId)).stream()
            .sorted(Comparator.comparing(TaskColumn::getColumnOrder))
            .map(TaskColumnRestFactory::getColumnRest)
            .collect(Collectors.toList());
    }

    private static TaskColumnRest getColumnRest(final TaskColumn column)
    {
        final Long id = column.getId();
        final String name = column.getName();
        final List<TaskRest> tasks = column.getTasks().stream()
            .sorted(Comparator.comparing(Task::getTaskOrder))
            .map(TaskColumnRestFactory::getTaskRest)
            .collect(Collectors.toList());
        return new TaskColumnRest(id, name, tasks);
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
        return new TaskRest(taskId, title, members, priority, parentTaskIdOrNull, parentTaskTitleOrNull);
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

        private TaskRest(final Long taskId, final String title,
            final List<TaskMemberRest> members, final TaskPriority priority,
            final Long parentTaskIdOrNull, final String parentTaskTitleOrNull)
        {
            this.taskId = taskId;
            this.title = title;
            this.members = members;
            this.priority = priority;
            this.parentTaskIdOrNull = parentTaskIdOrNull;
            this.parentTaskTitleOrNull = parentTaskTitleOrNull;
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

    }

}
