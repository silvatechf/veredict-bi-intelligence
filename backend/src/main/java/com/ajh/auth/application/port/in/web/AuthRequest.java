// backend/src/main/java/com/ajh/auth/adapter/in/web/AuthRequest.java
package com.ajh.auth.application.port.in.web;

import lombok.Data;

// Usado para as requisições de Login e Register
@Data
public class AuthRequest {
    private String email;
    private String password;
}