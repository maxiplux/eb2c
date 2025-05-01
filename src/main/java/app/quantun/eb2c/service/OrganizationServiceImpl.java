package app.quantun.eb2c.service;

import app.quantun.eb2c.mapper.OrganizationMapper;
import app.quantun.eb2c.model.contract.request.OrganizationRequestDTO;
import app.quantun.eb2c.model.contract.response.OrganizationResponseDTO;
import app.quantun.eb2c.model.entity.bussines.Organization;
import app.quantun.eb2c.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link OrganizationService} interface.
 */
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    //private final ModelMapper modelMapper;

    private final OrganizationMapper organizationMapper;

    @Override
    @Transactional
    public OrganizationResponseDTO createOrganization(OrganizationRequestDTO requestDTO) {
        Organization organization = organizationMapper.toEntity(requestDTO);
        Organization savedOrganization = organizationRepository.save(organization);
        return organizationMapper.toOrganizationResponseDTO(savedOrganization);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<OrganizationResponseDTO> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(organizationMapper::toOrganizationResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public OrganizationResponseDTO getOrganizationById(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + id));
        return organizationMapper.toOrganizationResponseDTO(organization);
    }

    @Override
    @Transactional
    public OrganizationResponseDTO updateOrganization(Long id, OrganizationRequestDTO requestDTO) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + id));

        organization.setName(requestDTO.getName());
        organization.setDescription(requestDTO.getDescription());
        organization.setTaxId(requestDTO.getTaxId());

        Organization updatedOrganization = organizationRepository.save(organization);
        return organizationMapper.toOrganizationResponseDTO(updatedOrganization);
    }

    @Override
    @Transactional
    public void deleteOrganization(Long id) {
        if (!organizationRepository.existsById(id)) {
            throw new EntityNotFoundException("Organization not found with id: " + id);
        }
        organizationRepository.deleteById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<OrganizationResponseDTO> searchOrganizationsByName(String name) {
        return organizationRepository.findByNameContainingIgnoreCase(name).stream()
                .map(organizationMapper::toOrganizationResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public OrganizationResponseDTO getOrganizationByTaxId(String taxId) {
        Organization organization = organizationRepository.findByTaxId(taxId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with tax ID: " + taxId));
        return organizationMapper.toOrganizationResponseDTO(organization);
    }

    @Override
    public void deleteAllOrganizations() {
        organizationRepository.deleteAll();
    }

}