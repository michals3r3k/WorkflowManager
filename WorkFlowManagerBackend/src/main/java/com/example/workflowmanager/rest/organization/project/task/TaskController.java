package com.example.workflowmanager.rest.organization.project.task;

import com.example.workflowmanager.db.organization.project.task.TaskRepository;
import com.example.workflowmanager.entity.organization.project.task.Task;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.example.workflowmanager.service.utils.ServiceResult;
import com.google.common.collect.Iterables;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class TaskController
{
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_DATE_TIME;
    private final TaskRepository taskRepository;

    public TaskController(final TaskRepository taskRepository)
    {
        this.taskRepository = taskRepository;
    }

    @PostMapping("/api/organization/{organizationId}/project/{projectId}/task/save")
    @Transactional
    public ResponseEntity<ServiceResult<?>> getTask(@PathVariable Long organizationId,
        @PathVariable Long projectId, @RequestBody TaskRest task)
    {
        return ResponseEntity.ok(ServiceResult.ok());
    }

    @GetMapping("/api/organization/{organizationId}/project/{projectId}/task/{taskId}")
    @Transactional
    public ResponseEntity<TaskRest> getTask(@PathVariable Long organizationId,
        @PathVariable Long projectId, @PathVariable Long taskId)
    {
        /*
        insert into task_column(id, column_order, organization_id, project_id, name) values(1, 1, 1, 1, 'test');
        insert into task(id, organization_id, project_id, task_column_id, title, create_time) values(1, 1, 1, 1, 'test', now());
        insert into chat(id) values(1);
        */
        final Task task = Iterables.getOnlyElement(taskRepository.getListByIdsWithRelationalTasksAndMembers(
            Collections.singleton(taskId)) );
        List<TaskMemberRest> members = task.getMembers().stream()
            .map(member -> {
                final User user = member.getMember().getUser();
                final Long userId = user.getId();
                final String email = user.getEmail();
                return new TaskMemberRest(userId, email);
            })
            .sorted(Comparator.comparing(TaskMemberRest::getEmail, Comparator.naturalOrder())
                .thenComparing(TaskMemberRest::getUserId))
            .collect(Collectors.toList());
        List<SubTaskRest> subTasks = task.getSubTasks().stream()
            .map(subTask -> {
                final Long subTaskId = subTask.getId();
                final String title = subTask.getTitle();
                return new SubTaskRest(subTaskId, title);
            })
            .sorted(Comparator.comparing(SubTaskRest::getSubTaskId))
            .collect(Collectors.toList());
        final Task parentTaskOrNull = task.getParentTask();
        final Long parentTaskIdOrNull = ObjectUtils.accessNullable(parentTaskOrNull, Task::getId);
        final String parentTaskTitleOrNull = ObjectUtils.accessNullable(parentTaskOrNull, Task::getTitle);
        final String title = task.getTitle();
        final String descriptionOrNull = task.getDescription();
        final String createTime = task.getCreateTime().format(DTF);
        final String startDateOrNull = ObjectUtils.accessNullable(task.getStartDate(), date -> date.format(DTF));
        final String finishDateOrNull = ObjectUtils.accessNullable(task.getFinishDate(), date -> date.format(DTF));
        final String deadlineDateOrNull = ObjectUtils.accessNullable(task.getDeadlineDate(), date -> date.format(DTF));
        final Long chatId = task.getChat().getId();
        return ResponseEntity.ok(new TaskRest(taskId, chatId, title, descriptionOrNull, createTime,
            startDateOrNull, finishDateOrNull, deadlineDateOrNull, parentTaskIdOrNull,
            parentTaskTitleOrNull, members, subTasks));
    }

    public static class TaskRest
    {
        private Long taskId;
        private Long chatId;
        private String title;
        private String descriptionOrNull;
        private String createTime;
        private String startDateOrNull;
        private String finishDateOrNull;
        private String deadlineDateOrNull;
        private Long parentTaskIdOrNull;
        private String parentTaskTitleOrNull;
        private List<TaskMemberRest> members;
        private List<SubTaskRest> subTasks;

        private TaskRest(final Long taskId, Long chatId, final String title,
            final String descriptionOrNull, final String createTime,
            final String startDateOrNull, final String finishDateOrNull,
            final String deadlineDateOrNull, final Long parentTaskIdOrNull,
            final String parentTaskTitleOrNull, final List<TaskMemberRest> members,
            final List<SubTaskRest> subTasks)
        {
            this.taskId = taskId;
            this.chatId = chatId;
            this.title = title;
            this.descriptionOrNull = descriptionOrNull;
            this.createTime = createTime;
            this.startDateOrNull = startDateOrNull;
            this.finishDateOrNull = finishDateOrNull;
            this.deadlineDateOrNull = deadlineDateOrNull;
            this.parentTaskIdOrNull = parentTaskIdOrNull;
            this.parentTaskTitleOrNull = parentTaskTitleOrNull;
            this.members = members;
            this.subTasks = subTasks;
        }

        public TaskRest()
        {
            // for Spring
        }

        public Long getTaskId()
        {
            return taskId;
        }

        public void setTaskId(final Long taskId)
        {
            this.taskId = taskId;
        }

        public Long getChatId()
        {
            return chatId;
        }

        public void setChatId(final Long chatId)
        {
            this.chatId = chatId;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(final String title)
        {
            this.title = title;
        }

        public String getCreateTime()
        {
            return createTime;
        }

        public void setCreateTime(final String createTime)
        {
            this.createTime = createTime;
        }

        public String getStartDateOrNull()
        {
            return startDateOrNull;
        }

        public void setStartDateOrNull(final String startDateOrNull)
        {
            this.startDateOrNull = startDateOrNull;
        }

        public String getFinishDateOrNull()
        {
            return finishDateOrNull;
        }

        public void setFinishDateOrNull(final String finishDateOrNull)
        {
            this.finishDateOrNull = finishDateOrNull;
        }

        public String getDeadlineDateOrNull()
        {
            return deadlineDateOrNull;
        }

        public void setDeadlineDateOrNull(final String deadlineDateOrNull)
        {
            this.deadlineDateOrNull = deadlineDateOrNull;
        }

        public Long getParentTaskIdOrNull()
        {
            return parentTaskIdOrNull;
        }

        public void setParentTaskIdOrNull(final Long parentTaskIdOrNull)
        {
            this.parentTaskIdOrNull = parentTaskIdOrNull;
        }

        public String getParentTaskTitleOrNull()
        {
            return parentTaskTitleOrNull;
        }

        public void setParentTaskTitleOrNull(final String parentTaskTitleOrNull)
        {
            this.parentTaskTitleOrNull = parentTaskTitleOrNull;
        }

        public List<TaskMemberRest> getMembers()
        {
            return members;
        }

        public void setMembers(
            final List<TaskMemberRest> members)
        {
            this.members = members;
        }

        public List<SubTaskRest> getSubTasks()
        {
            return subTasks;
        }

        public void setSubTasks(
            final List<SubTaskRest> subTasks)
        {
            this.subTasks = subTasks;
        }

        public String getDescriptionOrNull()
        {
            return descriptionOrNull;
        }

        public void setDescriptionOrNull(final String descriptionOrNull)
        {
            this.descriptionOrNull = descriptionOrNull;
        }

    }

    public static class SubTaskRest
    {
        private Long subTaskId;
        private String title;

        private SubTaskRest(final Long subTaskId, final String title)
        {
            this.subTaskId = subTaskId;
            this.title = title;
        }

        public Long getSubTaskId()
        {
            return subTaskId;
        }

        public void setSubTaskId(final Long subTaskId)
        {
            this.subTaskId = subTaskId;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(final String title)
        {
            this.title = title;
        }

    }

    public static class TaskMemberRest
    {

        private Long userId;
        private String email;

        private TaskMemberRest(final Long userId, final String email)
        {
            this.userId = userId;
            this.email = email;
        }

        public Long getUserId()
        {
            return userId;
        }

        public void setUserId(final Long userId)
        {
            this.userId = userId;
        }

        public String getEmail()
        {
            return email;
        }

        public void setEmail(final String email)
        {
            this.email = email;
        }

    }

}
