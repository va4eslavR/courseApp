package com.courseApp.models;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ROLE_ADMIN("ROLE_ADMIN"), ROLE_USER("ROLE_USER");
    private final String value;

    RoleEnum(String value) {
        this.value = value;
    }
}
