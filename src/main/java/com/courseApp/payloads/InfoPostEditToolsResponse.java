package com.courseApp.payloads;

import lombok.Data;

import java.util.Set;

@Data
public class InfoPostEditToolsResponse {

    private String photoServiceKey;

    private String photoServiceHost;

    private Set<String> tags;

    private Set<String> themes;
}
