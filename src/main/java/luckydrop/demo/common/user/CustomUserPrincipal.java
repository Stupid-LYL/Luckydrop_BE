package luckydrop.demo.common.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import luckydrop.demo.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


@Getter
public class CustomUserPrincipal implements UserDetails {

    private final Member member;
    private final String registrationId;
    public CustomUserPrincipal(Member member) {
        this.member = member;
        this.registrationId = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }


    @Override
    public String getUsername() {
        if(registrationId == null) {
            return member.getEmail();
        }
        return null;
    }

    @Override
    public String getPassword() {
        if(registrationId == null) {
            return member.getName();
        }
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
