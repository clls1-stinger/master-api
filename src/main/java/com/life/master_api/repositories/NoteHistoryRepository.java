package com.life.master_api.repositories;

import com.life.master_api.entities.NoteHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteHistoryRepository extends JpaRepository<NoteHistory, Long> {

    List<NoteHistory> findByNote_IdOrderByVersionIdDesc(Long noteId);
    List<NoteHistory> findByNote_IdAndVersionId(Long noteId, Long versionId);
}