package com.codingshuttle.projects.Loveable_clone.service.impl;

import com.codingshuttle.projects.Loveable_clone.dto.project.ProjectRequest;
import com.codingshuttle.projects.Loveable_clone.dto.project.ProjectResponse;
import com.codingshuttle.projects.Loveable_clone.dto.project.ProjectSummaryResponse;
import com.codingshuttle.projects.Loveable_clone.entity.Project;
import com.codingshuttle.projects.Loveable_clone.entity.User;
import com.codingshuttle.projects.Loveable_clone.mapper.ProjectMapper;
import com.codingshuttle.projects.Loveable_clone.repository.ProjectRepository;
import com.codingshuttle.projects.Loveable_clone.repository.UserRepository;
import com.codingshuttle.projects.Loveable_clone.service.ProjectService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true , level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;

    @Override
    public ProjectResponse createProject(ProjectRequest request, Long userId) {
//this code is basically for creating the project
        User owner = userRepository.findById(userId).orElseThrow();
        Project project = Project.builder()
                .name(request.name())
                .owner(owner)
                .isPublic(false)
                .build();

        project = projectRepository.save(project);
//this code is return converting part of the Entity into DTO with the help of mapStruct
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {

        var projects = projectRepository.findAllAccessibleByUser(userId);
        return projectMapper.toListProjectSummaryResponse(projects);

        //this method of converting service into DTO Output , looks liitle confusing , so we are going to another one
        /*return projectRepository.findAllAccessibleByUser(userId)
                .stream()
                .map(projectMapper::toProjectSummaryResponse)
                .collect(Collectors.toList());*/
    }

    @Override
    public ProjectResponse getUserProjectById(Long id, Long userId) {
       Project project = getAccessibleProjectById(id,userId);
        return projectMapper.toProjectResponse(project);
    }



    @Override
    public ProjectResponse updateProject(Long id, ProjectRequest request, Long userId) {
        Project project = getAccessibleProjectById(id,userId);
        project.setName(request.name());
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public void softDelete(Long id, Long userId) {
        Project project = getAccessibleProjectById(id,userId);

        if(!project.getOwner().getId().equals(userId)){
            throw new RuntimeException("You are not allowed to delete");
        }
        project.setDeletedAt(Instant.now());
        projectRepository.save(project);

    }




    //INTERNAL METHOD
    public Project getAccessibleProjectById(Long projectId , Long userId){
        return projectRepository.findAccessibleProjectById(projectId,userId).orElseThrow();
    }
}
