package com.example.workflowmanager.rest.organization.project.task;

import com.example.workflowmanager.db.organization.project.ProjectRepository;
import com.example.workflowmanager.db.organization.project.task.TaskColumnRepository;
import com.example.workflowmanager.entity.organization.project.task.Task;
import com.example.workflowmanager.entity.organization.project.task.TaskColumn;
import com.example.workflowmanager.entity.user.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskColumnRestFactory
{
    private final TaskColumnRepository taskColumnRepository;
    private final ProjectRepository projectRepository;

    public TaskColumnRestFactory(
        final TaskColumnRepository taskColumnRepository,
        final ProjectRepository projectRepository)
    {
        this.taskColumnRepository = taskColumnRepository;
        this.projectRepository = projectRepository;
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
            .sorted(Comparator.comparing(Task::getId))
            .map(TaskColumnRestFactory::getTaskRest)
            .collect(Collectors.toList());
        return new TaskColumnRest(id, name, tasks);
    }

    private static TaskRest getTaskRest(final Task task)
    {
        final Long taskId = task.getId();
        final String title = task.getTitle();
        final List<TaskMemberRest> members = getMembers(task);
        return new TaskRest(taskId, title, members);
    }

    private static List<TaskMemberRest> getMembers(final Task task)
    {
        return task.getMembers().stream()
            .map(member -> {
                final User user = member.getMember().getUser();
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

        private TaskRest(final Long taskId, final String title,
            final List<TaskMemberRest> members)
        {
            this.taskId = taskId;
            this.title = title;
            this.members = members;
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

    }

    public static class TaskMemberRest
    {
        private final Long userId;
        private final String email;

        private TaskMemberRest(final Long userId, final String email)
        {
            this.userId = userId;
            this.email = email;
        }

        public Long getUserId()
        {
            return userId;
        }

        public String getEmail()
        {
            return email;
        }

    }
}