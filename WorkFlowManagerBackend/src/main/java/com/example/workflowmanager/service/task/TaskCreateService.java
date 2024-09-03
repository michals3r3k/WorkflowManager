package com.example.workflowmanager.service.task;

import com.example.workflowmanager.db.chat.ChatRepository;
import com.example.workflowmanager.db.organization.project.task.TaskColumnRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRepository;
import com.example.workflowmanager.entity.chat.Chat;
import com.example.workflowmanager.entity.organization.project.task.Task;
import com.example.workflowmanager.entity.organization.project.task.TaskColumn;
import com.example.workflowmanager.entity.organization.project.task.TaskPriority;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.rest.organization.project.task.TaskColumnController.TaskCreateRequestRest;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.Iterables;
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

    public TaskCreateService(final ChatRepository chatRepository, final TaskRepository taskRepository,
        final TaskColumnRepository taskColumnRepository)
    {
        this.chatRepository = chatRepository;
        this.taskRepository = taskRepository;
        this.taskColumnRepository = taskColumnRepository;
    }

    public TaskCreateServiceResult create(final Long projectId,
        final TaskCreateRequestRest taskDto, final User creator)
    {
        final List<Task> tasks = taskRepository.getListByProjectIds(Collections.singleton(projectId));
        final Set<TaskCreateError> errors = EnumSet.noneOf(TaskCreateError.class);
        if(isExists(taskDto, tasks))
        {
            errors.add(TaskCreateError.EXISTS);
        }
        final TaskColumn taskColumnOrNull = Iterables.getFirst(taskColumnRepository.getListByIds(
            Collections.singleton(taskDto.getTaskColumnId())), null);
        if(taskColumnOrNull == null)
        {
            errors.add(TaskCreateError.COLUMN_NOT_EXISTS);
        }
        if(!errors.isEmpty())
        {
            return new TaskCreateServiceResult(null, errors);
        }
        final Chat chat = new Chat();
        chatRepository.save(chat);
        final Task task = new Task(taskDto.getTitle(), LocalDateTime.now(),
            taskColumnOrNull, chat, creator, TaskPriority.MEDIUM);
        taskRepository.save(task);
        return new TaskCreateServiceResult(task, errors);
    }

    private static boolean isExists(final TaskCreateRequestRest task, final List<Task> tasks)
    {
        return tasks.stream()
            .map(Task::getTitle)
            .anyMatch(title -> StringUtils.equalsIgnoreCase(title, task.getTitle()));
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
        COLUMN_NOT_EXISTS;
    }

}
