package com.courseApp.payloads;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SignupRequest {
    private String username;
    private String email;
    private String password;

    public Map<String, String> getClaims() {
        return new HashMap<>() {
            {
                put("username", username);
                put("email", email);
            }
        };
    }
}
