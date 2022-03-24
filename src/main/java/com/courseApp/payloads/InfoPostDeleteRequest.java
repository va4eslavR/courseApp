package com.courseApp.payloads;

import lombok.Data;

@Data
public class InfoPostDeleteRequest {
    private Long id;
    private String author;
}
