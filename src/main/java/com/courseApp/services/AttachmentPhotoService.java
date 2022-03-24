package com.courseApp.services;

import com.courseApp.models.AttachmentPhoto;
import com.courseApp.models.repositories.AttachmentPhotoRepo;
import com.courseApp.payloads.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AttachmentPhotoService {
    @Autowired
    private AttachmentPhotoRepo attachmentPhotoRepo;

    public Set<AttachmentPhoto> photosToAttachments(Set<Photo>photos){
      return   photos.stream().map(x->{
            var item=new AttachmentPhoto();
            item.setAddress(x.getAddress());
            item.setDeleteLink(x.getDeleteLink());
            item.setThumb(x.getThumb());
            item.setMedium(x.getMedium());
            return item;
        }).collect(Collectors.toSet());
    }
}
