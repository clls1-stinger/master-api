package com.life.master_api.repositories;

import com.life.master_api.entities.FileAttachment;
import com.life.master_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {
    List<FileAttachment> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<FileAttachment> findByEntityTypeAndEntityIdAndUser(String entityType, Long entityId, User user);
    Optional<FileAttachment> findByIdAndUser(Long id, User user);
    void deleteByEntityTypeAndEntityId(String entityType, Long entityId);
}