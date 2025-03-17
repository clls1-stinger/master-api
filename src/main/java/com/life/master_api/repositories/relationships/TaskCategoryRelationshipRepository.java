package com.life.master_api.repositories.relationships;

import com.life.master_api.entities.relationships.TaskCategoryRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCategoryRelationshipRepository extends JpaRepository<TaskCategoryRelationship, Long> {
}