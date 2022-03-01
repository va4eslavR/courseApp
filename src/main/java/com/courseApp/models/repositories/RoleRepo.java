package com.courseApp.models.repositories;

import com.courseApp.models.Role;
import com.courseApp.models.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleEnum roleName);
}
