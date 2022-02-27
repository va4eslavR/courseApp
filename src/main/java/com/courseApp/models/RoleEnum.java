package com.courseApp.models;

import lombok.Getter;

@Getter
public enum RoleEnum {
    SCOPE_ADMIN("SCOPE_ADMIN"),SCOPE_USER("SCOPE_USER");
    private final String value;

    RoleEnum(String value) {
        this.value = value;
    }
}
