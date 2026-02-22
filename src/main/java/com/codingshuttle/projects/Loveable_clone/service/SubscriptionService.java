package com.codingshuttle.projects.Loveable_clone.service;

import com.codingshuttle.projects.Loveable_clone.dto.subscription.*;
import org.jspecify.annotations.Nullable;

public interface SubscriptionService {
     SubscriptionResponse getCurrentSubscription();

     CheckResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId);

     PortalResponse openCustomerPortal(Long userId);
}
