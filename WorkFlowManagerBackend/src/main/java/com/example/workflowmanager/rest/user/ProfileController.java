package com.example.workflowmanager.rest.user;

import com.example.workflowmanager.db.organization.OrganizationRepository;
import com.example.workflowmanager.db.organization.project.ProjectRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRepository;
import com.example.workflowmanager.db.user.UserRepository;
import com.example.workflowmanager.entity.organization.Organization;
import com.example.workflowmanager.entity.organization.project.Project;
import com.example.workflowmanager.entity.organization.project.task.Task;
import com.example.workflowmanager.entity.organization.project.task.TaskColumn;
import com.example.workflowmanager.entity.organization.role.Permission;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.auth.CurrentUserService;
import com.example.workflowmanager.service.auth.UserPermissionService;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class ProfileController
{
    private static final String UPLOADED_FILE = "uploadedfile";

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final CurrentUserService currentUserService;
    private final UserPermissionService userPermissionService;

    public ProfileController(final UserRepository userRepository,
        final OrganizationRepository organizationRepository,
        final ProjectRepository projectRepository,
        final TaskRepository taskRepository, final CurrentUserService currentUserService,
        final UserPermissionService userPermissionService)
    {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.currentUserService = currentUserService;
        this.userPermissionService = userPermissionService;
    }

    @PostMapping("/api/profile/img/upload")
    @Transactional
    public ResponseEntity<byte[]> uploadImg(@RequestParam("file") MultipartFile file) {
        try
        {
            final byte[] bytes = file.getBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(bytes.length);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        }
        catch (Exception e)
        {
            return ResponseEntity.ok(null);
        }
    }

    @PostMapping("/api/profile/img/save")
    @Transactional
    public ResponseEntity<ServiceResult<?>> saveImg(@RequestParam("file") MultipartFile file)
    {
        User user = currentUserService.getCurrentUser()
            .orElseThrow(NoSuchElementException::new);
        try
        {
            user.setImgContent(file.getBytes());
            userRepository.save(user);
            return ResponseEntity.ok(ServiceResult.ok());
        }
        catch (Exception e)
        {
            return ResponseEntity.ok(ServiceResult.ok());
        }
    }

    @PostMapping("/api/profile/save")
    @Transactional
    public ResponseEntity<ServiceResult<?>> saveProfile(@RequestBody ProfileEditRest editRest) {
        User user = currentUserService.getCurrentUser()
            .orElseThrow(NoSuchElementException::new);
        user.setFirstName(editRest.getFirstName());
        user.setSecondName(editRest.getSecondName());
        userRepository.save(user);
        return ResponseEntity.ok(ServiceResult.ok());
    }

    @GetMapping("/api/profile/img")
    @Transactional
    public ResponseEntity<byte[]> getProfileImg() {
        User user = currentUserService.getCurrentUser()
            .orElseThrow(NoSuchElementException::new);
        if(user.getImgContent() != null)
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(user.getImgContent().length);
            return new ResponseEntity<>(user.getImgContent(), headers, HttpStatus.OK);
        }
        else
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/api/profile")
    @Transactional
    public ResponseEntity<ProfileEditRest> getProfileData()
    {
        ProfileEditRest editRest = currentUserService.getCurrentUser()
            .map(user -> new ProfileEditRest(user.getFirstName(), user.getSecondName()))
            .orElseThrow(NoSuchElementException::new);
        return ResponseEntity.ok(editRest);
    }

    @GetMapping("/api/profile/connected-objects")
    @Transactional
    public ResponseEntity<ConnectedObjects> getConnectedObjects()
    {
        final Long userId = currentUserService.getCurrentUser()
            .map(User::getId)
            .orElseThrow();
        final Map<Long, Collection<Permission>> currentUserPermissions = userPermissionService.getCurrentUserPermissions();
        final Set<Long> organizationIds = currentUserPermissions.entrySet().stream()
            .filter(entry -> entry.getValue().contains(Permission.ORGANIZATION_R))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
        final Set<Long> projectOrganizationIds = currentUserPermissions.entrySet().stream()
            .filter(entry -> entry.getValue().contains(Permission.PROJECT_R))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
        final List<OrganizationRest> organizations = organizationRepository.getList(
            organizationIds).stream()
            .map(OrganizationRest::new)
            .sorted(Comparator.comparing(OrganizationRest::getName, Comparator.naturalOrder())
                .thenComparing(OrganizationRest::getId))
            .collect(Collectors.toList());
        final List<ProjectRest> projects = projectRepository.getListByOrganizationIds(
            projectOrganizationIds).stream()
            .map(ProjectRest::new)
            .sorted(Comparator.comparing(ProjectRest::getOrganizationName, Comparator.naturalOrder())
                .thenComparing(ProjectRest::getOrganizationId)
                .thenComparing(ProjectRest::getProjectName, Comparator.naturalOrder())
                .thenComparing(ProjectRest::getProjectId))
            .collect(Collectors.toList());
        final List<TaskRest> tasks = taskRepository.getListByMemberUserIdsWithMemberAndOrganization(
            Collections.singleton(userId)).stream()
            .map(TaskRest::new)
            .sorted(Comparator.comparing(TaskRest::getOrganizationName, Comparator.naturalOrder())
                .thenComparing(TaskRest::getOrganizationId)
                .thenComparing(TaskRest::getProjectName, Comparator.naturalOrder())
                .thenComparing(TaskRest::getProjectId)
                .thenComparing(TaskRest::getTitle, Comparator.naturalOrder())
                .thenComparing(TaskRest::getTaskId))
            .collect(Collectors.toList());
        return ResponseEntity.ok(new ConnectedObjects(projects, organizations, tasks));
    }

    public static class ConnectedObjects
    {
        private final List<ProjectRest> projects;
        private final List<OrganizationRest> organizations;
        private final List<TaskRest> tasks;

        private ConnectedObjects(final List<ProjectRest> projects,
            final List<OrganizationRest> organizations,
            final List<TaskRest> tasks)
        {
            this.projects = projects;
            this.organizations = organizations;
            this.tasks = tasks;
        }

        public List<ProjectRest> getProjects()
        {
            return projects;
        }

        public List<OrganizationRest> getOrganizations()
        {
            return organizations;
        }

        public List<TaskRest> getTasks()
        {
            return tasks;
        }

    }

    public static class ProjectRest
    {
        private final Project project;

        public ProjectRest(final Project project)
        {
            this.project = project;
        }

        public Long getOrganizationId()
        {
            return project.getOrganization().getId();
        }

        public Long getProjectId()
        {
            return project.getId();
        }

        public String getProjectName()
        {
            return project.getName();
        }

        public String getOrganizationName()
        {
            return project.getOrganization().getName();
        }

    }

    public static class OrganizationRest
    {
        private final Organization organization;

        private OrganizationRest(final Organization organization)
        {
            this.organization = organization;
        }

        public Long getId()
        {
            return organization.getId();
        }

        public String getName()
        {
            return organization.getName();
        }

    }

    public static class TaskRest
    {
        private final Task task;

        public TaskRest(final Task task)
        {
            this.task = task;
        }

        public Long getTaskId()
        {
            return task.getId();
        }

        public Long getOrganizationId()
        {
            return task.getOrganizationId();
        }

        public Long getProjectId()
        {
            return task.getProjectId();
        }

        public String getTitle()
        {
            return task.getTitle();
        }

        public String getColumnNameOrNull()
        {
            return ObjectUtils.accessNullable(task.getTaskColumn(), TaskColumn::getName);
        }

        public String getOrganizationName()
        {
            return task.getProject().getOrganization().getName();
        }

        public String getProjectName()
        {
            return task.getProject().getName();
        }

    }

    public static class ProfileEditRest
    {
        private String firstName;
        private String secondName;

        public ProfileEditRest()
        {
            // for Spring
        }

        public ProfileEditRest(String firstName, String secondName)
        {
            this.firstName = firstName;
            this.secondName = secondName;
        }

        public String getFirstName()
        {
            return firstName;
        }

        public void setFirstName(final String firstName)
        {
            this.firstName = firstName;
        }

        public String getSecondName()
        {
            return secondName;
        }

        public void setSecondName(final String secondName)
        {
            this.secondName = secondName;
        }

    }

}
