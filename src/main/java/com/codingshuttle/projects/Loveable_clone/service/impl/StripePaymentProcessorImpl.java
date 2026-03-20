package com.codingshuttle.projects.Loveable_clone.service.impl;

import com.codingshuttle.projects.Loveable_clone.dto.subscription.CheckResponse;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.CheckoutRequest;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.PortalResponse;
import com.codingshuttle.projects.Loveable_clone.entity.Plan;
import com.codingshuttle.projects.Loveable_clone.entity.User;
import com.codingshuttle.projects.Loveable_clone.enums.SubscriptionStatus;
import com.codingshuttle.projects.Loveable_clone.error.BadRequestException;
import com.codingshuttle.projects.Loveable_clone.error.ResourceNotFoundException;
import com.codingshuttle.projects.Loveable_clone.repository.PlanRepository;
import com.codingshuttle.projects.Loveable_clone.repository.UserRepository;
import com.codingshuttle.projects.Loveable_clone.security.AuthUtil;
import com.codingshuttle.projects.Loveable_clone.service.PaymentProcessor;
import com.codingshuttle.projects.Loveable_clone.service.SubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
//@FieldDefaults(makeFinal = true , level = AccessLevel.PRIVATE)
public class StripePaymentProcessorImpl implements PaymentProcessor {

    @Value("${client.url}")
    private String frontendUrl;

   private final AuthUtil authUtil;
   private final PlanRepository planRepository;
   private final UserRepository userRepository;
   private final SubscriptionService subscriptionService;


    @Override
    public CheckResponse createCheckoutSessionUrl(CheckoutRequest request) {

        Plan plan = planRepository.findById(request.planId()).orElseThrow(() ->
                new ResourceNotFoundException("Plan",request.planId().toString()));

        Long userId = authUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));


        var params = SessionCreateParams.builder()
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
                .putMetadata("planId" , plan.getId().toString());

        try {

           String stripeCustomerId = user.getStripeCustomerId();
           if (stripeCustomerId == null || stripeCustomerId.isBlank()){
               params.setCustomerEmail(user.getUsername());
           }else {
               params.setCustomer(stripeCustomerId);
           }

            Session session = Session.create(params.build());
            return new CheckResponse(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public PortalResponse openCustomerPortal() {
        Long userId = authUtil.getCurrentUserId();
        User user = getUser(userId);

        String stripeCustomerId = user.getStripeCustomerId();
        if (stripeCustomerId == null || stripeCustomerId.isEmpty()){
            throw new BadRequestException("User Does not have a Stripe CustomerId , userId : {}" + userId);
        }
        try {
            var portalSession = com.stripe.model.billingportal.Session.create(
                    com.stripe.param.billingportal.SessionCreateParams.builder()
                            .setCustomer(stripeCustomerId)
                            .setReturnUrl(frontendUrl)
                            .build()
            );
            return new PortalResponse(portalSession.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {
        log.debug("Handling Stripe Events : {}" , type);
        switch (type){
            case "checkout.session.completed" -> handleCheckoutSessionCompleted((Session) stripeObject, metadata);
            case "customer.subscription.updated" -> handleCustomerSubscriptionUpdated((Subscription) stripeObject);
            case "customer.subscription.deleted" -> handleCustomerSubscriptionDeleted((Subscription) stripeObject);
            case "invoice.paid" -> handleInvoicePaid((Invoice) stripeObject);
            case "invoice.payment_failed" -> handleInvoicePaymentFailed((Invoice) stripeObject);
            default -> log.debug("Ignoring the event: {}", type);
        }
    }

    private void handleCheckoutSessionCompleted(Session session , Map<String, String> metadata){

            if (session == null) {
                log.error("Session is null");
                return;
            }

            // metadata parameter te depend mat kar - session toh seedha lo
            Map<String, String> sessionMetadata = session.getMetadata();

            log.info("Session metadata: {}", sessionMetadata); // debug log

            if (sessionMetadata == null || sessionMetadata.isEmpty()) {
                log.error("Session metadata is EMPTY! userId aur planId nahi mila");
                return;
            }

            String userIdStr = sessionMetadata.get("userId");
            String planIdStr = sessionMetadata.get("planId");

            if (userIdStr == null || planIdStr == null) {
                log.error("userId={}, planId={} -- koi ek null hai!", userIdStr, planIdStr);
                return;
            }

            Long userId = Long.parseLong(userIdStr);
            Long planId = Long.parseLong(planIdStr);

            String subscriptionId = session.getSubscription();
            String customerId = session.getCustomer();

            User user = getUser(userId);
            if (user.getStripeCustomerId() == null) {
                user.setStripeCustomerId(customerId);
                userRepository.save(user);
            }

            subscriptionService.activateSubscription(userId, planId, customerId, subscriptionId);
        }
/*        if(session == null){
            log.error("Session Object is Null");
            return;
        }

        log.info("Metadata received: {}", metadata);
        log.info("Session metadata: {}", session.getMetadata());

        Long userId = Long.parseLong(metadata.get("user_Id"));
        Long planId = Long.parseLong(metadata.get("plan_Id"));


       String subscriptionId =  session.getSubscription();
        String customerId =  session.getCustomer();

       User user = getUser(userId);
       if (user.getStripeCustomerId() == null){
           user.setStripeCustomerId(customerId);
           userRepository.save(user);
       }
       subscriptionService.activateSubscription(userId , planId , customerId , subscriptionId);
    }*/

    private void handleCustomerSubscriptionUpdated(Subscription subscription){
        if (subscription == null){
            log.info("Subscription is null ");
        }
        SubscriptionStatus status = mapStripeStatusToEnum(subscription.getStatus());
        if (status == null) {
            log.warn("Unknown status '{}' for subscription {}", subscription.getStatus(), subscription.getId());
            return;
        }

        SubscriptionItem item = subscription.getItems().getData().get(0);
        Instant periodStart = toInstant(item.getCurrentPeriodStart());
        Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

        Long planId = resolvePlanId(item.getPrice());

        subscriptionService.updateSubscription(
                subscription.getId(), status, periodStart, periodEnd,
                subscription.getCancelAtPeriodEnd(), planId
        );


    }


    private void handleCustomerSubscriptionDeleted(Subscription subscription){
        if (subscription == null){
            log.info("Subscription is Empty");

        }
        subscriptionService.cancelSubscription(subscription.getId());

    }

    private void handleInvoicePaid(Invoice invoice){
        String subId = extractSubscriptionId(invoice);
        if (subId == null) return;
        try {
            Subscription subscription = Subscription.retrieve(subId);
            var item = subscription.getItems().getData().get(0);

            Instant periodStart = toInstant(item.getCurrentPeriodStart());
            Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

            subscriptionService.renewSubscriptionPeriod(
                    subId,
                    periodStart,
                    periodEnd
            );

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }


    }

    private void handleInvoicePaymentFailed(Invoice invoice){
        String subId = extractSubscriptionId(invoice);
        if (subId == null) return;

        subscriptionService.markSubscriptionDue(subId);

    }


    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
    }

    private SubscriptionStatus mapStripeStatusToEnum(String status) {
        return switch (status) {
            case "active" -> SubscriptionStatus.ACTIVE;
            case "trialing" -> SubscriptionStatus.TRAILING;
            case "past_due", "unpaid", "paused", "incomplete_expired" -> SubscriptionStatus.PAST_DUE;
            case "canceled" -> SubscriptionStatus.CANCELED;
            case "incomplete" -> SubscriptionStatus.INCOMPLETE;
            default -> {
                log.warn("Unmapped Stripe status: {}", status);
                yield null;
            }
        };

    }
    private Instant toInstant(Long epoch) {
        return epoch != null ? Instant.ofEpochSecond(epoch) : null;
    }

        private Long resolvePlanId(Price price) {
            if (price == null || price.getId() == null) return null;
            return planRepository.findByStripePriceId(price.getId())
                    .map(Plan::getId)
                    .orElse(null);
        }

    private String extractSubscriptionId(Invoice invoice) {
        var parent = invoice.getParent();
        if (parent == null) return null;

        var subDetails = parent.getSubscriptionDetails();
        if (subDetails == null) return null;

        return subDetails.getSubscription();
    }


}
