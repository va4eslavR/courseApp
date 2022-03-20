package com.courseApp.services.oauth2customization;

import com.courseApp.models.Role;
import com.courseApp.models.RoleEnum;
import com.courseApp.models.AppUser;
import com.courseApp.models.repositories.AppUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
@Component
public class GoogleInitiator implements AppUserInitiator{
    @Autowired
    private final AppUserRepo appUserRepo;

    public GoogleInitiator(AppUserRepo appUserRepo) {
        this.appUserRepo = appUserRepo;
    }

    @Override
    public void saveNewAppUser(Map<String, Object> userAttributes, String name, Role role) {
        var entity= appUserRepo.findByEmail(name).orElseGet(()-> initAppUser(userAttributes,new AppUser(), role));
        entity.setLastSeen(Timestamp.valueOf(LocalDateTime.now()));
        appUserRepo.save(entity);
    }

    @Override
    public AppUser initAppUser(Map<String, Object> attributes, AppUser appUser, Role roleEnum) {
        appUser.setEmail((String) attributes.get("email"));
        appUser.setGender((String) attributes.get("gender"));
        appUser.setId((String) attributes.get("sub"));
        appUser.setName((String) attributes.get("name"));
        appUser.setPicture((String) attributes.get("picture"));
        appUser.getRole().add(roleEnum);
        return appUser;
    }
}
