package com.codingshuttle.projects.Loveable_clone.dto.auth;

public record SignupRequest(
        String email,
        String password,
        String name
) {
}
