package com.codingshuttle.projects.Loveable_clone.service.impl;

import com.codingshuttle.projects.Loveable_clone.dto.auth.UserProfileResponse;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.PlanLimitResponse;
import com.codingshuttle.projects.Loveable_clone.dto.subscription.UsageTodayResponse;
import com.codingshuttle.projects.Loveable_clone.error.ResourceNotFoundException;
import com.codingshuttle.projects.Loveable_clone.repository.UserRepository;
import com.codingshuttle.projects.Loveable_clone.service.UsageService;
import com.codingshuttle.projects.Loveable_clone.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(makeFinal = true , level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

public class UserServiceImpl implements UserService , UserDetailsService {

    UserRepository userRepository;

    @Override
    public UserProfileResponse getProfile(Long userId) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() ->new ResourceNotFoundException("user" , username));
    }
}

