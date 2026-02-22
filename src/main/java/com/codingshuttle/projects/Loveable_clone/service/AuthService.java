package com.codingshuttle.projects.Loveable_clone.service;

import com.codingshuttle.projects.Loveable_clone.dto.auth.AuthResponse;
import com.codingshuttle.projects.Loveable_clone.dto.auth.LoginRequest;
import com.codingshuttle.projects.Loveable_clone.dto.auth.SignupRequest;



public interface AuthService {
     AuthResponse signup(SignupRequest request);

     AuthResponse login(LoginRequest request);
}
