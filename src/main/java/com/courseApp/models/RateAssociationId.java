package com.courseApp.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class RateAssociationId implements Serializable {
    @Column(name="readeridentifier")
    private String readerId;
    @Column(name="postidentifier")
    private Long postId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RateAssociationId)) return false;
        RateAssociationId that = (RateAssociationId) o;
        return Objects.equals(readerId, that.readerId) && Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(readerId, postId);
    }
}
