package com.life.master_api.repositories;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;

import java.util.List;

public interface HabitRepositoryCustom {
    List<Habit> findByCategories(Category category);
}