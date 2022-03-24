package com.courseApp.payloads;

import com.courseApp.models.Tag;
import com.courseApp.models.Theme;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.Set;

@Getter
public class InfoPostEditToolsResponse {
    @Value("${app.imgbb.api.key}")
    private String photoServiceKey;
    @Value("${app.imgbb.api.host}")
    private String photoServiceHost;
    @Setter
    private Set<String>tags;
    @Setter
    private Set<String>themes;
}
