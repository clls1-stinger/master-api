package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "Notes")
@Data
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT") // Para mapear a tipo TEXT en PostgreSQL
    private String note;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;
}