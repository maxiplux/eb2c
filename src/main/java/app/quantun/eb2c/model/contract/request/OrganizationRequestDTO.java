package app.quantun.b2b.model.contract.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Organization requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationRequestDTO {
    
    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 100, message = "Organization name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @Size(max = 30, message = "Tax ID cannot exceed 30 characters")
    private String taxId;
}