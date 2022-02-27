package com.courseApp.services.oauth2customization;

import com.courseApp.models.RoleEnum;
import com.courseApp.models.AppUser;
import com.courseApp.models.repositories.AppUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
@Component
public class GithubInitiator implements AppUserInitiator{
    @Autowired
    private final AppUserRepo appUserRepo;

    public GithubInitiator(AppUserRepo appUserRepo) {
        this.appUserRepo = appUserRepo;
    }

    @Override
    public void saveNewAppUser(Map<String, Object> userAttributes, String id, RoleEnum roleEnum){
        var entity= appUserRepo.findByEmail((String) userAttributes.get("email"))
                .or(()->appUserRepo.findById(id))
                .orElseGet(()-> initAppUser(userAttributes,new AppUser(), roleEnum));
        entity.setLastSeen(LocalDateTime.now());
        appUserRepo.save(entity);
    }

    @Override
    public  AppUser initAppUser(Map<String, Object> userAttributes, AppUser appUser, RoleEnum roleEnum) {
            appUser.setEmail((String) (userAttributes.get("email") != null ?
                    userAttributes.get("email") : userAttributes.get("id").toString()));
            appUser.setGender((String) userAttributes.get("gender"));
            appUser.setId(userAttributes.get("id").toString());
            appUser.setName((String) userAttributes.get("login"));
            appUser.setPicture((String) userAttributes.get("avatar_url"));
            appUser.setRoleEnum(roleEnum);
            return appUser;
        }
}
