package app.quantun.eb2c.model.contract.contract.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupResponse {
    private String groupName;
    private String description;
    private Instant creationDate;
    private Instant lastModifiedDate;
    private Integer precedence;
    private List<String> users;
} 