package app.quantun.eb2c.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User, UserDetails {

    private final OAuth2User oauth2User;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(OAuth2User oauth2User) {
        this.oauth2User = oauth2User;
        this.attributes = oauth2User.getAttributes();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getName() {
        return oauth2User.getAttribute("name");
    }

    @Override
    public String getUsername() {
        return oauth2User.getAttribute("email"); // Use email as the username
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

    public String getEmail() {
        return oauth2User.getAttribute("email");
    }

    public String getFirstName() {
        return oauth2User.getAttribute("given_name");
    }

    public String getLastName() {
        return oauth2User.getAttribute("family_name");
    }
}