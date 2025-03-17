package com.life.master_api.repositories.relationships;

import com.life.master_api.entities.relationships.HabitNoteRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitNoteRelationshipRepository extends JpaRepository<HabitNoteRelationship, Long> {
}