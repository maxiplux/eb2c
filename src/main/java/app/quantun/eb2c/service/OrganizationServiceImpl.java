package app.quantun.b2b.service;

import app.quantun.b2b.model.contract.request.OrganizationRequestDTO;
import app.quantun.b2b.model.contract.response.OrganizationResponseDTO;
import app.quantun.b2b.model.entity.bussines.Organization;
import app.quantun.b2b.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
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
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    public OrganizationResponseDTO createOrganization(OrganizationRequestDTO requestDTO) {
        Organization organization = modelMapper.map(requestDTO, Organization.class);
        Organization savedOrganization = organizationRepository.save(organization);
        return modelMapper.map(savedOrganization, OrganizationResponseDTO.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrganizationResponseDTO> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(organization -> modelMapper.map(organization, OrganizationResponseDTO.class))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrganizationResponseDTO getOrganizationById(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + id));
        return modelMapper.map(organization, OrganizationResponseDTO.class);
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
        return modelMapper.map(updatedOrganization, OrganizationResponseDTO.class);
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
    @Transactional(readOnly = true)
    public List<OrganizationResponseDTO> searchOrganizationsByName(String name) {
        return organizationRepository.findByNameContainingIgnoreCase(name).stream()
                .map(organization -> modelMapper.map(organization, OrganizationResponseDTO.class))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrganizationResponseDTO getOrganizationByTaxId(String taxId) {
        Organization organization = organizationRepository.findByTaxId(taxId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with tax ID: " + taxId));
        return modelMapper.map(organization, OrganizationResponseDTO.class);
    }
}