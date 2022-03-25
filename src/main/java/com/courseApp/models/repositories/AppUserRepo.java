package com.courseApp.models.repositories;

import com.courseApp.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AppUserRepo extends JpaRepository<AppUser,String> {


        Optional<AppUser> findByEmail(String email);
        boolean existsByEmail(String email);
        boolean existsByName(String name);


}
