package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Entity
@Table(name = "CategoryHistory")
@Data
@EqualsAndHashCode(of = "historyId")
public class CategoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    private Long versionId; // Consider using a more robust versioning system (e.g., UUID, sequence)

    @ManyToOne(fetch = FetchType.LAZY) // Important: Use FetchType.LAZY for performance
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // Reference to the original Category entity

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Date creation;

    @Column(nullable = false)
    private Date timestamp; // Timestamp of the change

    // Optional: Add a user_id or modified_by field to track who made the change
    // private Long modifiedBy;
}