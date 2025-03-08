package app.quantun.b2b.model.contract;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserInfoDTO {
    private String username;
    private String email;
    private Long userId;
    private List<String> roles;
}