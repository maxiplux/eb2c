package app.quantun.eb2c.model.contract.contract.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private String username;
    private String userId;
    private String email;
    private String phoneNumber;
    private Boolean enabled;
    private Boolean emailVerified;
    private Boolean phoneNumberVerified;
    private String userStatus;
    private Instant userCreateDate;
    private Instant userLastModifiedDate;
    private List<String> groups;
    private Map<String, String> attributes;
} 