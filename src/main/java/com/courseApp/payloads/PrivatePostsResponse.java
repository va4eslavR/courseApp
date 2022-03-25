package com.courseApp.payloads;

import lombok.Data;

@Data
public class PrivatePostsResponse {
    PublicPostsResponse publicPostsResponse;
    Integer rate;
    Boolean owner;
}
