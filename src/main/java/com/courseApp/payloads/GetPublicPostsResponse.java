package com.courseApp.payloads;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;

@Data
public class GetPublicPostsResponse {
    Long id;
    String text;
    Timestamp creationDate;
    Integer rating;
    Set<Photo> photos;
    String theme;
    String topic;
    Set<String> tags;
    String author;
}
