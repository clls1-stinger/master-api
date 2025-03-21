package com.life.master_api.repositories;

import com.life.master_api.entities.NoteHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteHistoryRepository extends JpaRepository<NoteHistory, Long> {
}