package com.example.workflowmanager.service.task;

import com.example.workflowmanager.db.chat.ChatRepository;
import com.example.workflowmanager.db.issue.IssueRepository;
import com.example.workflowmanager.db.organization.project.task.TaskColumnRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRepository;
import com.example.workflowmanager.entity.chat.Chat;
import com.example.workflowmanager.entity.issue.Issue;
import com.example.workflowmanager.entity.organization.project.task.Task;
import com.example.workflowmanager.entity.organization.project.task.TaskColumn;
import com.example.workflowmanager.entity.organization.project.task.TaskPriority;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TaskCreateService
{
    private final ChatRepository chatRepository;
    private final TaskRepository taskRepository;
    private final TaskColumnRepository taskColumnRepository;
    private final IssueRepository issueRepository;

    public TaskCreateService(final ChatRepository chatRepository,
        final TaskRepository taskRepository,
        final TaskColumnRepository taskColumnRepository,
        final IssueRepository issueRepository)
    {
        this.chatRepository = chatRepository;
        this.taskRepository = taskRepository;
        this.taskColumnRepository = taskColumnRepository;
        this.issueRepository = issueRepository;
    }

    public TaskCreateServiceResult create(final Long organizationId,
        final Long projectId, final String title, final Long issueId,
        final Long columnId, final User creator)
    {
        final Multimap<Optional<Long>, Task> columnTasksMap = Multimaps.index(
            taskRepository.getListByProjectIds(Collections.singleton(projectId)),
            task -> Optional.ofNullable(task.getTaskColumnId()));
        final Set<TaskCreateError> errors = EnumSet.noneOf(TaskCreateError.class);
        if(isExists(title, columnTasksMap.values()))
        {
            errors.add(TaskCreateError.EXISTS);
        }
        if(columnId != null)
        {
            final TaskColumn taskColumnOrNull = Iterables.getFirst(taskColumnRepository.getListByIds(
                Collections.singleton(columnId)), null);
            if(taskColumnOrNull == null)
            {
                errors.add(TaskCreateError.COLUMN_NOT_EXISTS);
            }
        }
        if(issueId != null)
        {
            final Issue issue = issueRepository.getById(issueId);
            if(issue == null)
            {
                errors.add(TaskCreateError.ISSUE_NOT_EXISTS);
            }
        }
        if(!errors.isEmpty())
        {
            return new TaskCreateServiceResult(null, errors);
        }
        final Chat chat = new Chat();
        chatRepository.save(chat);
        final short order = getNewOrder(columnTasksMap.get(
            Optional.ofNullable(columnId)));
        final Task task = new Task(title, LocalDateTime.now(),
            chat, organizationId, projectId, issueId, columnId,
            creator, TaskPriority.MEDIUM, order);
        taskRepository.save(task);
        return new TaskCreateServiceResult(task, errors);
    }

    private static short getNewOrder(final Collection<Task> tasks)
    {
        return (short) tasks.stream()
            .mapToInt(Task::getTaskOrder)
            .max()
            .stream().map(i -> i + 1)
            .findFirst()
            .orElse(0);
    }

    private static boolean isExists(final String newTitle, final Collection<Task> tasks)
    {
        return tasks.stream()
            .map(Task::getTitle)
            .anyMatch(title -> StringUtils.equalsIgnoreCase(title, newTitle));
    }

    public static class TaskCreateServiceResult extends ServiceResult<TaskCreateError>
    {
        private final Task taskOrNull;

        public TaskCreateServiceResult(final Task taskOrNull,
            final Collection<TaskCreateError> errors)
        {
            super(errors);
            this.taskOrNull = taskOrNull;
        }

        public Long getTaskIdOrNull()
        {
            return ObjectUtils.accessNullable(taskOrNull, Task::getId);
        }

        Task getTaskOrNull()
        {
            return taskOrNull;
        }

    }

    public enum TaskCreateError
    {
        EXISTS,
        COLUMN_NOT_EXISTS,
        ISSUE_NOT_EXISTS
    }

}
