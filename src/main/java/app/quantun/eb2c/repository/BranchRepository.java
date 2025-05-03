package app.quantun.eb2c.repository;

import app.quantun.eb2c.model.entity.bussines.Branch;
import app.quantun.eb2c.model.entity.bussines.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Branch entities.
 * This interface provides methods for CRUD operations on branches.
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    /**
     * Find all branches for a specific organization.
     *
     * @param organization the organization
     * @return a list of branches
     */
    List<Branch> findByOrganization(Organization organization);

    /**
     * Find a branch by its name and organization.
     *
     * @param name         the branch name
     * @param organization the organization
     * @return a list of branches
     */
    List<Branch> findByNameAndOrganization(String name, Organization organization);
}