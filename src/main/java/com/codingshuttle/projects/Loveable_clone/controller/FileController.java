package com.codingshuttle.projects.Loveable_clone.controller;


import com.codingshuttle.projects.Loveable_clone.dto.project.*;
import com.codingshuttle.projects.Loveable_clone.service.FileService;
import com.codingshuttle.projects.Loveable_clone.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/projects/{projectId}/files")

public class FileController {
    private FileService fileService;

    @GetMapping
    public ResponseEntity<List<FileNode>> getFileTree(@PathVariable Long projectId){
        Long userId = 1L;
        return ResponseEntity.ok(fileService.getFileTree(projectId , userId));
    }
    @GetMapping("/{*path}")
    public ResponseEntity<FileContentResponse> getFile(@PathVariable Long projectId ,
                                                       @PathVariable String path){
        Long userId = 1L;
        return ResponseEntity.ok(fileService.getFile(projectId , path , userId));
    }

}
