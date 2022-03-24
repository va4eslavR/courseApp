package com.courseApp.payloads;

import lombok.Data;

@Data
public class InfoPostDislikeRequest {
    String user;
    Long post;
}
