package app.quantun.eb2c.rest;

import app.quantun.eb2c.model.contract.request.UserRequestDTO;
import app.quantun.eb2c.model.contract.response.UserResponseDTO;
import app.quantun.eb2c.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc() // Disable Spring Security filters for MVC tests
public class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;
    private List<UserResponseDTO> userResponseDTOList;

    @BeforeEach
    public void setup() {
        // Setup test data
        userRequestDTO = UserRequestDTO.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .roleIds(new HashSet<>(Arrays.asList(1L, 2L)))
                .build();

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setUsername("testuser");
        userResponseDTO.setEmail("test@example.com");

        UserResponseDTO userResponseDTO2 = new UserResponseDTO();
        userResponseDTO2.setId(2L);
        userResponseDTO2.setUsername("anotheruser");
        userResponseDTO2.setEmail("another@example.com");

        userResponseDTOList = Arrays.asList(userResponseDTO, userResponseDTO2);
    }

    // Test with admin role (authorized)
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createUser_asAdmin_shouldSucceed() throws Exception {
        given(userService.createUser(any(UserRequestDTO.class))).willReturn(userResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    // Test with user role (unauthorized)
    @Test
    @WithMockUser(roles = "USER")
    public void createUser_asUser_shouldBeForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isForbidden());
    }

    // Test with anonymous user (unauthorized)
    @Test
    @WithAnonymousUser
    public void createUser_asAnonymous_shouldBeUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllUsers_asAdmin_shouldSucceed() throws Exception {
        given(userService.getAllUsers()).willReturn(userResponseDTOList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getAllUsers_asUser_shouldBeForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getUserById_asAdmin_shouldSucceed() throws Exception {
        given(userService.getUserById(1L)).willReturn(userResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getUserByUsername_asAdmin_shouldSucceed() throws Exception {
        given(userService.getUserByUsername("testuser")).willReturn(userResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/username/testuser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateUser_asAdmin_shouldSucceed() throws Exception {
        given(userService.updateUser(eq(1L), any(UserRequestDTO.class))).willReturn(userResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteUser_asAdmin_shouldSucceed() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteUser_asUser_shouldBeForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void usernameExists_asAdmin_shouldSucceed() throws Exception {
        given(userService.existsByUsername("testuser")).willReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/exists/testuser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
