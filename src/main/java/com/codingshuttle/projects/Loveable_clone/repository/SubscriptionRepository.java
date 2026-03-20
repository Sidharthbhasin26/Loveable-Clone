package com.codingshuttle.projects.Loveable_clone.repository;

import com.codingshuttle.projects.Loveable_clone.entity.Subscription;
import com.codingshuttle.projects.Loveable_clone.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import java.lang.ScopedValue;
import java.util.Optional;
import java.util.Set;


public interface SubscriptionRepository extends JpaRepository<Subscription , Long> {
    Optional <Subscription>findByUserIdAndStatusIn(Long userId, Set<SubscriptionStatus> statusSet);


    Optional <Subscription> findByStripeSubscriptionId(String gatewaySubscriptionId);

    boolean existsByStripeSubscriptionId(String subscriptionId);
}
