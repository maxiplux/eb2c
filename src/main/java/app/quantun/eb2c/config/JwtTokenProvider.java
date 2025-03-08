package app.quantun.b2b.config;

import app.quantun.b2b.model.contract.UserInfoDTO;
import app.quantun.b2b.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    /**
     * Generates a JWT token based on the provided authentication details.
     *
     * @param authentication the authentication details
     * @return the generated JWT token
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Extract user information and create a DTO
        UserInfoDTO userInfo = extractUserInfo(authentication);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpirationMs());

        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));

        // Convert DTO to claims map
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userInfo.getUsername());
        claims.put("roles", userInfo.getRoles());

        // Add optional fields only if they exist
        if (userInfo.getUserId() != null) {
            claims.put("userId", userInfo.getUserId());
        }
        if (userInfo.getEmail() != null) {
            claims.put("email", userInfo.getEmail());
        }

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(claims)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * Extracts user information from the provided authentication details.
     *
     * @param authentication the authentication details
     * @return the extracted user information as a UserInfoDTO
     */
    private UserInfoDTO extractUserInfo(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Extract roles
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Build basic UserInfoDTO
        UserInfoDTO.UserInfoDTOBuilder builder = UserInfoDTO.builder()
                .username(userDetails.getUsername())
                .email(user.getEmail())
                .userId(user.getId())
                .roles(roles);

        return builder.build();
    }

    /**
     * Retrieves the username from the provided JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
//        Claims claims = Jwts.parser().verifyWith(key).build().parseEncryptedClaims(token).getPayload();
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        return claims.getSubject();
    }

    /**
     * Validates the provided JWT token.
     *
     * @param token the JWT token
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
            //Jwts.parser().verifyWith(key).build().parseEncryptedClaims(token).getPayload();
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
