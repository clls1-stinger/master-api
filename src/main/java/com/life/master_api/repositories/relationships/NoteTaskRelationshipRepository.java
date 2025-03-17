package com.life.master_api.repositories.relationships;

import com.life.master_api.entities.relationships.NoteTaskRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteTaskRelationshipRepository extends JpaRepository<NoteTaskRelationship, Long> {
}