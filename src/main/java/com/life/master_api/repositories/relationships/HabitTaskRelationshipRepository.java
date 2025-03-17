package com.life.master_api.repositories.relationships;

import com.life.master_api.entities.relationships.HabitTaskRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitTaskRelationshipRepository extends JpaRepository<HabitTaskRelationship, Long> {
}