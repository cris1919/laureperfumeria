package com.laure.perfumeria.backend.service.auth;

import com.laure.perfumeria.backend.model.auth.dto.LoginRequest;
import com.laure.perfumeria.backend.model.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse iniciarSesion(LoginRequest request);
}
