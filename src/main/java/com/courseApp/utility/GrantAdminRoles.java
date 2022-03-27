package com.courseApp.utility;


import com.courseApp.models.RoleEnum;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Component
public class GrantAdminRoles {
    @Autowired
    @Value("${app.admin.attribute.value}")
    private String value;
    @Autowired
    @Value("${app.admin.attribute.type}")
    private String type;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Getter
    private final Map<String, String> adminAttributes = new HashMap<>();


    public <T> RoleEnum getRole(Map<String, T> map) {
        getAdminAttributes().put(value, type);
        logger.info("got my admin rules?  " + Arrays.toString(getAdminAttributes().entrySet().toArray()));
        for (var entry : map.entrySet()) {
            logger.info("vlaims from registrationRequest: " + Arrays.toString(map.entrySet().toArray()));
            if (getAdminAttributes().containsKey(entry.getValue().toString())
                    && getAdminAttributes().get(entry.getValue().toString()).equals(entry.getKey()))
                return RoleEnum.ROLE_ADMIN;
        }
        return RoleEnum.ROLE_USER;
    }
}
