package app.quantun.eb2c.model.contract.contract.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {

    @Min(value = 0, message = "Page must be greater than or equal to 0")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be greater than or equal to 1")
    @Max(value = 100, message = "Page size must be less than or equal to 100")
    private Integer size = 20;

    private String sortBy;

    private String sortDirection = "asc";

    private String filter;
} 