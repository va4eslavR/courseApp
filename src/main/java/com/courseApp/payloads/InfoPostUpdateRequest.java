package com.courseApp.payloads;

import lombok.Data;

import java.util.Set;

@Data
public class InfoPostUpdateRequest {
    private Long id;
    private String Text;
    private Set<String> tags;
    private Set<String>photos;
}
