package app.quantun.eb2c.rest;

import app.quantun.eb2c.Eb2cApplication;
import app.quantun.eb2c.TestConfig;
import app.quantun.eb2c.model.contract.request.OrganizationRequestDTO;
import app.quantun.eb2c.model.contract.response.OrganizationResponseDTO;
import app.quantun.eb2c.service.OrganizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Eb2cApplication.class)
@AutoConfigureMockMvc
@Import(TestConfig.class)
class OrganizationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrganizationService organizationService;

    private OrganizationRequestDTO requestDTO;
    private OrganizationResponseDTO responseDTO;
    private List<OrganizationResponseDTO> responseDTOList;

    @BeforeEach
    void setUp() {
        // Prepare test data
        requestDTO = new OrganizationRequestDTO();
        requestDTO.setName("Test Organization");
        requestDTO.setDescription("Test Description");
        requestDTO.setTaxId("123456789");

        responseDTO = new OrganizationResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Test Organization");
        responseDTO.setDescription("Test Description");
        responseDTO.setTaxId("123456789");

        OrganizationResponseDTO responseDTO2 = new OrganizationResponseDTO();
        responseDTO2.setId(2L);
        responseDTO2.setName("Second Organization");
        responseDTO2.setDescription("Another Description");
        responseDTO2.setTaxId("987654321");

        responseDTOList = Arrays.asList(responseDTO, responseDTO2);

        // We don't need to recreate mockMvc since @SpringBootTest provides the real application context
        // Including all configured exception handlers
    }

    @Test
    void createOrganization_ShouldReturnCreatedOrganization() throws Exception {
        // Arrange
        when(organizationService.createOrganization(any(OrganizationRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name", is("Test Organization")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.taxId", is("123456789")));

        verify(organizationService).createOrganization(any(OrganizationRequestDTO.class));
    }

    @Test
    void getAllOrganizations_ShouldReturnAllOrganizations() throws Exception {
        // Arrange
        when(organizationService.getAllOrganizations()).thenReturn(responseDTOList);

        // Act & Assert
        mockMvc.perform(get("/api/organizations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name", is("Test Organization")))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name", is("Second Organization")));

        verify(organizationService).getAllOrganizations();
    }

    @Test
    void getOrganizationById_WhenExists_ShouldReturnOrganization() throws Exception {
        // Arrange
        when(organizationService.getOrganizationById(anyLong())).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/organizations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name", is("Test Organization")));

        verify(organizationService).getOrganizationById(1L);
    }

    @Test
    void getOrganizationById_WhenNotExists_ShouldReturnProblemDetail() throws Exception {
        // Arrange
        String errorMessage = "Organization not found with id: 1";
        when(organizationService.getOrganizationById(anyLong())).thenThrow(new EntityNotFoundException(errorMessage));

        // Act & Assert
        mockMvc.perform(get("/api/organizations/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(organizationService).getOrganizationById(1L);
    }

    @Test
    void updateOrganization_WhenExists_ShouldReturnUpdatedOrganization() throws Exception {
        // Arrange
        when(organizationService.updateOrganization(anyLong(), any(OrganizationRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/organizations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name", is("Test Organization")));

        verify(organizationService).updateOrganization(eq(1L), any(OrganizationRequestDTO.class));
    }

    @Test
    void updateOrganization_WhenNotExists_ShouldReturnProblemDetail() throws Exception {
        // Arrange
        String errorMessage = "Organization not found with id: 1";
        when(organizationService.updateOrganization(anyLong(), any(OrganizationRequestDTO.class)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        // Act & Assert
        mockMvc.perform(put("/api/organizations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(organizationService).updateOrganization(eq(1L), any(OrganizationRequestDTO.class));
    }

    @Test
    void deleteOrganization_WhenExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(organizationService).deleteOrganization(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/organizations/1"))
                .andExpect(status().isNoContent());

        verify(organizationService).deleteOrganization(1L);
    }

    @Test
    void deleteOrganization_WhenNotExists_ShouldReturnProblemDetail() throws Exception {
        // Arrange
        String errorMessage = "Organization not found with id: 1";
        doThrow(new EntityNotFoundException(errorMessage)).when(organizationService).deleteOrganization(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/organizations/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(organizationService).deleteOrganization(1L);
    }

    @Test
    void searchOrganizationsByName_ShouldReturnMatchingOrganizations() throws Exception {
        // Arrange
        when(organizationService.searchOrganizationsByName(anyString())).thenReturn(Arrays.asList(responseDTO));

        // Act & Assert
        mockMvc.perform(get("/api/organizations/search?name=Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name", is("Test Organization")));

        verify(organizationService).searchOrganizationsByName("Test");
    }

    @Test
    void getOrganizationByTaxId_WhenExists_ShouldReturnOrganization() throws Exception {
        // Arrange
        when(organizationService.getOrganizationByTaxId(anyString())).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/organizations/tax/123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taxId", is("123456789")));

        verify(organizationService).getOrganizationByTaxId("123456789");
    }

    @Test
    void getOrganizationByTaxId_WhenNotExists_ShouldReturnProblemDetail() throws Exception {
        // Arrange
        String errorMessage = "Organization not found with tax ID: 123456789";
        when(organizationService.getOrganizationByTaxId(anyString())).thenThrow(new EntityNotFoundException(errorMessage));

        // Act & Assert
        mockMvc.perform(get("/api/organizations/tax/123456789"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(organizationService).getOrganizationByTaxId("123456789");
    }

//    @Test
//    void createOrganization_WhenValidationFails_ShouldReturnProblemDetail() throws Exception {
//        // Arrange: Create a request with invalid data (empty name)
//        OrganizationRequestDTO invalidRequest = new OrganizationRequestDTO();
//        invalidRequest.setName(""); // Empty name, will trigger validation error
//        invalidRequest.setDescription("Test Description");
//        invalidRequest.setTaxId("123456789");
//
//        // Act & Assert
//        mockMvc.perform(post("/api/organizations")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.type").exists())
//                .andExpect(jsonPath("$.title").value("Validation Error"))
//                .andExpect(jsonPath("$.status").value(400))
//                .andExpect(jsonPath("$.errors").exists());
//    }
}