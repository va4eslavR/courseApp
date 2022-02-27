package com.courseApp.services.oauth2customization;


import com.courseApp.models.RoleEnum;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GrantAdminRoles {
    public Map<String,String> adminAttributes(){
        return new HashMap<>() {{
            put("email","va4eslavr@gmail.com");
        }};
    }
    public RoleEnum getRole(Map<String,Object>map){
       var att= map.values().stream().map(Object::toString).collect(Collectors.toList());
       var flag=false;
        for (String s : att) {
            flag = adminAttributes().containsValue(s);
            if (flag)
                break;
        }
        return flag? RoleEnum.SCOPE_ADMIN: RoleEnum.SCOPE_USER;
    }
}
