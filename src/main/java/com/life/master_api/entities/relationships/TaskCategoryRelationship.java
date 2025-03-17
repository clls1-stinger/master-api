package com.life.master_api.entities.relationships;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Task;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TaskCategories")
@Data
public class TaskCategoryRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "TaskId")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "CategoryId")
    private Category category;

    public TaskCategoryRelationship() {
    }

    public TaskCategoryRelationship(Task task, Category category) {
        this.task = task;
        this.category = category;
    }
}