package com.codingshuttle.projects.Loveable_clone.service;

import com.codingshuttle.projects.Loveable_clone.dto.project.FileContentResponse;
import com.codingshuttle.projects.Loveable_clone.dto.project.FileNode;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface FileService {

     List<FileNode> getFileTree(Long projectId, Long userId);

     FileContentResponse getFile(Long projectId, String path, Long userId);
}
