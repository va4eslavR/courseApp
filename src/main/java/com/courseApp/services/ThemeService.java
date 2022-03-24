package com.courseApp.services;

import com.courseApp.models.Tag;
import com.courseApp.models.Theme;
import com.courseApp.models.repositories.ThemeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ThemeService {
    @Autowired
    private ThemeRepo themeRepo;
    public Set<String> getAllThemeNames(){
        return themeRepo.findAll().stream().map(Theme::getName).collect(Collectors.toSet());
    }
    public Theme getByName(String name){
       return themeRepo.getByName(name);
    }
}
