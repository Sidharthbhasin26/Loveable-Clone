package com.codingshuttle.projects.Loveable_clone.mapper;

import ch.qos.logback.core.model.ComponentModel;
import com.codingshuttle.projects.Loveable_clone.dto.project.ProjectResponse;
import com.codingshuttle.projects.Loveable_clone.dto.project.ProjectSummaryResponse;
import com.codingshuttle.projects.Loveable_clone.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel =  "spring")
public interface ProjectMapper {
    ProjectResponse toProjectResponse(Project project);

    @Mapping(target = "projectName" , source = "name")
    ProjectSummaryResponse toProjectSummaryResponse(Project project);

    List<ProjectSummaryResponse> toListProjectSummaryResponse(List<Project> projects);

}
