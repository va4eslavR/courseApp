package com.courseApp.services.oauth2customization;


import com.courseApp.models.RoleEnum;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class GrantAdminRoles {
    @Getter
    private final Map<String,String> adminAttributes=
         new HashMap<>() {{
            put("va4eslavr@gmail.com","email");
        }};

    public<T> RoleEnum getRole(Map<String,T>map){
        for (var entry:map.entrySet()) {
            if (getAdminAttributes().containsKey(entry.getValue().toString())
                    && getAdminAttributes().get(entry.getValue().toString()).equals(entry.getKey()))
                return RoleEnum.ROLE_ADMIN;
        }
        return RoleEnum.ROLE_USER;
    }
}
