package com.courseApp.payloads;

import lombok.Data;

@Data
public class PrivatePostsResponse {
   private PublicPostsResponse publicPostsResponse;
   private Integer rate;
   private Boolean owner;
}
