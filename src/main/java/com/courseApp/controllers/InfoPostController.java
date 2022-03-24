package com.courseApp.controllers;

import com.courseApp.payloads.GetAuthenticatedPostsResponse;
import com.courseApp.payloads.InfoPostCreateRequest;
import com.courseApp.payloads.InfoPostDeleteRequest;
import com.courseApp.payloads.MessageResponse;
import com.courseApp.services.InfoPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/info")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InfoPostController {
    @Autowired
    private InfoPostService infoPostService;

    @GetMapping("/tools")
    public ResponseEntity<?> getEditTools() {
        return ResponseEntity.ok(infoPostService.getTools());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPost(@Valid @RequestParam InfoPostCreateRequest infoPostCreateRequest) {
        infoPostService.insertPost(infoPostCreateRequest);
        return ResponseEntity.ok(new MessageResponse("success"));
    }
    @PostMapping("/delete")
    public ResponseEntity<?>deletePost(@Valid@RequestParam InfoPostDeleteRequest deleteRequest){
        return ResponseEntity.ok().body(new MessageResponse("ok"));
    }
    @GetMapping("/public")
    public ResponseEntity<?> getInfoPosts() {
        return ResponseEntity.ok().body(infoPostService.getPublicPosts());
    }

    @GetMapping("/authorized")
    public ResponseEntity<?>getAuthorizedPosts(){return ResponseEntity.ok().body(new GetAuthenticatedPostsResponse());}

}
