package com.huwdunnit.snookeruprest.security;

import com.huwdunnit.snookeruprest.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of Spring Security UserDetails (i.e. the object returned from a UserDetailsService), with values
 * based on the User object stored in the backing MongoDB.
 *
 * @author Huwdunnit
 */
@Data
public class UserPrincipal implements UserDetails {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private boolean isAdmin;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.isAdmin = user.isAdmin();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Roles.USER));
        if (isAdmin) {
            authorities.add(new SimpleGrantedAuthority(Roles.ADMIN));
        }
        return authorities;
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
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
