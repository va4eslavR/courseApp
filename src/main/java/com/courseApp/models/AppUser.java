package com.courseApp.models;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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
 private Timestamp lastSeen;
 private String password;
 private String picture;
 @Enumerated(EnumType.STRING)
 @ManyToMany
 @JoinTable(name = "uzers_roles",
 joinColumns = @JoinColumn(name = "uzer_id"),
 inverseJoinColumns = @JoinColumn(name = "role_id"))
 private Set<Role> role;


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
