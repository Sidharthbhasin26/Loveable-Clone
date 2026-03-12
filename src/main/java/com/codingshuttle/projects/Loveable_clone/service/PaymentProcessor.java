package com.codingshuttle.projects.Loveable_clone.service;

import com.codingshuttle.projects.Loveable_clone.dto.subscription.CheckResponse;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.CheckoutRequest;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.PortalResponse;

public interface PaymentProcessor {

    CheckResponse createCheckoutSessionUrl(CheckoutRequest request);

    PortalResponse openCustomerPortal(Long userId);
}
