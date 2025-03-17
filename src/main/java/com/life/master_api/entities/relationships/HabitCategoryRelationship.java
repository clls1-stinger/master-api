package com.life.master_api.entities.relationships;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "HabitCategories")
@Data
public class HabitCategoryRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "HabitId")
    private Habit habit;

    @ManyToOne
    @JoinColumn(name = "CategoryId")
    private Category category;

    public HabitCategoryRelationship() {
    }

    public HabitCategoryRelationship(Habit habit, Category category) {
        this.habit = habit;
        this.category = category;
    }
}