package com.example.economicssimulatorserver.dto;

import java.io.Serializable;
import java.time.Instant;

public class PendingRegistration implements Serializable {

    public String username;

    public String email;

    public String passwordHash;

    public String code;

    public Instant expiresAt;

    public PendingRegistration(String username, String email, String passwordHash, String code, Instant expiresAt) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.code = code;
        this.expiresAt = expiresAt;
    }
}
