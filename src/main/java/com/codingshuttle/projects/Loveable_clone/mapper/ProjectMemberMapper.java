package com.codingshuttle.projects.Loveable_clone.mapper;

import ch.qos.logback.core.model.ComponentModel;
import com.codingshuttle.projects.Loveable_clone.dto.member.MemberResponse;
import com.codingshuttle.projects.Loveable_clone.entity.ProjectMember;
import com.codingshuttle.projects.Loveable_clone.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel  = "spring")
public interface ProjectMemberMapper {

    @Mapping(target = "userId" , source = "id")
    @Mapping(target = "projectRole" , constant = "OWNER")
    MemberResponse toProjectMemberResponseFromOwner (User owner);

    @Mapping(target = "userId" , source = "user.id")
    @Mapping(target = "username" , source = "user.username")
    @Mapping(target = "name" , source = "user.name")
    MemberResponse toProjectMemberResponseFromMember(ProjectMember projectMember);

}
