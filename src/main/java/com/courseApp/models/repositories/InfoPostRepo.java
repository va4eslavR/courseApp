package com.courseApp.models.repositories;

import com.courseApp.models.InfoPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InfoPostRepo extends JpaRepository<InfoPost, Long> {
    List<InfoPost>findByAuthorId(String authorId);
    List<InfoPost>findByTextContains(String text);




}
