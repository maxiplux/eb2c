package app.quantun.b2b.model.contract.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object for Organization responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private String taxId;
    @Builder.Default
    private Set<BranchResponseDTO> branches = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    /**
     * Nested DTO for Branch responses within Organization.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchResponseDTO {
        private Long id;
        private String name;
        private String address;
        private String phone;
        private String email;
    }
}