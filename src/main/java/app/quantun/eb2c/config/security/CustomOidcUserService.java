package app.quantun.eb2c.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService delegate = new OidcUserService();

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate to the default OidcUserService to load the user
        OidcUser oidcUser = delegate.loadUser(userRequest);

        // Extract claims from the ID token or user info
        Map<String, Object> claims = oidcUser.getClaims();

        // Extract roles and groups from the claims
        Set<GrantedAuthority> authorities = new HashSet<>();
//        List<String> roles = (List<String>) claims.get("roles");
        List<String> groups = (List<String>) claims.get("cognito:groups");

        // Add roles to authorities
//        if (roles != null) {
//            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
//        }

        // Add groups to authorities
        if (groups != null) {
            groups.forEach(group -> authorities.add(new SimpleGrantedAuthority("ROLE_" + group)));
        }

        // Return a new DefaultOidcUser with the extracted authorities
        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}
