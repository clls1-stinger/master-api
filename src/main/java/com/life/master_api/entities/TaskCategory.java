package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "TaskCategories")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TaskCategory {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private TaskCategoryId id = new TaskCategoryId();

    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "TaskId")
    private Task task;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "CategoryId")
    private Category category;

    @Data
    @Embeddable
    @EqualsAndHashCode
    public static class TaskCategoryId implements java.io.Serializable {
        private Long taskId;
        private Long categoryId;
    }
}