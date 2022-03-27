package com.courseApp.controllers;

import com.courseApp.payloads.*;
import com.courseApp.services.InfoPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @GetMapping("/tools")
    public ResponseEntity<?> getEditTools() {
        var tools = infoPostService.getTools();
        tools.setPhotoServiceHost(photoServiceHost);
        tools.setPhotoServiceKey(photoServiceKey);
        return ResponseEntity.ok(tools);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPost(@Valid @RequestBody PostCreateRequest postCreateRequest) {
        logger.info("from controller"+String.join(",",postCreateRequest.getTags()));
        infoPostService.insertPost(postCreateRequest);
        return ResponseEntity.ok(new MessageResponse("success"));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(@Valid @RequestBody InfoPostDeleteRequest deleteRequest) {
        infoPostService.deletePost(deleteRequest);
        return ResponseEntity.ok().body(new MessageResponse("ok"));
    }

    @PostMapping("/like")
    public ResponseEntity<?> like(@Valid @RequestBody InfoPostDislikeRequest infoPostDislikeRequest) {
        return ResponseEntity.ok().body(new PostLikeResponse(infoPostService
                .likeDislike(infoPostDislikeRequest.getPost(), infoPostDislikeRequest.getValue())));
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editPost(@Valid @RequestBody PostUpdateRequest postUpdateRequest) {
        infoPostService.updatePost(postUpdateRequest);
        return ResponseEntity.ok().body(new MessageResponse("ok"));
    }

    @GetMapping("/public")
    public ResponseEntity<?> getInfoPosts() {
        return ResponseEntity.ok().body(infoPostService.getPublicPosts());
    }

    @GetMapping("/private")
    public ResponseEntity<?> getAuthorizedPosts() {
        return ResponseEntity.ok().body(infoPostService.getPrivatePosts());
    }

}
