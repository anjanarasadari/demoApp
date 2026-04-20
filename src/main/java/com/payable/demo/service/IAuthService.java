package com.payable.demo.service;

import com.payable.demo.dto.AuthResponse;
import com.payable.demo.dto.LoginRequest;
import com.payable.demo.dto.RegisterRequest;

public interface IAuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
