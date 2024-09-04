package com.example.workflowmanager.rest.organization.project.task;

import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.rest.organization.project.task.TaskColumnRestFactory.TaskColumnRest;
import com.example.workflowmanager.service.auth.CurrentUserService;
import com.example.workflowmanager.service.task.*;
import com.example.workflowmanager.service.task.TaskChangeOrderService.TaskChangeOrderError;
import com.example.workflowmanager.service.task.TaskColumnChangeOrderService.TaskColumnChangeOrderError;
import com.example.workflowmanager.service.task.TaskColumnCreateService.TaskColumnCreateError;
import com.example.workflowmanager.service.task.TaskColumnDeleteService.TaskColumnDeleteError;
import com.example.workflowmanager.service.task.TaskCreateService.TaskCreateServiceResult;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin
@RestController
public class TaskColumnController
{
    private final TaskColumnRestFactory taskColumnRestFactory;
    private final TaskColumnCreateService taskColumnCreateService;
    private final TaskCreateService taskCreateService;
    private final TaskColumnChangeOrderService taskColumnChangeOrderService;
    private final TaskChangeOrderService taskChangeOrderService;
    private final TaskColumnDeleteService taskColumnDeleteService;
    private final CurrentUserService currentUserService;

    public TaskColumnController(final TaskColumnRestFactory taskColumnRestFactory,
        final TaskColumnCreateService taskColumnCreateService,
        final TaskCreateService taskCreateService,
        final TaskColumnChangeOrderService taskColumnChangeOrderService,
        final TaskChangeOrderService taskChangeOrderService, final TaskColumnDeleteService taskColumnDeleteService,
        final CurrentUserService currentUserService)
    {
        this.taskColumnRestFactory = taskColumnRestFactory;
        this.taskColumnCreateService = taskColumnCreateService;
        this.taskCreateService = taskCreateService;
        this.taskColumnChangeOrderService = taskColumnChangeOrderService;
        this.taskChangeOrderService = taskChangeOrderService;
        this.taskColumnDeleteService = taskColumnDeleteService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/api/organization/{organizationId}/project/{projectId}/task/column/add-task")
    @Transactional
    public ResponseEntity<TaskCreateServiceResult> createTask(
        @PathVariable Long organizationId, @PathVariable Long projectId,
        @RequestBody TaskCreateRequestRest task)
    {
        final User user = currentUserService.getCurrentUser()
            .orElseThrow(NoSuchElementException::new);
        return ResponseEntity.ok(taskCreateService.create(organizationId, projectId, task, user));
    }

    @PostMapping("/api/organization/{organizationId}/project/{projectId}/task/column/add")
    @Transactional
    public ResponseEntity<ServiceResult<TaskColumnCreateError>> create(
        @PathVariable Long organizationId, @PathVariable Long projectId, @RequestBody String columnName)
    {
        return ResponseEntity.ok(taskColumnCreateService.create(projectId, columnName));
    }

    @GetMapping("/api/organization/{organizationId}/project/{projectId}/task/columns")
    @Transactional
    public ResponseEntity<List<TaskColumnRest>> getList(@PathVariable Long organizationId, @PathVariable Long projectId)
    {
        return ResponseEntity.ok(taskColumnRestFactory.getList(projectId));
    }

    @PostMapping("/api/organization/{organizationId}/project/{projectId}/task/column/change-order")
    @Transactional
    public ResponseEntity<ServiceResult<TaskColumnChangeOrderError>> saveOrder(
        @PathVariable Long organizationId, @PathVariable Long projectId,
        @RequestBody List<TaskColumnOrderRest> taskColumnOrders)
    {
        return ResponseEntity.ok(taskColumnChangeOrderService.changeOrder(projectId, taskColumnOrders));
    }

    @PostMapping("/api/organization/{organizationId}/project/{projectId}/task/column/change-task-order")
    @Transactional
    public ResponseEntity<ServiceResult<TaskChangeOrderError>> saveTaskOrder(
        @PathVariable Long organizationId, @PathVariable Long projectId,
        @RequestBody List<TaskOrderRest> taskOrders)
    {
        return ResponseEntity.ok(taskChangeOrderService.changeOrder(projectId, taskOrders));
    }

    @GetMapping("/api/organization/{organizationId}/project/{projectId}/task/column/{taskColumnId}/delete")
    @Transactional
    public ResponseEntity<ServiceResult<TaskColumnDeleteError>> delete(@PathVariable Long organizationId,
        @PathVariable Long projectId, @PathVariable Long taskColumnId)
    {
        return ResponseEntity.ok(taskColumnDeleteService.delete(projectId, taskColumnId));
    }

    public static class TaskColumnOrderRest
    {
        private Long taskColumnId;
        private Short order;

        public TaskColumnOrderRest(final Long taskColumnId,
            final Short order)
        {
            this.taskColumnId = taskColumnId;
            this.order = order;
        }

        public TaskColumnOrderRest()
        {
            // for Spring
        }

        public Long getTaskColumnId()
        {
            return taskColumnId;
        }

        public void setTaskColumnId(final Long taskColumnId)
        {
            this.taskColumnId = taskColumnId;
        }

        public Short getOrder()
        {
            return order;
        }

        public void setOrder(final Short order)
        {
            this.order = order;
        }

    }

    public static class TaskOrderRest
    {
        private Long taskId;
        private Long taskColumnId;
        private Short order;

        public TaskOrderRest(final Long taskId, final Long taskColumnId,
            final Short order)
        {
            this.taskId = taskId;
            this.taskColumnId = taskColumnId;
            this.order = order;
        }

        public TaskOrderRest()
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

        public Long getTaskColumnId()
        {
            return taskColumnId;
        }

        public void setTaskColumnId(final Long taskColumnId)
        {
            this.taskColumnId = taskColumnId;
        }

        public Short getOrder()
        {
            return order;
        }

        public void setOrder(final Short order)
        {
            this.order = order;
        }

    }

    public static class TaskCreateRequestRest
    {
        private String title;
        private Long taskColumnId;

        public TaskCreateRequestRest()
        {
            // for Spring
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(final String title)
        {
            this.title = title;
        }

        public Long getTaskColumnId()
        {
            return taskColumnId;
        }

        public void setTaskColumnId(final Long taskColumnId)
        {
            this.taskColumnId = taskColumnId;
        }

    }

}
