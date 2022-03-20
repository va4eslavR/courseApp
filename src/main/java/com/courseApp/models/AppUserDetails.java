package com.courseApp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class AppUserDetails implements UserDetails {
    private String id;
    private String username;
    @JsonIgnore
    private String password;
    private boolean enabled;
    private String email;
    private Collection<?extends GrantedAuthority>authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    public static AppUserDetails getAppUserDetailsVia(AppUser appUser){
        List<GrantedAuthority> authorities=appUser
                .getRole().stream().map(x->new SimpleGrantedAuthority(x.getRole()
                        .getValue()))
                .collect(Collectors.toList());
        return  new AppUserDetails(
                appUser.getId(),
                appUser.getName(),
                appUser.getPassword(),
                true,
                appUser.getEmail(),
                authorities
        );
    }
}
