package com.courseApp.services;

import com.courseApp.models.Tag;
import com.courseApp.models.repositories.TagRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagService {
    @Autowired
    private TagRepo tagRepo;

    public Set<String> getAllTagsNames(){
       return tagRepo.findAll().stream().map(Tag::getName).collect(Collectors.toSet());
    }
    public void insertTags(Set<String> names){
        tagRepo.saveAll(names.stream().map(x->{
            var tag=new Tag();
            tag.setName(x);
            return tag;}).collect(Collectors.toSet()));
    }
    public Set<Tag> getTagsByNames(Set<String>names ){
       return tagRepo.findAllByNameIn(names);
    }
}
