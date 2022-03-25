package com.courseApp.payloads;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;

@Data
public class PublicPostsResponse {
    Long id;
    String text;
    Timestamp creationDate;
    Long rating;
    Set<Photo> photos;
    String theme;
    String topic;
    Set<String> tags;
    String author;
}
