package com.courseApp.models;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "infoposts")
public class InfoPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "text")
    private String text;
    @Column(name = "creationdate")
    private Timestamp creationDate;
    @Column(name = "lastmodified")
    private Timestamp lastModified;
    @Column(name = "topic")
    private String topic;
    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private AppUser authorId;
    @ManyToMany
    @JoinTable(name = "infoposts_tags",
            joinColumns = @JoinColumn(name = "postid"),
            inverseJoinColumns = @JoinColumn(name = "tagid"))
    @ToString.Exclude
    private Set<Tag> tags;
    @OneToMany(mappedBy = "post")
    @ToString.Exclude
    private Set<Rate> rates = new HashSet<>();
    @OneToMany(mappedBy = "infoPostId")
    @ToString.Exclude
    private Set<AttachmentPhoto> photos = new HashSet<>();
    @ManyToOne()
    @ToString.Exclude
    Theme theme;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        InfoPost infoPost = (InfoPost) o;
        return id != null && Objects.equals(id, infoPost.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getInfoPosts().add(this);
    }

    public void removeTag(Long tagId) {
        var tag = tags.stream().filter(x -> x.getId().equals(tagId))
                .findFirst().orElse(null);
        if (tag != null) {
            tags.remove(tag);
            tag.getInfoPosts().remove(this);
        }
    }
    public void addPhoto(AttachmentPhoto photo){
        this.photos.add(photo);
        photo.setInfoPostId(this.id);
    }
    public void removePhoto(Long id){
        this.photos.stream().filter(x -> x.getId().equals(id))
                .findFirst().ifPresent(photo -> photos.remove(photo));
    }

    public void getRate(String userId) {

    }

}
