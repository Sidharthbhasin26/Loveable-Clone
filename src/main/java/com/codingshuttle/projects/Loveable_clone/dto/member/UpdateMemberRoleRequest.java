package com.codingshuttle.projects.Loveable_clone.dto.member;

import com.codingshuttle.projects.Loveable_clone.enums.ProjectRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(@NotNull ProjectRole role) {
}
