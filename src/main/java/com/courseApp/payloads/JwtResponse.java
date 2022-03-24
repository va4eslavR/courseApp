package com.courseApp.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String ty;
    private String id;
    private String email;
    private String name;
    private List<String> roles;
}
