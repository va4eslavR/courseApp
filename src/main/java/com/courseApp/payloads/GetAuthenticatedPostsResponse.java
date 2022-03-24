package com.courseApp.payloads;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetAuthenticatedPostsResponse extends GetPublicPostsResponse {
    Integer rate;
    Boolean owner;
}
