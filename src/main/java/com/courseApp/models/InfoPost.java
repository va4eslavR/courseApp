package com.courseApp.models;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

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

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "infoposts_tags",
            joinColumns = @JoinColumn(name = "postid"),
            inverseJoinColumns = @JoinColumn(name = "tagid"))
    private Set<Tag> tags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post")
    @ToString.Exclude
    private Set<Rate> rates = new HashSet<>();

    @ManyToOne()
    @ToString.Exclude
    Theme theme;

    @OneToMany(mappedBy = "infoPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<AttachmentPhoto> attachmentPhotos = new LinkedHashSet<>();


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

    public void removeTag(String tagName) {
        var tag = tags.stream().filter(x -> x.getName().equals(tagName))
                .findFirst().orElse(null);
        if (tag != null) {
            tags.remove(tag);
            tag.getInfoPosts().remove(this);
        }
    }
    public void addPhoto(AttachmentPhoto photo) {
        this.attachmentPhotos.add(photo);
        photo.setInfoPost(this);
    }
    public void removePhoto(Long id) {
        this.attachmentPhotos.stream().filter(x -> x.getId().equals(id))
                .findFirst().ifPresent(photo -> attachmentPhotos.remove(photo));
    }
}
