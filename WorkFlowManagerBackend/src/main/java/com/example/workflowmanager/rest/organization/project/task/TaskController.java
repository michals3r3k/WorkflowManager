package com.example.workflowmanager.rest.organization.project.task;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRepository;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.OrganizationMember;
import com.example.workflowmanager.entity.organization.OrganizationMemberInvitationStatus;
import com.example.workflowmanager.entity.organization.project.task.Task;
import com.example.workflowmanager.entity.organization.project.task.TaskColumn;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.rest.organization.project.task.TaskRestFactory.TaskRest;
import com.example.workflowmanager.service.auth.CurrentUserService;
import com.example.workflowmanager.service.task.TaskDeleteService;
import com.example.workflowmanager.service.task.TaskEditService;
import com.example.workflowmanager.service.task.TaskEditService.TaskEditServiceResult;
import com.example.workflowmanager.service.utils.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
    private final TaskRepository taskRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationRepository organizationRepository;
    private final TaskRestFactory taskRestFactory;
    private final TaskEditService taskEditService;
    private final CurrentUserService currentUserService;
    private final TaskDeleteService taskDeleteService;

    public TaskController(final TaskRepository taskRepository,
        final OrganizationMemberRepository organizationMemberRepository,
        final OrganizationRepository organizationRepository,
        final TaskRestFactory taskRestFactory, final TaskEditService taskEditService,
        final CurrentUserService currentUserService,
        final TaskDeleteService taskDeleteService)
    {
        this.taskRepository = taskRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.organizationRepository = organizationRepository;
        this.taskRestFactory = taskRestFactory;
        this.taskEditService = taskEditService;
        this.currentUserService = currentUserService;
        this.taskDeleteService = taskDeleteService;
    }

    @GetMapping("/api/organization/{organizationId}/project/{projectId}/task/{taskId}/relation/options")
    @Transactional
    public ResponseEntity<List<TaskRelationOptionRest>> getTaskRelationOptions(
        @PathVariable Long organizationId, @PathVariable Long projectId, @PathVariable Long taskId)
    {
        final List<TaskRelationOptionRest> taskRelationOptions = taskRepository.getListByProjectIds(
            Collections.singleton(projectId)).stream()
            .filter(task -> !task.getId().equals(taskId))
            .map(TaskRelationOptionRest::new)
            .sorted(Comparator.comparing(TaskRelationOptionRest::getTitle, Comparator.naturalOrder())
                .thenComparing(TaskRelationOptionRest::getTaskId))
            .collect(Collectors.toList());
        return ResponseEntity.ok(taskRelationOptions);
    }

    @GetMapping("/api/organization/{organizationId}/task/member/options")
    @Transactional
    public ResponseEntity<List<TaskMemberOptionRest>> getMemberOptions(@PathVariable Long organizationId)
    {
        final Set<Long> organizationIds = Collections.singleton(organizationId);
        final List<TaskMemberOptionRest> options = Stream.concat(
            organizationRepository.getList(organizationIds).stream()
                .map(Organization::getUser),
            organizationMemberRepository.getListByOrganization(organizationIds, Collections.singleton(
                OrganizationMemberInvitationStatus.ACCEPTED)).stream()
                .map(OrganizationMember::getUser))
            .distinct()
            .sorted(Comparator.comparing(User::getEmail, Comparator.naturalOrder())
                .thenComparing(User::getId))
            .map(TaskMemberOptionRest::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(options);
    }

    @GetMapping("/api/organization/{organizationId}/project/{projectId}/task/{taskId}/delete")
    @Transactional
    public void delete(@PathVariable Long organizationId, @PathVariable Long projectId, @PathVariable Long taskId)
    {
        taskDeleteService.delete(Collections.singleton(taskId));
    }

    @PostMapping("/api/organization/{organizationId}/project/{projectId}/task/save")
    @Transactional
    public ResponseEntity<TaskEditServiceResult> save(@PathVariable Long organizationId,
        @PathVariable Long projectId, @RequestBody TaskRest task)
    {
        final User user = currentUserService.getCurrentUser().orElseThrow();
        return ResponseEntity.ok(taskEditService.save(organizationId, projectId, task, user));
    }

    @GetMapping("/api/organization/{organizationId}/project/{projectId}/task/{taskId}")
    @Transactional
    public ResponseEntity<TaskRest> get(@PathVariable Long organizationId,
        @PathVariable Long projectId, @PathVariable Long taskId)
    {
        return ResponseEntity.ok(taskRestFactory.get(taskId));
    }

    public static class TaskMemberOptionRest
    {
        private final User user;

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
            return user.getFullName();
        }

    }

    public static class TaskRelationOptionRest
    {
        private final Task task;

        public TaskRelationOptionRest(final Task task)
        {
            this.task = task;
        }

        public Long getTaskId()
        {
            return task.getId();
        }

        public String getTitle()
        {
            return task.getTitle();
        }

        public String getColumnName()
        {
            return ObjectUtils.accessNullable(task.getTaskColumn(), TaskColumn::getName);
        }

    }

}
