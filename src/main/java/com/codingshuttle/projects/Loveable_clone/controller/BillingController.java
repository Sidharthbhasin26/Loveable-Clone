package com.codingshuttle.projects.Loveable_clone.controller;

import com.codingshuttle.projects.Loveable_clone.dto.subscription.*;
import com.codingshuttle.projects.Loveable_clone.service.PaymentProcessor;
import com.codingshuttle.projects.Loveable_clone.service.PlanService;
import com.codingshuttle.projects.Loveable_clone.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BillingController {

    private final PlanService planService;
    private final SubscriptionService subscriptionService;
    private final PaymentProcessor paymentProcessor;

    @GetMapping("/api/plans")
    public ResponseEntity<PlanResponse> getAllPlans(){
        return ResponseEntity.ok(planService.getAllActivePlans());
    }

    @GetMapping("/api/me/subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription(){
        Long userId = 1L;
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription());

    }
    @PostMapping("/api/payments/checkout")
    public ResponseEntity<CheckResponse> createCheckoutResponse(
            @RequestBody CheckoutRequest request){
        return ResponseEntity.ok(paymentProcessor.createCheckoutSessionUrl(request));

    }
    @PostMapping("/api/stripe/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal(){
        Long userId = 1L;
        return ResponseEntity.ok(paymentProcessor.openCustomerPortal(userId));
    }

}
