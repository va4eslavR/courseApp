package com.courseApp.models.repositories;

import com.courseApp.models.AppUser;
import com.sun.istack.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepo extends JpaRepository<AppUser,String> {
        Optional<AppUser> findByEmail(String email);
        @NotNull
        Optional<AppUser>findById(String id);
}
