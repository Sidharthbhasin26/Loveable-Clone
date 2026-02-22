package com.codingshuttle.projects.Loveable_clone.dto.subscription;

public record PlanResponse(
        Long id,
        String name,
        Integer maxProjects,
        Integer maxTokensPerDay,
        boolean unlimitedAi,
        boolean active
) {
}
