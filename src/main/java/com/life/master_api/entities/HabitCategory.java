package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "HabitCategories")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HabitCategory {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private HabitCategoryId id = new HabitCategoryId();

    @ManyToOne
    @MapsId("habitId")
    @JoinColumn(name = "HabitId")
    private Habit habit;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "CategoryId")
    private Category category;

    @Data
    @Embeddable
    @EqualsAndHashCode
    public static class HabitCategoryId implements java.io.Serializable {
        private Long habitId;
        private Long categoryId;
    }
}