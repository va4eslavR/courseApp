package com.courseApp.controllers;

import com.courseApp.payloads.*;
import com.courseApp.services.InfoPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/info")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InfoPostController {
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InfoPostService infoPostService;
    @Value("${app.imgbb.api.key}")
    private String photoServiceKey;
    @Value("${app.imgbb.api.host}")
    private String photoServiceHost;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/tools")
    public ResponseEntity<?> getEditTools() {
        var tools = infoPostService.getTools();
        tools.setPhotoServiceHost(photoServiceHost);
        tools.setPhotoServiceKey(photoServiceKey);
        return ResponseEntity.ok(tools);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> addPost(@Valid @RequestBody PostCreateRequest postCreateRequest) {
        logger.info("from controller"+String.join(",",postCreateRequest.getTags()));
        infoPostService.insertPost(postCreateRequest);
        return ResponseEntity.ok(new MessageResponse("success"));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(@Valid @RequestBody InfoPostDeleteRequest deleteRequest) {
        infoPostService.deletePost(deleteRequest);
        return ResponseEntity.ok().body(new MessageResponse("ok"));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/like")
    public ResponseEntity<?> like(@Valid @RequestBody InfoPostDislikeRequest infoPostDislikeRequest) {
        return ResponseEntity.ok().body(new PostLikeResponse(infoPostService
                .likeDislike(infoPostDislikeRequest.getPost(), infoPostDislikeRequest.getValue())));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/edit")
    public ResponseEntity<?> editPost(@Valid @RequestBody PostUpdateRequest postUpdateRequest) {
        infoPostService.updatePost(postUpdateRequest);
        return ResponseEntity.ok().body(new MessageResponse("ok"));
    }

    @GetMapping("/public")
    public ResponseEntity<?> getInfoPosts() {
        return ResponseEntity.ok().body(infoPostService.getPublicPosts());
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/private")
    public ResponseEntity<List<PrivatePostsResponse>> getAuthorizedPosts() {
        var posts = infoPostService.getPrivatePosts();
        return ResponseEntity.ok().body(posts);
    }

}
