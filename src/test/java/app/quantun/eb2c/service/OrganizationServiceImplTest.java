package app.quantun.eb2c.service;

import app.quantun.eb2c.cucumber.steps.OrganizationServiceSteps;
import app.quantun.eb2c.model.contract.request.OrganizationRequestDTO;
import app.quantun.eb2c.model.contract.response.OrganizationResponseDTO;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class OrganizationServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(OrganizationServiceSteps.class);

    @Autowired
    private Validator validator;

    @Autowired
    private OrganizationService organizationService;

    private OrganizationRequestDTO requestDTO;
    private OrganizationResponseDTO responseDTO;
    private List<OrganizationResponseDTO> responseDTOList;
    private Exception caughtException;

    @Before
    public void setup() {
        organizationService.deleteAllOrganizations();
        caughtException = null;
    }

    @Given("I have a valid organization request with name {string}, description {string} and tax ID {string}")
    public void iHaveAValidOrganizationRequest(String name, String description, String taxId) {
        requestDTO = OrganizationRequestDTO.builder()
                .name(name)
                .description(description)
                .taxId(taxId)
                .build();
    }

    @When("I create a new organization")
    public void iCreateANewOrganization() {
        try {
            responseDTO = organizationService.createOrganization(requestDTO);
            if (responseDTO == null) {
                System.err.println("Service returned null response");
            }
        } catch (Exception e) {
            caughtException = e;
            log.error("Exception during organization creation: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Then("the organization is successfully created")
    public void theOrganizationIsSuccessfullyCreated() {
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getId());
    }

    @Then("the created organization has the name {string}, description {string} and tax ID {string}")
    public void theCreatedOrganizationHasTheCorrectData(String name, String description, String taxId) {
        assertEquals(name, responseDTO.getName());
        assertEquals(description, responseDTO.getDescription());
        assertEquals(taxId, responseDTO.getTaxId());
    }

    @Given("the following organizations exist:")
    public void theFollowingOrganizationsExist(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            OrganizationRequestDTO dto = OrganizationRequestDTO.builder()
                    .name(row.get("name"))
                    .description(row.get("description"))
                    .taxId(row.get("taxId"))
                    .build();
            validateOrganizationRequestDTO(dto);
            organizationService.createOrganization(dto);
        }
    }

    private void validateOrganizationRequestDTO(OrganizationRequestDTO dto) {
        Set<ConstraintViolation<OrganizationRequestDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<OrganizationRequestDTO> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new ConstraintViolationException("Invalid OrganizationRequestDTO: " + sb.toString(), violations);
        }
    }

    @When("I request all organizations")
    public void iRequestAllOrganizations() {
        try {
            responseDTOList = organizationService.getAllOrganizations();
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("I should receive a list of {int} organizations")
    public void iShouldReceiveAListOfOrganizations(int count) {
        assertEquals(count, responseDTOList.size());
    }

    @When("I request the organization with ID {string}")
    public void iRequestTheOrganizationWithId(String id) {
        try {
            Long orgId = Long.parseLong(id);
            responseDTO = organizationService.getOrganizationById(orgId);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("I should receive the organization details")
    public void iShouldReceiveTheOrganizationDetails() {
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getId());
    }

    @Then("I should receive an entity not found error with message {string}")
    public void iShouldReceiveAnEntityNotFoundErrorWithMessage(String message) {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof EntityNotFoundException);
        assertEquals(message, caughtException.getMessage());
    }

    @When("I update the organization with ID {string} with name {string}, description {string} and tax ID {string}")
    public void iUpdateTheOrganizationWithIdWithNameDescriptionAndTaxId(String id, String name, String description, String taxId) {
        try {
            Long orgId = Long.parseLong(id);
            requestDTO = OrganizationRequestDTO.builder()
                    .name(name)
                    .description(description)
                    .taxId(taxId)
                    .build();
            responseDTO = organizationService.updateOrganization(orgId, requestDTO);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the organization is successfully updated")
    public void theOrganizationIsSuccessfullyUpdated() {
        assertNotNull(responseDTO);
    }

    @Then("the updated organization has the name {string}, description {string} and tax ID {string}")
    public void theUpdatedOrganizationHasTheCorrectData(String name, String description, String taxId) {
        assertEquals(name, responseDTO.getName());
        assertEquals(description, responseDTO.getDescription());
        assertEquals(taxId, responseDTO.getTaxId());
    }

    @When("I delete the organization with ID {string}")
    public void iDeleteTheOrganizationWithId(String id) {
        try {
            Long orgId = Long.parseLong(id);
            organizationService.deleteOrganization(orgId);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the organization is successfully deleted")
    public void theOrganizationIsSuccessfullyDeleted() {
        assertNull(caughtException);
        Long orgId = 1L;
        try {
            organizationService.getOrganizationById(orgId);
            fail("Organization should have been deleted");
        } catch (EntityNotFoundException e) {
            // Expected behavior - organization should not exist
        }
    }

    @When("I search for organizations with name {string}")
    public void iSearchForOrganizationsWithName(String name) {
        try {
            responseDTOList = organizationService.searchOrganizationsByName(name);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the list should contain organizations with names {string} and {string}")
    public void theListShouldContainOrganizationsWithNames(String name1, String name2) {
        List<String> orgNames = responseDTOList.stream()
                .map(OrganizationResponseDTO::getName)
                .collect(Collectors.toList());
        assertTrue(orgNames.contains(name1));
        assertTrue(orgNames.contains(name2));
    }

    @Given("an organization with tax ID {string} exists")
    public void anOrganizationWithTaxIdExists(String taxId) {
        try {
            organizationService.getOrganizationByTaxId(taxId);
        } catch (EntityNotFoundException e) {
            OrganizationRequestDTO dto = OrganizationRequestDTO.builder()
                    .name("Test Organization")
                    .description("Test Description")
                    .taxId(taxId)
                    .build();
            organizationService.createOrganization(dto);
        }
    }

    @When("I request the organization with tax ID {string}")
    public void iRequestTheOrganizationWithTaxId(String taxId) {
        try {
            responseDTO = organizationService.getOrganizationByTaxId(taxId);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("I should receive the organization details with tax ID {string}")
    public void iShouldReceiveTheOrganizationDetailsWithTaxId(String taxId) {
        assertNotNull(responseDTO);
        assertEquals(taxId, responseDTO.getTaxId());
    }

    // Additional step definition to complete the undefined step
    @Given("an organization with ID {string} exists")
    public void anOrganizationWithIdExists(String id) {
        Long expectedId = Long.parseLong(id);
        // Create organization with dummy data using the provided id in the name and tax ID.
        OrganizationRequestDTO dto = OrganizationRequestDTO.builder()
                .name("Test Organization " + id)
                .description("Description for organization " + id)
                .taxId("TAX" + id)
                .build();
        responseDTO = organizationService.createOrganization(dto);
        // Optionally, check if the generated id equals the expected id.
        // This assumes a deterministic setup; if not, you might store the response and map the expected id.
        assertEquals(expectedId, responseDTO.getId(), "The created organization's id should match the expected id");
    }

}