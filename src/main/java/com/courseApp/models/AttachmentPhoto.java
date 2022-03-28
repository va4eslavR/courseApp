package com.courseApp.models;

import com.courseApp.payloads.Photo;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "photos")
public class AttachmentPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "infopost_id")
    private InfoPost infoPost;
    @Column(name = "adress")
    String address;
    @Column(name = "delete_link")
    String deleteLink;
    @Column(name = "thumb")
    String thumb;


    public Photo getPhoto() {
        var rez= new Photo();
        rez.setAddress(this.address);
        rez.setDeleteLink(this.deleteLink);
        rez.setThumb(this.thumb);
        return rez;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AttachmentPhoto that = (AttachmentPhoto) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
