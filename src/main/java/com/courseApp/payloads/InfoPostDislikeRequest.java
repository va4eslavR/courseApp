package com.courseApp.payloads;

import lombok.Data;

@Data
public class InfoPostDislikeRequest {
    Integer value;
    Long post;
}
