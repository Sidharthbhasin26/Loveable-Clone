package com.codingshuttle.projects.Loveable_clone.service.impl;

import com.codingshuttle.projects.Loveable_clone.dto.subscription.CheckResponse;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.CheckoutRequest;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.PortalResponse;
import com.codingshuttle.projects.Loveable_clone.entity.Plan;
import com.codingshuttle.projects.Loveable_clone.error.ResourceNotFoundException;
import com.codingshuttle.projects.Loveable_clone.repository.PlanRepository;
import com.codingshuttle.projects.Loveable_clone.security.AuthUtil;
import com.codingshuttle.projects.Loveable_clone.service.PaymentProcessor;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
//@FieldDefaults(makeFinal = true , level = AccessLevel.PRIVATE)
public class StripePaymentProcessorImpl implements PaymentProcessor {

    @Value("${client.url}")
    private String frontendUrl;

   private final AuthUtil authUtil;
   private final PlanRepository planRepository;


    @Override
    public CheckResponse createCheckoutSessionUrl(CheckoutRequest request) {

        Plan plan = planRepository.findById(request.planId()).orElseThrow(() ->
                new ResourceNotFoundException("Plan",request.planId().toString()));

        Long userId = authUtil.getCurrentUserId();

        SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(plan.getStripePriceId())
                                .setQuantity(1L).build())

                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSubscriptionData(
                        SessionCreateParams.SubscriptionData.builder()
                                .setBillingMode(
                                        SessionCreateParams.SubscriptionData.BillingMode.builder()
                                                .setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE)
                                                .build()
                                )
                                .build()
                )
                .setSuccessUrl(frontendUrl+ "/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl+"/cancel.html")
                .putMetadata("userId" , userId.toString())
                .putMetadata("planId" , plan.getId().toString())
                .build();
        try {
            Session session = Session.create(params);
            return new CheckResponse(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public PortalResponse openCustomerPortal(Long userId) {
        return null;
    }
}
