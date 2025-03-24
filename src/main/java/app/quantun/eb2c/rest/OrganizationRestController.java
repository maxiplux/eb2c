package app.quantun.eb2c.rest;

import app.quantun.eb2c.model.contract.request.OrganizationRequestDTO;
import app.quantun.eb2c.model.contract.response.OrganizationResponseDTO;
import app.quantun.eb2c.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing organizations.
 */
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
@Tag(name = "Organization API", description = "Operations for managing organizations")
public class OrganizationRestController {

    private final OrganizationService organizationService;

    @Operation(summary = "Create a new organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Organization created successfully",
                    content = @Content(schema = @Schema(implementation = OrganizationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<OrganizationResponseDTO> createOrganization(
            @Valid @RequestBody OrganizationRequestDTO requestDTO) {
        OrganizationResponseDTO responseDTO = organizationService.createOrganization(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Get all organizations")
    @ApiResponse(responseCode = "200", description = "List of organizations retrieved successfully",
            content = @Content(schema = @Schema(implementation = OrganizationResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<OrganizationResponseDTO>> getAllOrganizations() {
        List<OrganizationResponseDTO> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }

    @Operation(summary = "Get organization by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organization found",
                    content = @Content(schema = @Schema(implementation = OrganizationResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponseDTO> getOrganizationById(
            @Parameter(description = "Organization ID") @PathVariable Long id) {
        OrganizationResponseDTO responseDTO = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Update an organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organization updated successfully",
                    content = @Content(schema = @Schema(implementation = OrganizationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponseDTO> updateOrganization(
            @Parameter(description = "Organization ID") @PathVariable Long id,
            @Valid @RequestBody OrganizationRequestDTO requestDTO) {
        OrganizationResponseDTO responseDTO = organizationService.updateOrganization(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Delete an organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Organization deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(
            @Parameter(description = "Organization ID") @PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search organizations by name")
    @ApiResponse(responseCode = "200", description = "Search results",
            content = @Content(schema = @Schema(implementation = OrganizationResponseDTO.class)))
    @GetMapping("/search")
    public ResponseEntity<List<OrganizationResponseDTO>> searchOrganizationsByName(
            @Parameter(description = "Name to search for") @RequestParam String name) {
        List<OrganizationResponseDTO> organizations = organizationService.searchOrganizationsByName(name);
        return ResponseEntity.ok(organizations);
    }

    @Operation(summary = "Find organization by tax ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organization found",
                    content = @Content(schema = @Schema(implementation = OrganizationResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @GetMapping("/tax/{taxId}")
    public ResponseEntity<OrganizationResponseDTO> getOrganizationByTaxId(
            @Parameter(description = "Tax ID to search for") @PathVariable String taxId) {
        OrganizationResponseDTO responseDTO = organizationService.getOrganizationByTaxId(taxId);
        return ResponseEntity.ok(responseDTO);
    }
}