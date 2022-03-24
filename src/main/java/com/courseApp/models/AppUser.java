package com.courseApp.models;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "uzers")
public class AppUser {
 @Id
 @Column(name ="id",unique = true,nullable = false)
 private String id;
 @Column(unique=true)
 private String name;
 @Column(name = "email",nullable = false,unique = true)
 private String email;
 private String gender;
 @Column(name = "last_seen")
 private Timestamp lastSeen;
 @Column(name = "password")
 private String password;
 private String picture;
 @Enumerated(EnumType.STRING)
 @ManyToMany
 @JoinTable(name = "uzers_roles",
 joinColumns = @JoinColumn(name = "uzer_id"),
 inverseJoinColumns = @JoinColumn(name = "role_id"))
 @ToString.Exclude
 private Set<Role> role=new HashSet<>();
 @OneToMany(mappedBy = "authorId",cascade = CascadeType.ALL)
 @ToString.Exclude
 private Set<InfoPost> infoPosts;
 @OneToMany(mappedBy = "reader",cascade = CascadeType.ALL,orphanRemoval = true)
 @ToString.Exclude
 private Set<Rate> rates=new HashSet<>();

 @Override
 public boolean equals(Object o) {
  if (this == o) return true;
  if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
  AppUser appUser = (AppUser) o;
  return id != null && Objects.equals(id, appUser.id)
          && Objects.equals(email, appUser.email);
 }

 @Override
 public int hashCode() {
  return getClass().hashCode();
 }
}
