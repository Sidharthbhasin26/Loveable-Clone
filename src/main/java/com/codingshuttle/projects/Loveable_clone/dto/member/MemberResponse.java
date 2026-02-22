package com.codingshuttle.projects.Loveable_clone.dto.member;

import com.codingshuttle.projects.Loveable_clone.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
        Long userId,
        String email,
        String name,
        String avatarUrl,
        ProjectRole projectRole,
        Instant invitedAt
) {
}
