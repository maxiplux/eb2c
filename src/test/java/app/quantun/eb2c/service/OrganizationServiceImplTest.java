package app.quantun.b2b.service;

import app.quantun.b2b.model.contract.request.OrganizationRequestDTO;
import app.quantun.b2b.model.contract.response.OrganizationResponseDTO;
import app.quantun.b2b.model.entity.bussines.Organization;
import app.quantun.b2b.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.aot.DisabledInAotMode;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisabledInAotMode
class OrganizationServiceImplTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    private Organization organization;
    private OrganizationRequestDTO requestDTO;
    private OrganizationResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Prepare test data
        organization = new Organization();
        organization.setId(1L);
        organization.setName("Test Organization");
        organization.setDescription("Test Description");
        organization.setTaxId("123456789");

        requestDTO = new OrganizationRequestDTO();
        requestDTO.setName("Test Organization");
        requestDTO.setDescription("Test Description");
        requestDTO.setTaxId("123456789");

        responseDTO = new OrganizationResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Test Organization");
        responseDTO.setDescription("Test Description");
        responseDTO.setTaxId("123456789");
    }

    @Test
    void createOrganization_ShouldReturnCreatedOrganization() {
        // Arrange
        when(modelMapper.map(any(OrganizationRequestDTO.class), eq(Organization.class))).thenReturn(organization);
        when(organizationRepository.save(any(Organization.class))).thenReturn(organization);
        when(modelMapper.map(any(Organization.class), eq(OrganizationResponseDTO.class))).thenReturn(responseDTO);

        // Act
        OrganizationResponseDTO result = organizationService.createOrganization(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Organization", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals("123456789", result.getTaxId());
        verify(organizationRepository).save(any(Organization.class));
    }

    @Test
    void getAllOrganizations_ShouldReturnAllOrganizations() {
        // Arrange
        Organization org2 = new Organization();
        org2.setId(2L);
        org2.setName("Second Organization");

        OrganizationResponseDTO resp2 = new OrganizationResponseDTO();
        resp2.setId(2L);
        resp2.setName("Second Organization");

        List<Organization> organizations = Arrays.asList(organization, org2);
        
        when(organizationRepository.findAll()).thenReturn(organizations);
        when(modelMapper.map(eq(organization), eq(OrganizationResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(eq(org2), eq(OrganizationResponseDTO.class))).thenReturn(resp2);

        // Act
        List<OrganizationResponseDTO> result = organizationService.getAllOrganizations();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(organizationRepository).findAll();
    }

    @Test
    void getOrganizationById_WhenExists_ShouldReturnOrganization() {
        // Arrange
        when(organizationRepository.findById(anyLong())).thenReturn(Optional.of(organization));
        when(modelMapper.map(any(Organization.class), eq(OrganizationResponseDTO.class))).thenReturn(responseDTO);

        // Act
        OrganizationResponseDTO result = organizationService.getOrganizationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Organization", result.getName());
        verify(organizationRepository).findById(1L);
    }

    @Test
    void getOrganizationById_WhenNotExists_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(organizationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            organizationService.getOrganizationById(1L);
        });
        verify(organizationRepository).findById(1L);
    }

    @Test
    void updateOrganization_WhenExists_ShouldReturnUpdatedOrganization() {
        // Arrange
        when(organizationRepository.findById(anyLong())).thenReturn(Optional.of(organization));
        when(organizationRepository.save(any(Organization.class))).thenReturn(organization);
        when(modelMapper.map(any(Organization.class), eq(OrganizationResponseDTO.class))).thenReturn(responseDTO);

        // Act
        OrganizationResponseDTO result = organizationService.updateOrganization(1L, requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Organization", result.getName());
        verify(organizationRepository).findById(1L);
        verify(organizationRepository).save(any(Organization.class));
    }

    @Test
    void updateOrganization_WhenNotExists_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(organizationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            organizationService.updateOrganization(1L, requestDTO);
        });
        verify(organizationRepository).findById(1L);
        verify(organizationRepository, never()).save(any(Organization.class));
    }

    @Test
    void deleteOrganization_WhenExists_ShouldDeleteOrganization() {
        // Arrange
        when(organizationRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(organizationRepository).deleteById(anyLong());

        // Act
        organizationService.deleteOrganization(1L);

        // Assert
        verify(organizationRepository).existsById(1L);
        verify(organizationRepository).deleteById(1L);
    }

    @Test
    void deleteOrganization_WhenNotExists_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(organizationRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            organizationService.deleteOrganization(1L);
        });
        verify(organizationRepository).existsById(1L);
        verify(organizationRepository, never()).deleteById(anyLong());
    }

    @Test
    void searchOrganizationsByName_ShouldReturnMatchingOrganizations() {
        // Arrange
        List<Organization> organizations = Arrays.asList(organization);
        when(organizationRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(organizations);
        when(modelMapper.map(any(Organization.class), eq(OrganizationResponseDTO.class))).thenReturn(responseDTO);

        // Act
        List<OrganizationResponseDTO> result = organizationService.searchOrganizationsByName("Test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Organization", result.get(0).getName());
        verify(organizationRepository).findByNameContainingIgnoreCase("Test");
    }

    @Test
    void getOrganizationByTaxId_WhenExists_ShouldReturnOrganization() {
        // Arrange
        when(organizationRepository.findByTaxId(anyString())).thenReturn(Optional.of(organization));
        when(modelMapper.map(any(Organization.class), eq(OrganizationResponseDTO.class))).thenReturn(responseDTO);

        // Act
        OrganizationResponseDTO result = organizationService.getOrganizationByTaxId("123456789");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("123456789", result.getTaxId());
        verify(organizationRepository).findByTaxId("123456789");
    }

    @Test
    void getOrganizationByTaxId_WhenNotExists_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(organizationRepository.findByTaxId(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            organizationService.getOrganizationByTaxId("123456789");
        });
        verify(organizationRepository).findByTaxId("123456789");
    }
}