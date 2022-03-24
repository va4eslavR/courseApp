package com.courseApp.models.repositories;

import com.courseApp.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface TagRepo extends JpaRepository<Tag, Long> {
    Set<Tag> findAllByNameIn(Set<String> names);
}
