package com.life.master_api.controllers;

import com.life.master_api.dto.FileAttachmentDto;
import com.life.master_api.entities.FileAttachment;
import com.life.master_api.entities.User;
import com.life.master_api.exceptions.ResourceNotFoundException;
import com.life.master_api.repositories.FileAttachmentRepository;
import com.life.master_api.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "File Attachment API", description = "Endpoints for managing file attachments")
@SecurityRequirement(name = "bearerAuth")
public class FileAttachmentController {

    @Autowired
    private FileAttachmentRepository fileAttachmentRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/upload")
    @Operation(summary = "Upload file", description = "Upload a file and attach it to an entity")
    public ResponseEntity<FileAttachmentDto> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") Long entityId) throws IOException {

        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authentication.getName()));

        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setOriginalFileName(file.getOriginalFilename());
        fileAttachment.setFileType(file.getContentType());
        fileAttachment.setFileSize(file.getSize());
        fileAttachment.setUploadDate(new Date()); // Usar java.util.Date en lugar de LocalDateTime
        fileAttachment.setFileData(file.getBytes());
        fileAttachment.setEntityType(entityType);
        fileAttachment.setEntityId(entityId);
        fileAttachment.setUser(user);

        FileAttachment savedFile = fileAttachmentRepository.save(fileAttachment);

        // Convertir Date a LocalDateTime para el DTO
        LocalDateTime uploadDateTime = savedFile.getUploadDate() != null ? 
                savedFile.getUploadDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : 
                null;
                
        FileAttachmentDto fileAttachmentDto = new FileAttachmentDto(
                savedFile.getId(),
                savedFile.getOriginalFileName(),
                savedFile.getFileType(),
                savedFile.getFileSize(),
                uploadDateTime,
                savedFile.getEntityType(),
                savedFile.getEntityId(),
                user.getId()
        );

        return new ResponseEntity<>(fileAttachmentDto, HttpStatus.CREATED);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Get files by entity", description = "Get all files attached to a specific entity")
    public ResponseEntity<List<FileAttachmentDto>> getFilesByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {

        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authentication.getName()));

        List<FileAttachment> files = fileAttachmentRepository.findByEntityTypeAndEntityIdAndUser(entityType, entityId, user);

        // Agregar log para depuración
        System.out.println("Archivos encontrados: " + files.size());
        
        List<FileAttachmentDto> fileAttachmentDtos = files.stream()
                .map(file -> {
                    // Convertir Date a LocalDateTime para el DTO
                    LocalDateTime uploadDateTime = file.getUploadDate() != null ? 
                            file.getUploadDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : 
                            null;
                            
                    return new FileAttachmentDto(
                        file.getId(),
                        file.getOriginalFileName(),
                        file.getFileType(),
                        file.getFileSize(),
                        uploadDateTime,
                        file.getEntityType(),
                        file.getEntityId(),
                        user.getId()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(fileAttachmentDtos);
    }

    @GetMapping("/download/{fileId}")
    @Operation(summary = "Download file", description = "Download a file by its ID")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authentication.getName()));

        FileAttachment fileAttachment = fileAttachmentRepository.findByIdAndUser(fileId, user)
                .orElseThrow(() -> new ResourceNotFoundException("File", "id", fileId));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileAttachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileAttachment.getOriginalFileName() + "\"")
                .body(new ByteArrayResource(fileAttachment.getFileData()));
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "Delete file", description = "Delete a file by its ID")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authentication.getName()));

        FileAttachment fileAttachment = fileAttachmentRepository.findByIdAndUser(fileId, user)
                .orElseThrow(() -> new ResourceNotFoundException("File", "id", fileId));

        fileAttachmentRepository.delete(fileAttachment);

        return ResponseEntity.ok("File deleted successfully");
    }
}