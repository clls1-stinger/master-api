package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

import com.life.master_api.entities.User;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Date creation;

    @Column(nullable = false)
    private Date timestamp; // Timestamp of the change

    // User relationship added for data isolation
}