package com.life.master_api.repositories.relationships;

import com.life.master_api.entities.relationships.NoteCategoryRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteCategoryRelationshipRepository extends JpaRepository<NoteCategoryRelationship, Long> {
}