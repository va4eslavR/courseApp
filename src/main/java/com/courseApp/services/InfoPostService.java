package com.courseApp.services;

import com.courseApp.models.*;
import com.courseApp.models.repositories.InfoPostRepo;
import com.courseApp.models.repositories.RateRepo;
import com.courseApp.payloads.*;
import com.courseApp.utility.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InfoPostService {

    @Autowired
    private InfoPostRepo infoPostRepo;
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
        if (value < -1 || value > 1||infoId==null) throw new IllegalArgumentException();
        var id = new RateAssociationId(Utility.getCurrentUser().getId(), infoId);
        if (!rateRepo.existsById(id)) rateRepo.insert(infoId, value, Utility.getCurrentUser().getId());
        return buttonLogicUpdate(rateRepo.findById(id).orElseThrow(), value);
    }

    private Integer buttonLogicUpdate(Rate rate, Integer div) {
        var temp = rate.getScore();
        var score = temp + div < -1 || temp + div > 1 ? temp - div : temp + div;
        rateRepo.updateScore(score, rate.getId().getPostId(), rate.getReader().getId());
        return score;
    }

    public List<PublicPostsResponse> getPublicPosts() {
        return infoPostRepo.findAll()
                .stream()
                .map(this::infoPostToPublicDao)
                .collect(Collectors.toList());
    }

    public List<PrivatePostsResponse> getPrivatePosts() {
        return infoPostRepo.findAll().stream().map(x -> {
            var item = new PrivatePostsResponse();
            item.setPublicPostsResponse(infoPostToPublicDao(x));
            var ownerEmail = Utility.getCurrentUser().getEmail();
            item.setOwner(x.getAuthorId().getEmail().equals(ownerEmail));
            var rateVal = x.getRates()
                    .stream()
                    .filter(rate -> rate.getReader().getEmail().equals(ownerEmail))
                    .findFirst()
                    .orElse(null);
            item.setRate(rateVal == null ? 0 : rateVal.getScore());
            return item;
        }).collect(Collectors.toList());
    }

    public PublicPostsResponse infoPostToPublicDao(InfoPost post) {
        var item = new PublicPostsResponse();
        item.setId(post.getId());
        item.setCreationDate(post.getCreationDate());
        item.setAuthor(post.getAuthorId().getId());
        item.setTopic(post.getTopic());
        item.setTheme(post.getTheme().getName());
        item.setText(post.getText());
        item.setTags(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        item.setPhotos(post.getPhotos().stream().map(AttachmentPhoto::getPhoto).collect(Collectors.toSet()));
        item.setRating(post.getRates().stream().map(Rate::getScore).count());
        return item;
    }

    public InfoPostEditToolsResponse getTools() {
        var rez = new InfoPostEditToolsResponse();
        rez.setTags(knownTags);
        rez.setThemes(themes);
        return rez;
    }

    private void setTags(InfoPost post, Set<String> tags) {
        expandKnownTags(tags);
        tagService.getTagsByNames(tags).forEach(post::addTag);
    }

    public void insertPost(PostCreateRequest request) {
        logger.info("tags from request first line:"+String.join(",", request.getTags()));
        var post = new InfoPost();
        var now = Timestamp.valueOf(LocalDateTime.now());
        post.setAuthorId(appUserDetailsService.getById(Utility.getCurrentUser().getId()));
        post.setLastModified(now);
        Utility.photosToAttachments(request.getPhotos()).forEach(post::addPhoto);
        expandKnownTags(request.getTags());
        tagService.getTagsByNames(request.getTags()).forEach(post::addTag);
        post.setTheme(themeService.getByName(request.getTheme()));
        post.setTopic(request.getTopic());
        post.setText(request.getText());
        post.setCreationDate(now);
        infoPostRepo.save(post);
    }

    public void updatePost(PostUpdateRequest request) {
        var post = infoPostRepo.findById(request.getId()).orElseThrow();
        if (StringUtils.hasText(request.getText()))
            post.setText(request.getText());
        post.getTags().clear();
        if (request.getTags().size() > 0)
            setTags(post, request.getTags());
        post.getPhotos().clear();
        if (request.getPhotos().size() > 0)
            Utility.photosToAttachments(request.getPhotos()).forEach(post::addPhoto);
        infoPostRepo.save(post);
    }

    public void deletePost(InfoPostDeleteRequest deleteRequest) {
        if (deleteRequest.getAuthor().equals(
                Utility.getCurrentUser().getUsername()))
            infoPostRepo.deleteById(deleteRequest.getId());
    }

    private void expandKnownTags(Set<String> tags) {
        var extras=new HashSet<>(tags);
        if (extras.removeAll(knownTags)) {
            tagService.insertTags(extras);
            knownTags.addAll(extras);
        }
    }

}
