package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "NotesHistory")
@Data
public class NoteHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Column(nullable = false)
    private Long noteId; // Referencia al ID de la nota principal

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationTimestamp; // Timestamp de la modificación que creó esta entrada de historial
}