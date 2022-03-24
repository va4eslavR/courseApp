package com.courseApp.models.repositories;

import com.courseApp.models.Theme;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ThemeRepo extends JpaRepository<Theme,Long> {

    Theme getByName(String name);
}
