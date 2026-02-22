package com.codingshuttle.projects.Loveable_clone.service;

import com.codingshuttle.projects.Loveable_clone.dto.subscription.PlanResponse;
import org.jspecify.annotations.Nullable;

public interface PlanService {
    @Nullable PlanResponse getAllActivePlans();
}
