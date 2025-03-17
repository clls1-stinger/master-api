package com.life.master_api.repositories.relationships;

import com.life.master_api.entities.relationships.HabitCategoryRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitCategoryRelationshipRepository extends JpaRepository<HabitCategoryRelationship, Long> {
}