package com.courseApp.services.oauth2customization;

import com.courseApp.models.RoleEnum;
import com.courseApp.models.AppUser;


import java.util.Map;

public interface AppUserInitiator {
     void saveNewAppUser(Map<String, Object> userAttributes, String name, RoleEnum roleEnum);
    AppUser initAppUser(Map<String,Object>attributes, AppUser appUser, RoleEnum roleEnum);
}
