package com.courseApp.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rates")
public class Rate implements Serializable {
    @EmbeddedId
    private RateAssociationId id;

    @ManyToOne
    @MapsId("readerId")
    @JoinColumn(name = "readeridentifier")
    private AppUser reader;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "postidentifier")
    private InfoPost post;

    @Column(name = "score")
    private Integer score;

    public Rate(AppUser reader, InfoPost post, Integer score) {
        this.id = new RateAssociationId(reader.getId(), post.getId());
        this.reader = reader;
        this.post = post;
        this.score = score;
    }
}
