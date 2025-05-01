package app.quantun.eb2c.model.contract.contract.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class CustomUserDTO implements Serializable {
    private String username;
    private String email;
    private String accountId;
    private String givenName;
    private List<String> groups;
    private List<GrantedAuthority> authorities;
}

