package com.example.workflowmanager.rest.organization.project.task;

import com.example.workflowmanager.db.organization.project.task.TaskRelationRepository;
import com.example.workflowmanager.db.organization.project.task.TaskRepository;
import com.example.workflowmanager.entity.organization.project.task.*;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.utils.ObjectUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TaskRestFactory
{
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_DATE_TIME;

    private final TaskRepository taskRepository;
    private final TaskRelationRepository taskRelationRepository;

    public TaskRestFactory(final TaskRepository taskRepository,
        final TaskRelationRepository taskRelationRepository)
    {
        this.taskRepository = taskRepository;
        this.taskRelationRepository = taskRelationRepository;
    }
    public TaskRest get(final Long taskId)
    {
        final Task task = Iterables.getOnlyElement(taskRepository
            .getListByIdsWithRelationalTasksAndMembers(Collections.singleton(taskId)) );

        final List<TaskMemberRest> members = TaskColumnRestFactory.getMembers(task);
        final List<SubTaskRest> subTasks = getSubTasks(task);
        final List<TaskRelationRest> taskRelations = getTaskRelations(taskId);
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
        final TaskPriority priorityOrNull = task.getPriority();
        final String columnName = ObjectUtils.accessNullable(task.getTaskColumn(), TaskColumn::getName);
        final TaskPriority parentTaskPriorityOrNull = ObjectUtils.accessNullable(
            parentTaskOrNull, Task::getPriority);
        return new TaskRest(taskId, chatId, title, descriptionOrNull,
            createTime, creatorId, creatorName, startDateOrNull, finishDateOrNull,
            deadlineDateOrNull, parentTaskIdOrNull, parentTaskTitleOrNull,
            parentTaskPriorityOrNull, members, subTasks, taskRelations,
            priorityOrNull, columnName);
    }

    private static List<SubTaskRest> getSubTasks(final Task task)
    {
        return task.getSubTasks().stream()
            .map(subTask -> {
                final Long subTaskId = subTask.getId();
                final String title = subTask.getTitle();
                return new SubTaskRest(subTaskId, title);
            })
            .sorted(Comparator.comparing(SubTaskRest::getSubTaskId))
            .collect(Collectors.toList());
    }

    private List<TaskRelationRest> getTaskRelations(final Long taskId)
    {
        return taskRelationRepository
            .getListByTaskIds(Collections.singleton(taskId)).stream()
            .map(taskRelation -> getTaskRelation(taskRelation, taskId))
            .sorted(Comparator.comparing(TaskRelationRest::getRelationType)
                .thenComparing(TaskRelationRest::getTitle, Comparator.naturalOrder()))
            .collect(Collectors.toList());
    }

    private TaskRelationRest getTaskRelation(TaskRelation taskRelation, Long referenceTaskId)
    {
        final boolean relationOwner = taskRelation.getSourceTask().getId().equals(referenceTaskId);
        final Task displayedTask = relationOwner ? taskRelation.getTargetTask() : taskRelation.getSourceTask();
        final TaskRelationTypeRest relationTypeRest = TaskRelationTypeRest.valueOf(
            taskRelation.getId().getRelationType(), !relationOwner);
        return new TaskRelationRest(displayedTask.getId(), displayedTask.getTitle(), relationTypeRest,
            ObjectUtils.accessNullable(displayedTask.getTaskColumn(), TaskColumn::getName));
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
        private TaskPriority parentTaskPriorityOrNull;
        private List<TaskMemberRest> members;
        private List<SubTaskRest> subTasks;
        private List<TaskRelationRest> taskRelations;
        private TaskPriority priority;
        private String columnName;

        private TaskRest(final Long taskId, final Long chatId, final String title,
            final String descriptionOrNull, final String createTime,
            final Long creatorId, final String creatorName, final String startDateOrNull,
            final String finishDateOrNull, final String deadlineDateOrNull,
            final Long parentTaskIdOrNull, final String parentTaskTitleOrNull,
            final TaskPriority parentTaskPriorityOrNull, final List<TaskMemberRest> members,
            final List<SubTaskRest> subTasks, final List<TaskRelationRest> taskRelations,
            final TaskPriority priority, final String columnName)
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
            this.parentTaskPriorityOrNull = parentTaskPriorityOrNull;
            this.members = members;
            this.subTasks = subTasks;
            this.taskRelations = taskRelations;
            this.priority = priority;
            this.columnName = columnName;
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

        public List<TaskRelationRest> getTaskRelations()
        {
            return taskRelations;
        }

        public void setTaskRelations(final List<TaskRelationRest> taskRelations)
        {
            this.taskRelations = taskRelations;
        }

        public TaskPriority getPriority()
        {
            return priority;
        }

        public void setPriority(final TaskPriority priority)
        {
            this.priority = priority;
        }

        public String getColumnName()
        {
            return columnName;
        }

        public void setColumnName(final String columnName)
        {
            this.columnName = columnName;
        }

        public TaskPriority getParentTaskPriorityOrNull()
        {
            return parentTaskPriorityOrNull;
        }

        public void setParentTaskPriorityOrNull(
            final TaskPriority parentTaskPriorityOrNull)
        {
            this.parentTaskPriorityOrNull = parentTaskPriorityOrNull;
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

    public static class TaskRelationRest
    {
        private Long taskId;
        private String title;
        private TaskRelationTypeRest relationType;
        private String columnName;

        private TaskRelationRest(final Long taskId, final String title,
            final TaskRelationTypeRest relationType, final String columnName)
        {
            this.taskId = taskId;
            this.title = title;
            this.relationType = relationType;
            this.columnName = columnName;
        }

        public TaskRelationRest()
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

        public String getTitle()
        {
            return title;
        }

        public void setTitle(final String title)
        {
            this.title = title;
        }

        public TaskRelationTypeRest getRelationType()
        {
            return relationType;
        }

        public void setRelationType(final TaskRelationTypeRest relationType)
        {
            this.relationType = relationType;
        }

        public String getColumnName()
        {
            return columnName;
        }

        public void setColumnName(final String columnName)
        {
            this.columnName = columnName;
        }

    }

    public enum TaskRelationTypeRest
    {
        IS_RELATIVE_TO(TaskRelationType.IS_RELATIVE_TO, false),
        BLOCKS(TaskRelationType.BLOCKS, false),
        IS_BLOCKED_BY(TaskRelationType.BLOCKS, true);

        private static final Multimap<TaskRelationType, TaskRelationTypeRest> MAP =
            Multimaps.index(EnumSet.allOf(TaskRelationTypeRest.class), TaskRelationTypeRest::getType);

        private final TaskRelationType type;
        private final boolean reversed;

        TaskRelationTypeRest(final TaskRelationType type, final boolean reversed)
        {
            this.type = type;
            this.reversed = reversed;
        }

        public TaskRelationType getType()
        {
            return type;
        }

        public boolean isReversed()
        {
            return reversed;
        }

        public static TaskRelationTypeRest valueOf(final TaskRelationType type,
            final boolean reversed)
        {
            final Collection<TaskRelationTypeRest> relations = MAP.get(type);
            if(relations.isEmpty())
            {
                throw new NoSuchElementException();
            }
            if(relations.size() == 1)
            {
                return Iterables.getOnlyElement(relations);
            }
            return MAP.get(type).stream()
                .filter(relationType -> relationType.isReversed() == reversed)
                .findFirst()
                .orElseThrow();
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

}
