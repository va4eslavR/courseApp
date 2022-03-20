package com.courseApp.services;

import com.courseApp.models.AppUserDetails;
import com.courseApp.models.repositories.AppUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {
    @Autowired
    private  AppUserRepo appUserRepo;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var rez=appUserRepo.findByEmail(email).orElseThrow();
        return AppUserDetails.getAppUserDetailsVia(rez);
    }
}
