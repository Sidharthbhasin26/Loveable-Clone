package com.codingshuttle.projects.Loveable_clone.service.impl;

import com.codingshuttle.projects.Loveable_clone.dto.subscription.CheckResponse;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.CheckoutRequest;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.PortalResponse;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.SubscriptionResponse;
import com.codingshuttle.projects.Loveable_clone.entity.Plan;
import com.codingshuttle.projects.Loveable_clone.entity.Subscription;
import com.codingshuttle.projects.Loveable_clone.entity.User;
import com.codingshuttle.projects.Loveable_clone.enums.SubscriptionStatus;
import com.codingshuttle.projects.Loveable_clone.error.ResourceNotFoundException;
import com.codingshuttle.projects.Loveable_clone.mapper.SubscriptionMapper;
import com.codingshuttle.projects.Loveable_clone.repository.PlanRepository;
import com.codingshuttle.projects.Loveable_clone.repository.SubscriptionRepository;
import com.codingshuttle.projects.Loveable_clone.repository.UserRepository;
import com.codingshuttle.projects.Loveable_clone.security.AuthUtil;
import com.codingshuttle.projects.Loveable_clone.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubcriptionServiceImpl implements SubscriptionService {

    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    @Override
    public SubscriptionResponse getCurrentSubscription() {
        Long userId = authUtil.getCurrentUserId();

       var currentSubscription =  subscriptionRepository.findByUserIdAndStatusIn(userId , Set.of(
                SubscriptionStatus.ACTIVE ,
               SubscriptionStatus.TRAILING ,
               SubscriptionStatus.PAST_DUE)).orElse(new Subscription());

       return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String customerId, String subscriptionId) {
        boolean exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);

        if (exists) return;

        User user = getUser(userId);
        Plan plan = getplan(planId);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscriptionId(subscriptionId)
                .status(SubscriptionStatus.INCOMPLETE)
                .cancelAtPeriodEnd(false)
                .build();
        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        boolean hasSubscriptionUpdated = false;

        if (status != null && status != subscription.getStatus()){
            subscription.setStatus(status);
            hasSubscriptionUpdated = true;
        }

        if (periodStart != null && !periodStart.equals(subscription.getCurrentPeriodStart())){
            subscription.setCurrentPeriodStart(periodStart);
            hasSubscriptionUpdated = true;
        }

        if (periodEnd != null && !periodEnd.equals(subscription.getCurrentPeriodEnd())){
            subscription.setCurrentPeriodEnd(periodEnd);
            hasSubscriptionUpdated = true;
        }

        if (cancelAtPeriodEnd != null && cancelAtPeriodEnd != subscription.getCancelAtPeriodEnd()){
            subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
            hasSubscriptionUpdated = true;
        }

        if (planId != null && !planId.equals(subscription.getPlan().getId())){
            Plan newPlan = getplan(planId);
            subscription.setPlan(newPlan);
            hasSubscriptionUpdated = true;
        }
        if(hasSubscriptionUpdated) {
            log.debug("Subscription has been updated: {}", gatewaySubscriptionId);
            subscriptionRepository.save(subscription);
        }

    }

    @Override
    public void cancelSubscription(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);
        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscriptionRepository.save(subscription);

    }

    @Override
    public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {

        // Subscription dhoondho, na mile toh quietly return kar do
        var subscriptionOpt = subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId);

        if (subscriptionOpt.isEmpty()) {
            log.warn("Subscription nahi mili DB mein: {} — invoice.paid ignore kar rahe hain", gatewaySubscriptionId);
            return;  // crash nahi karega
        }

        Subscription subscription = subscriptionOpt.get();

        Instant newStart = periodStart != null ? periodStart : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE
                || subscription.getStatus() == SubscriptionStatus.INCOMPLETE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }

        subscriptionRepository.save(subscription);
    }
    /*public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        Instant newStart = periodStart != null ? periodStart : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if(subscription.getStatus() == SubscriptionStatus.PAST_DUE || subscription.getStatus() == SubscriptionStatus.INCOMPLETE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }

        subscriptionRepository.save(subscription);


    }*/


    @Override
    public void markSubscriptionDue(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);
        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE){
            log.debug("Subscription is already past due, gatewaySubscriptionId: {}", gatewaySubscriptionId);
            return;
        }
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);

        // send message to the user
    }
    //utility methods
    private User getUser (Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User" , userId.toString()));
    }

    private Plan getplan(Long planId){
        return planRepository.findById(planId)
                .orElseThrow(()-> new ResourceNotFoundException("Plan" , planId.toString()));
    }

    private Subscription getSubscription(String gatewaySubscriptionId) {
        return subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", gatewaySubscriptionId));
    }

}
