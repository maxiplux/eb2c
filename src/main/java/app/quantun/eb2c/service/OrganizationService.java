package app.quantun.b2b.service;

import app.quantun.b2b.model.contract.request.OrganizationRequestDTO;
import app.quantun.b2b.model.contract.response.OrganizationResponseDTO;

import java.util.List;

/**
 * Service for handling organization-related operations.
 */
public interface OrganizationService {
    
    /**
     * Create a new organization.
     *
     * @param requestDTO the organization request DTO
     * @return the created organization response DTO
     */
    OrganizationResponseDTO createOrganization(OrganizationRequestDTO requestDTO);
    
    /**
     * Get all organizations.
     *
     * @return list of all organization response DTOs
     */
    List<OrganizationResponseDTO> getAllOrganizations();
    
    /**
     * Get an organization by its ID.
     *
     * @param id the organization ID
     * @return the organization response DTO
     * @throws jakarta.persistence.EntityNotFoundException if organization not found
     */
    OrganizationResponseDTO getOrganizationById(Long id);
    
    /**
     * Update an existing organization.
     *
     * @param id the organization ID
     * @param requestDTO the organization request DTO with updated data
     * @return the updated organization response DTO
     * @throws jakarta.persistence.EntityNotFoundException if organization not found
     */
    OrganizationResponseDTO updateOrganization(Long id, OrganizationRequestDTO requestDTO);
    
    /**
     * Delete an organization by its ID.
     *
     * @param id the organization ID
     * @throws jakarta.persistence.EntityNotFoundException if organization not found
     */
    void deleteOrganization(Long id);
    
    /**
     * Search organizations by name (case insensitive).
     *
     * @param name the name to search for
     * @return list of matching organization response DTOs
     */
    List<OrganizationResponseDTO> searchOrganizationsByName(String name);
    
    /**
     * Find an organization by tax ID.
     *
     * @param taxId the tax ID to search for
     * @return the organization response DTO
     * @throws jakarta.persistence.EntityNotFoundException if organization not found
     */
    OrganizationResponseDTO getOrganizationByTaxId(String taxId);
}