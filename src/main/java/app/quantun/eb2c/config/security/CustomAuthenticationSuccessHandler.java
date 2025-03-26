package app.quantun.eb2c.config.security;


import app.quantun.eb2c.model.contract.contract.dto.CustomUserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Extract authorities from cognito:groups
        Map<String, Object> attributes = ((OidcUser) authentication.getPrincipal()).getAttributes();

        List<String> groups = new ArrayList<>();

        if (attributes.get("cognito:groups") != null && attributes.get("cognito:groups") instanceof List) {
            log.info("Cognito groups attribute found: {}", attributes.get("cognito:groups"));
            groups = ((List<?>) attributes.get("cognito:groups")).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());

        } else {
            log.warn("Cognito groups attribute not found in user attributes");
        }


        List<GrantedAuthority> authorities = groups.stream()
                .map(group -> new SimpleGrantedAuthority("ROLE_" + group))
                .collect(Collectors.toList());

        // Set the authorities to the authentication object
        Authentication newAuth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), authorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        log.info("User successfully authenticated with authorities: {}", authorities);

        String username = (String) attributes.get("email");
        String email = (String) attributes.get("email");
        String accountId = (String) attributes.get("sub");
        String givenName = (String) attributes.get("given_name");


        CustomUserDTO customUserDTO = CustomUserDTO.builder()
                .username(username)
                .email(email)
                .accountId(accountId)
                .givenName(givenName)
                .groups(groups)
                .authorities(authorities)
                .build();

        HttpSession session = request.getSession();
        session.setAttribute("USER_DTO", customUserDTO);

        response.sendRedirect("/dashboard"); // Redirect to dashboard after login
    }

    private String getSingleAttribute(Map<String, List<Object>> attributes, String key) {
        List<Object> values = attributes.get(key);
        return values != null && !values.isEmpty() ? values.get(0).toString() : null;
    }
}

