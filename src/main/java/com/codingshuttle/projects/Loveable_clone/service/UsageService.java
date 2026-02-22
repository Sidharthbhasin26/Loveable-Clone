package com.codingshuttle.projects.Loveable_clone.service;

import com.codingshuttle.projects.Loveable_clone.dto.subscription.PlanLimitResponse;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.UsageTodayResponse;
import org.jspecify.annotations.Nullable;

public interface UsageService {
     UsageTodayResponse getTodayUsageOfUser(Long userId);

     PlanLimitResponse getCurrentSubscriptionLimitsOfUser(Long userId);
}
