
package com.codingshuttle.projects.Loveable_clone.security;

import com.codingshuttle.projects.Loveable_clone.enums.ProjectPermission;
import com.codingshuttle.projects.Loveable_clone.enums.ProjectRole;
import com.codingshuttle.projects.Loveable_clone.repository.ProjectMemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component("security")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true , level = AccessLevel.PRIVATE)
public class SecurityExpression {

    ProjectMemberRepository projectMemberRepository;
    AuthUtil authUtil;

    private boolean hasPermission (Long projectId , ProjectPermission permission){
        Long userId = authUtil.getCurrentUserId();

        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId,userId).
                map(role -> role.getPermissions().contains(ProjectPermission.VIEW))
                .orElse(false);
    }

    public boolean canViewProject(Long projectId) {
        return hasPermission(projectId , ProjectPermission.VIEW);
    }

    public boolean canEditProject(Long projectId){
        return hasPermission(projectId , ProjectPermission.EDIT);
    }

    public boolean canDeleteProject(Long projectId){
        return hasPermission(projectId , ProjectPermission.DELETE);
    }

    public boolean canViewMembers(Long projectId){
        return hasPermission(projectId , ProjectPermission.VIEW_MEMBERS);
    }

    public boolean canManageMembers(Long projectId){
        return hasPermission(projectId , ProjectPermission.MANAGE_MEMBERS);
    }

}

