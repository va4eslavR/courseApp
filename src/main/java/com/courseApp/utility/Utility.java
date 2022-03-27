package com.courseApp.utility;

import com.courseApp.models.AppUserDetails;
import com.courseApp.models.AttachmentPhoto;
import com.courseApp.payloads.Photo;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

public class Utility {
    public static AppUserDetails getCurrentUser() {
        return (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Set<AttachmentPhoto> photosToAttachments(Set<Photo> photos) {
        return photos.stream().map(x -> {
            var item = new AttachmentPhoto();
            item.setAddress(x.getAddress());
            item.setDeleteLink(x.getDeleteLink());
            item.setThumb(x.getThumb());
            return item;
        }).collect(Collectors.toSet());
    }
}
