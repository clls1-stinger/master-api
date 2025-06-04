package com.life.master_api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Entity
@Table(name = "FileAttachments")
@Data
@EqualsAndHashCode(of = "id")
public class FileAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;

    // Contenido del archivo almacenado como BYTEA en PostgreSQL
    @Lob
    @Column(name = "file_data", nullable = false)
    private byte[] fileData;

    // Relaciones polimórficas usando discriminador
    @Column(name = "entity_type")
    private String entityType; // "CATEGORY", "TASK", "NOTE", "HABIT"

    @Column(name = "entity_id")
    private Long entityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
}