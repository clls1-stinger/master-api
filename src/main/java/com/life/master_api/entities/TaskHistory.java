package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "TasksHistory")
@Data
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Column(nullable = false)
    private Long taskId; // Referencia al ID de la tarea principal

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationTimestamp; // Timestamp de la modificación que creó esta entrada de historial
}