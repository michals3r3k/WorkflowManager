package com.example.workflowmanager.service.task;

import com.example.workflowmanager.db.organization.project.task.TaskMemberRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRelationRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRepository;
import com.example.workflowmanager.entity.chat.Chat;
import com.example.workflowmanager.entity.organization.project.task.Task;
import com.example.workflowmanager.service.chat.ChatDeleteService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TaskDeleteService
{
    private final TaskRepository taskRepository;
    private final TaskRelationRepository taskRelationRepository;
    private final TaskMemberRepository taskMemberRepository;
    private final ChatDeleteService chatDeleteService;

    public TaskDeleteService(final TaskRepository taskRepository,
        final TaskRelationRepository taskRelationRepository,
        final TaskMemberRepository taskMemberRepository,
        final ChatDeleteService chatDeleteService)
    {
        this.taskRepository = taskRepository;
        this.taskRelationRepository = taskRelationRepository;
        this.taskMemberRepository = taskMemberRepository;
        this.chatDeleteService = chatDeleteService;
    }

    public void delete(Collection<Long> taskIds)
    {
        final Set<Task> allTasks = getAllTasks(taskIds);
        deleteTaskRelations(allTasks);
        deleteMembers(allTasks);
        deleteChats(allTasks);
        deleteTasks(allTasks);
    }

    private Set<Task> getAllTasks(final Collection<Long> taskIds)
    {
        final List<Task> tasks = taskRepository.getList(taskIds);
        return Stream.concat(
            tasks.stream(),
            tasks.stream()
                .map(Task::getSubTasks)
                .flatMap(Collection::stream))
            .collect(Collectors.toSet());
    }

    private void deleteTaskRelations(final Set<Task> tasks)
    {
        final Set<Long> taskIds = tasks.stream()
            .map(Task::getId)
            .collect(Collectors.toSet());
        taskRelationRepository.deleteAll(taskRelationRepository.getListByTaskIds(taskIds));
    }

    private void deleteMembers(final Set<Task> tasks)
    {
        tasks.stream()
            .map(Task::getMembers)
            .flatMap(Collection::stream)
            .forEachOrdered(taskMemberRepository::delete);
    }

    private void deleteChats(final Set<Task> tasks)
    {
        final Set<Long> chatIds = tasks.stream()
            .map(Task::getChat)
            .map(Chat::getId)
            .collect(Collectors.toSet());
        chatDeleteService.delete(chatIds);
    }

    private void deleteTasks(final Set<Task> allTasks)
    {
        allTasks.stream()
            .sorted(Comparator.comparing(Task::getParentTaskId, Comparator.nullsLast(Comparator.naturalOrder())))
            .forEachOrdered(taskRepository::delete);
    }

}
