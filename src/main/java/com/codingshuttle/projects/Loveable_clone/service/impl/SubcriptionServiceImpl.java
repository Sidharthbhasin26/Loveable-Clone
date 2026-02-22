package com.codingshuttle.projects.Loveable_clone.service.impl;

import com.codingshuttle.projects.Loveable_clone.dto.subscription.CheckResponse;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.CheckoutRequest;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.PortalResponse;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.SubscriptionResponse;
import com.codingshuttle.projects.Loveable_clone.service.SubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class SubcriptionServiceImpl implements SubscriptionService {
    @Override
    public SubscriptionResponse getCurrentSubscription() {
        return null;
    }

    @Override
    public CheckResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId) {
        return null;
    }

    @Override
    public PortalResponse openCustomerPortal(Long userId) {
        return null;
    }
}
