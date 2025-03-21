package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Entity
@Table(name = "HabitHistory")
@Data
@EqualsAndHashCode(of = "historyId")
public class HabitHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    private Long versionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date creation;

    @Column(nullable = false)
    private Date timestamp;

    // Optional: modifiedBy
}