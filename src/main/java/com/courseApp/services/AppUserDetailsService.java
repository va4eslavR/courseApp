package com.courseApp.services;

import com.courseApp.models.AppUser;
import com.courseApp.models.AppUserDetails;
import com.courseApp.models.Role;
import com.courseApp.models.repositories.AppUserRepo;
import com.courseApp.models.repositories.RoleRepo;
import com.courseApp.payloads.JwtResponse;
import com.courseApp.payloads.SignupRequest;
import com.courseApp.utility.GrantAdminRoles;
import com.courseApp.utility.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class AppUserDetailsService implements UserDetailsService {
    @Autowired
    private AppUserRepo appUserRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private GrantAdminRoles grantAdminRoles;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var rez = appUserRepo.findByEmail(email).orElseThrow();
        return AppUserDetails.getAppUserDetailsVia(rez);
    }

    public void insertNewUser(SignupRequest signupRequest) {
        var user = new AppUser();
        user.setId(signupRequest.getEmail() + signupRequest.getUsername());
        user.setName(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setLastSeen(Timestamp.valueOf(LocalDateTime.now()));
        //user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setPassword(signupRequest.getPassword());
        var roles = new HashSet<Role>();
        roles.add(roleRepo.findByRole(grantAdminRoles.getRole(signupRequest.getClaims())).orElseThrow());
        user.setRole(roles);
        appUserRepo.save(user);
    }

    public String existsByIdEmail(String email, String name) {
        if (!appUserRepo.existsByEmail(email)) return "email";
        if (!appUserRepo.existsByName(name)) return "name";
        return "success";
    }
    public AppUser getById(String id){
        return appUserRepo.getById(id);
    }

    public JwtResponse login(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var jwt = jwtUtils.generateJwtToken(authentication);
        var userDetails = (AppUserDetails) authentication.getPrincipal();
        var roles = userDetails.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return new JwtResponse(
                jwt,
                "Bearer",
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getUsername(),
                roles);
    }
}
