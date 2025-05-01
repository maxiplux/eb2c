package app.quantun.eb2c.model.contract.contract.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequest {

    @NotBlank(message = "Group name is required")
    @Size(min = 1, max = 128, message = "Group name must be between 1 and 128 characters")
    private String groupName;

    private String description;

    private Integer precedence;
} 