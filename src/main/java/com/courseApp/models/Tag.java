package com.courseApp.models;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="tags")
@Entity
public class Tag {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "name",nullable = false,unique = true)
    private String name;
    @ManyToMany(mappedBy = "tags")
    @ToString.Exclude
    private Set<InfoPost>infoPosts;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Tag tag = (Tag) o;
        return id != null && Objects.equals(id, tag.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
