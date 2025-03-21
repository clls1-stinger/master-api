package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Entity
@Table(name = "NoteHistory")
@Data
@EqualsAndHashCode(of = "historyId")
public class NoteHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    private Long versionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note; // Relationship to the original Note entity

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String noteContent; // Renamed field to avoid conflict and improve clarity

    @Column(nullable = false)
    private Date creation;

    @Column(nullable = false)
    private Date timestamp;

    // Optional: modifiedBy
}