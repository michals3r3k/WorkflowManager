package com.example.workflowmanager.rest.organization.project.task;

import com.example.workflowmanager.rest.organization.project.task.TaskColumnRestFactory.TaskColumnRest;
import com.example.workflowmanager.service.task.TaskColumnCreateService;
import com.example.workflowmanager.service.task.TaskColumnCreateService.TaskColumnCreateError;
import com.example.workflowmanager.service.task.TaskCreateService;
import com.example.workflowmanager.service.task.TaskCreateService.TaskCreateServiceResult;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class TaskColumnController
{
    private final TaskColumnRestFactory taskColumnRestFactory;
    private final TaskColumnCreateService taskColumnCreateService;
    private final TaskCreateService taskCreateService;

    public TaskColumnController(final TaskColumnRestFactory taskColumnRestFactory,
        final TaskColumnCreateService taskColumnCreateService,
        final TaskCreateService taskCreateService)
    {
        this.taskColumnRestFactory = taskColumnRestFactory;
        this.taskColumnCreateService = taskColumnCreateService;
        this.taskCreateService = taskCreateService;
    }

    @PostMapping("/api/organization/{organizationId}/project/{projectId}/task/column/add-task")
    @Transactional
    public ResponseEntity<TaskCreateServiceResult> createTask(
        @PathVariable Long organizationId, @PathVariable Long projectId,
        @RequestBody TaskCreateRequestRest task)
    {
        return ResponseEntity.ok(taskCreateService.create(projectId, task));
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
