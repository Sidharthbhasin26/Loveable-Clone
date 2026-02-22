package com.codingshuttle.projects.Loveable_clone.dto.member;

import com.codingshuttle.projects.Loveable_clone.enums.ProjectRole;

public record InviteMemberRequest(
        String email,
        ProjectRole role
) {
}
