package com.life.master_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileAttachmentDto {
    private Long id;
    private String originalFileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private String entityType;
    private Long entityId;
    private Long userId;
    
    // No incluimos fileData para evitar transferir datos binarios grandes en las respuestas
}