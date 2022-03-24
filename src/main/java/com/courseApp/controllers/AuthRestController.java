package com.courseApp.controllers;

import com.courseApp.payloads.LoginRequest;
import com.courseApp.payloads.MessageResponse;
import com.courseApp.payloads.SignupRequest;
import com.courseApp.services.AppUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthRestController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AppUserDetailsService appUserDetailsService;
    @Autowired
    protected AuthenticationManager authenticationManager;
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        var authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        return ResponseEntity.ok(
                appUserDetailsService.login(authentication));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        var rez = appUserDetailsService.existsByIdEmail(signupRequest.getEmail(), signupRequest.getUsername());
        if (rez.equals("success")) {
            String pw=signupRequest.getPassword();
            signupRequest.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            appUserDetailsService.insertNewUser(signupRequest);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse(rez + "fully registered"));
        } else return ResponseEntity
                .badRequest()
                .body(new MessageResponse("uzer with such " + rez + " already exists"));
    }

}
