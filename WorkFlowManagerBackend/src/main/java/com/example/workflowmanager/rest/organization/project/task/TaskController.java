package com.example.workflowmanager.rest.organization.project.task;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRepository;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.OrganizationMember;
import com.example.workflowmanager.entity.organization.project.task.Task;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.task.TaskEditService;
import com.example.workflowmanager.service.task.TaskEditService.TaskEditError;
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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin
@RestController
public class TaskController
{
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_DATE_TIME;

    private final TaskRepository taskRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationRepository organizationRepository;
    private final TaskEditService taskEditService;

    public TaskController(final TaskRepository taskRepository,
        final OrganizationMemberRepository organizationMemberRepository,
        final OrganizationRepository organizationRepository,
        final TaskEditService taskEditService)
    {
        this.taskRepository = taskRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.organizationRepository = organizationRepository;
        this.taskEditService = taskEditService;
    }

    @GetMapping("/api/organization/{organizationId}/task/member/options")
    @Transactional
    public ResponseEntity<List<TaskMemberOptionRest>> getMemberOptions(@PathVariable Long organizationId)
    {
        final Set<Long> organizationIds = Collections.singleton(organizationId);
        final List<TaskMemberOptionRest> options = Stream.concat(
            organizationRepository.getList(organizationIds).stream()
                .map(Organization::getUser),
            organizationMemberRepository.getListByOrganization(organizationIds).stream()
                .map(OrganizationMember::getUser))
            .distinct()
            .sorted(Comparator.comparing(User::getEmail, Comparator.naturalOrder())
                .thenComparing(User::getId))
            .map(TaskMemberOptionRest::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(options);
    }

    @PostMapping("/api/organization/{organizationId}/project/{projectId}/task/save")
    @Transactional
    public ResponseEntity<ServiceResult<TaskEditError>> save(@PathVariable Long organizationId,
        @PathVariable Long projectId, @RequestBody TaskRest task)
    {
        return ResponseEntity.ok(taskEditService.save(projectId, task));
    }

    @GetMapping("/api/organization/{organizationId}/project/{projectId}/task/{taskId}")
    @Transactional
    public ResponseEntity<TaskRest> getTask(@PathVariable Long organizationId,
        @PathVariable Long projectId, @PathVariable Long taskId)
    {
        final Task task = Iterables.getOnlyElement(taskRepository.getListByIdsWithRelationalTasksAndMembers(
            Collections.singleton(taskId)) );

        final List<TaskMemberRest> members = TaskColumnRestFactory.getMembers(task);
        final List<SubTaskRest> subTasks = task.getSubTasks().stream()
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
        final User creator = task.getCreator();
        final Long creatorId = creator.getId();
        final String creatorName = creator.getEmail();
        return ResponseEntity.ok(new TaskRest(taskId, chatId, title, descriptionOrNull,
            createTime, creatorId, creatorName, startDateOrNull, finishDateOrNull,
            deadlineDateOrNull, parentTaskIdOrNull, parentTaskTitleOrNull,
            members, subTasks));
    }

    public static class TaskRest
    {
        private Long taskId;
        private Long chatId;
        private String title;
        private String descriptionOrNull;
        private String createTime;
        private Long creatorId;
        private String creatorName;
        private String startDateOrNull;
        private String finishDateOrNull;
        private String deadlineDateOrNull;
        private Long parentTaskIdOrNull;
        private String parentTaskTitleOrNull;
        private List<TaskMemberRest> members;
        private List<SubTaskRest> subTasks;

        private TaskRest(final Long taskId, Long chatId, final String title,
            final String descriptionOrNull, final String createTime,
            final Long creatorId, final String creatorName, final String startDateOrNull,
            final String finishDateOrNull, final String deadlineDateOrNull,
            final Long parentTaskIdOrNull, final String parentTaskTitleOrNull,
            final List<TaskMemberRest> members, final List<SubTaskRest> subTasks)
        {
            this.taskId = taskId;
            this.chatId = chatId;
            this.title = title;
            this.descriptionOrNull = descriptionOrNull;
            this.createTime = createTime;
            this.creatorId = creatorId;
            this.creatorName = creatorName;
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

        public Long getCreatorId()
        {
            return creatorId;
        }

        public void setCreatorId(final Long creatorId)
        {
            this.creatorId = creatorId;
        }

        public String getCreatorName()
        {
            return creatorName;
        }

        public void setCreatorName(final String creatorName)
        {
            this.creatorName = creatorName;
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

        TaskMemberRest(final Long userId, final String email)
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

    public static class TaskMemberOptionRest
    {
        private User user;

        private TaskMemberOptionRest(User user)
        {
            this.user = user;
        }

        public Long getUserId()
        {
            return user.getId();
        }

        public String getName()
        {
            return user.getEmail();
        }

    }

}
