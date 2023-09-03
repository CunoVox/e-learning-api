package com.elearning.security;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Setter
@Getter
@SuperBuilder
public class SecurityUserDetail implements UserDetails {
    private final String id;
    private final String fullName;

    private final String email;

    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    private final boolean isDeleted;

    public SecurityUserDetail(String id, String fullName, String email,
                              String password, Collection<? extends GrantedAuthority> authorities, boolean isDeleted) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.isDeleted = isDeleted;
    }

    public String getId() {
        return id;
    }

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
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isDeleted;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !isDeleted;
    }
}
