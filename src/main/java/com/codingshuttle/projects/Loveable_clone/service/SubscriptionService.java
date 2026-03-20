package com.codingshuttle.projects.Loveable_clone.service;

import com.codingshuttle.projects.Loveable_clone.dto.subscription.*;
import com.codingshuttle.projects.Loveable_clone.enums.SubscriptionStatus;
import com.stripe.model.Subscription;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public interface SubscriptionService {
     SubscriptionResponse getCurrentSubscription();

     void activateSubscription(Long userId, Long planId, String customerId, String subscriptionId);

     void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

     void cancelSubscription(String gatewaySubscriptionId);

     void renewSubscriptionPeriod(String subId, Instant periodStart, Instant periodEnd);

     void markSubscriptionDue(String subId);
}
