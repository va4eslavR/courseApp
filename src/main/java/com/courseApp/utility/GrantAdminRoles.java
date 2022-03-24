package com.courseApp.utility;


import com.courseApp.models.RoleEnum;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class GrantAdminRoles {
    @Value("${app.admin.attribute.value}")
    private String value;
    @Value("${app.admin.attribute.type}")
    private String type;
    @Getter
    private final Map<String,String> adminAttributes=
         new HashMap<>() {{
            put(value,type);
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
