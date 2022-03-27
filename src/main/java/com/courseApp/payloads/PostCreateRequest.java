package com.courseApp.payloads;

import lombok.Data;

import java.util.Set;

@Data
public class PostCreateRequest {
    private String text;
    private Set<String>tags;
    private Set<Photo>photos;
    private String theme;
    private String topic;
}
