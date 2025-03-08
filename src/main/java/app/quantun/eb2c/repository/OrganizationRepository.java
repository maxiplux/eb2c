package app.quantun.b2b.repository;

import app.quantun.b2b.model.entity.bussines.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Organization} entity.
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    
    /**
     * Find an organization by its name.
     *
     * @param name the name of the organization
     * @return an Optional containing the found organization or empty if not found
     */
    Optional<Organization> findByName(String name);
    
    /**
     * Find organizations containing the given name (case insensitive).
     *
     * @param name the name to search for
     * @return a list of organizations matching the search criteria
     */
    List<Organization> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find an organization by its tax ID.
     *
     * @param taxId the tax ID to search for
     * @return an Optional containing the found organization or empty if not found
     */
    Optional<Organization> findByTaxId(String taxId);
}