package com.courseApp.controllers;

import com.courseApp.models.AppUser;
import com.courseApp.models.AppUserDetails;
import com.courseApp.models.Role;
import com.courseApp.models.repositories.AppUserRepo;
import com.courseApp.models.repositories.RoleRepo;
import com.courseApp.payloads.JwtResponse;
import com.courseApp.payloads.LoginRequest;
import com.courseApp.payloads.MessageResponse;
import com.courseApp.payloads.SignupRequest;
import com.courseApp.services.oauth2customization.GrantAdminRoles;
import com.courseApp.utility.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*",maxAge = 3600)
public class AuthRestController {
    private  final AuthenticationManager authenticationManager;
    private final AppUserRepo appUserRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final GrantAdminRoles grantAdminRoles;
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
@Autowired
    public AuthRestController(AuthenticationManager authenticationManager, AppUserRepo appUserRepo, RoleRepo roleRepo, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, GrantAdminRoles grantAdminRoles) {
        this.authenticationManager = authenticationManager;
        this.appUserRepo = appUserRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.grantAdminRoles = grantAdminRoles;
    }
    @PostMapping ("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        logger.info("in signin method before actions line 52");

        Authentication authentication=authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername()
                        ,loginRequest.getPassword()));
        logger.info("in signin method setting authentication line 53-55 success");
                        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.info("in signin method setting authentication line 56 success");
                        String jwt= jwtUtils.generateJwtToken(authentication);
        logger.info("in signin method setting authentication line 58 success");
        AppUserDetails userDetails=(AppUserDetails) authentication.getPrincipal();
        List<String> roles= userDetails.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
                        return ResponseEntity.ok(new JwtResponse(jwt,"Bearer",
                                userDetails.getId(),
                                userDetails.getEmail(),
                                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        if (appUserRepo.existsByEmail(signupRequest.getEmail())||appUserRepo.existsByName(signupRequest.getUsername()))
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("User already exists"));

        var user=new AppUser();
        user.setId(signupRequest.getEmail()+signupRequest.getUsername());
        user.setName(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        var roles=new HashSet<Role>();
        roles.add(roleRepo.findByRole(grantAdminRoles.getRole(signupRequest.getClaims())).orElseThrow());
        user.setRole(roles);
        appUserRepo.save(user);
        return  ResponseEntity.ok(new MessageResponse("Successfully registered"));
    }

}
