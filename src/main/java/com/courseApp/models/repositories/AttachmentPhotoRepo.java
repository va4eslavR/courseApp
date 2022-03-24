package com.courseApp.models.repositories;

import com.courseApp.models.AttachmentPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentPhotoRepo extends JpaRepository<AttachmentPhoto,Long> {
}
