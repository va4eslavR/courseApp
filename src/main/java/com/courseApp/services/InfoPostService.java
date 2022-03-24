package com.courseApp.services;

import com.courseApp.models.AppUserDetails;
import com.courseApp.models.InfoPost;
import com.courseApp.models.Rate;
import com.courseApp.models.RateAssociationId;
import com.courseApp.models.repositories.InfoPostRepo;
import com.courseApp.models.repositories.RateRepo;
import com.courseApp.payloads.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class InfoPostService {

    @Autowired
    private InfoPostRepo infoPostRepo;
    @Autowired
    private AttachmentPhotoService attachmentPhotoService;
    @Autowired
    private RateRepo rateRepo;
    @Autowired
    private AppUserDetailsService appUserDetailsService;
    private final ThemeService themeService;
    private final TagService tagService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Set<String> knownTags;
    private final Set<String> themes;

    @Autowired
    public InfoPostService(TagService tagService, ThemeService themeService) {
        this.tagService = tagService;
        this.themeService = themeService;
        knownTags = tagService.getAllTagsNames();
        themes = themeService.getAllThemeNames();
    }

    public Integer likeDislike(Long infoId, Integer value) {
        if (value < -1 || value > 1) throw new IllegalArgumentException();
        var user = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var id = new RateAssociationId(user.getId(), infoId);
        if (!rateRepo.existsById(id)) rateRepo.insert(infoId, value, user.getId());
        return buttonLogicUpdate(rateRepo.findById(id).orElseThrow(), value);
    }

    private Integer buttonLogicUpdate(Rate rate, Integer div) {
        var temp = rate.getScore();
        var score = temp + div < -1 || temp + div > 1 ? temp - div : temp + div;
        rateRepo.updateScore(score, rate.getId().getPostId(), rate.getReader().getId());
        return score;
    }

    public List<GetPublicPostsResponse> getPublicPosts() {
        return new ArrayList<>();
    }

    public List<GetAuthenticatedPostsResponse> getAuthPosts() {
        return new ArrayList<>();
    }

    public InfoPostEditToolsResponse getTools() {
        var rez = new InfoPostEditToolsResponse();
        rez.setTags(knownTags);
        rez.setThemes(themes);
        return rez;
    }

    public void insertPost(InfoPostCreateRequest request) {
        var post = new InfoPost();
        var now = Timestamp.valueOf(LocalDateTime.now());
        expandKnownTags(request.getTags());
        post.setAuthorId(appUserDetailsService.getById(
                ((AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()));
        post.setLastModified(now);
        attachmentPhotoService.photosToAttachments(request.getPhotos()).forEach(post::addPhoto);
        tagService.getTagsByNames(request.getTags()).forEach(post::addTag);
        post.setTheme(themeService.getByName(request.getTheme()));
        post.setTopic(request.getTopic());
        post.setText(request.getText());
        post.setCreationDate(now);
        infoPostRepo.save(post);
    }

    public void deletePost(InfoPostDeleteRequest deleteRequest) {
        if (deleteRequest.getAuthor().equals(
                ((AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()))
            infoPostRepo.deleteById(deleteRequest.getId());
    }

    private void expandKnownTags(Set<String> extras) {
        if (extras.removeAll(knownTags)) {
            tagService.insertTags(extras);
            knownTags.addAll(extras);
        }
    }

}
