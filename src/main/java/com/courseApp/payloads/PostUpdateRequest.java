package com.courseApp.payloads;

import lombok.Data;

import java.util.Set;

@Data
public class PostUpdateRequest {
    private Long id;
    private String Text;
    private Set<String> tags;
    private Set<Photo>photos;
}
