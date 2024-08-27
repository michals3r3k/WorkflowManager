package com.example.workflowmanager.db.organization.project.task;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class Task
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private LocalDateTime createTime;
    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;
    @OneToMany(mappedBy = "parentTask")
    private Set<Task> subTasks;


}
