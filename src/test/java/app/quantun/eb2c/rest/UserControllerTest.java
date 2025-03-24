package app.quantun.eb2c.rest;


import app.quantun.eb2c.Eb2cApplication;
import app.quantun.eb2c.TestConfig;
import app.quantun.eb2c.model.contract.contract.request.PaginationRequest;
import app.quantun.eb2c.model.contract.contract.request.UserRequest;
import app.quantun.eb2c.model.contract.contract.response.PagedResponse;
import app.quantun.eb2c.model.contract.contract.response.UserResponse;
import app.quantun.eb2c.service.CognitoUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Eb2cApplication.class)
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CognitoUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequest userRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        // Initialize test data
        userRequest = new UserRequest();
        userRequest.setUsername("testuser");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("Password123!");
        userRequest.setPhoneNumber("+12345678901");
        userRequest.setEmailVerified(true);
        userRequest.setPhoneNumberVerified(true);
        userRequest.setAttributes(new HashMap<>());

        userResponse = UserResponse.builder()
                .username("testuser")
                .email("test@example.com")
                .phoneNumber("+12345678901")
                .userStatus("CONFIRMED")
                .enabled(true)
                .emailVerified(true)
                .phoneNumberVerified(true)
                .userCreateDate(Instant.now())
                .userLastModifiedDate(Instant.now())
                .attributes(new HashMap<>())
                .groups(Arrays.asList("Users"))
                .build();
    }

    @Test
    void createUserTest() throws Exception {
        when(userService.createUser(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUserByUsernameTest() throws Exception {
        when(userService.getUserByUsername("testuser")).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void listUsersTest() throws Exception {
        PagedResponse<UserResponse> pagedResponse = new PagedResponse<>(
                Collections.singletonList(userResponse),
                0, 20, 1, 1, true, "username", "asc", null);

        when(userService.listUsers(any(PaginationRequest.class))).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testuser"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUser(eq("testuser"), any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(put("/api/users/testuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void deleteUserTest() throws Exception {
        doNothing().when(userService).deleteUser("testuser");

        mockMvc.perform(delete("/api/users/testuser"))
                .andExpect(status().isNoContent());
    }

    @Test
    void enableUserTest() throws Exception {
        when(userService.enableUser("testuser")).thenReturn(userResponse);

        mockMvc.perform(post("/api/users/testuser/enable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void disableUserTest() throws Exception {
        UserResponse disabledUser = UserResponse.builder()
                .username("testuser")
                .email("test@example.com")
                .enabled(false)
                .build();

        when(userService.disableUser("testuser")).thenReturn(disabledUser);

        mockMvc.perform(post("/api/users/testuser/disable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.enabled").value(false));
    }

    @Test
    void resetPasswordTest() throws Exception {
        when(userService.resetPassword("testuser")).thenReturn(userResponse);

        mockMvc.perform(post("/api/users/testuser/reset-password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getUserGroupsTest() throws Exception {
        List<String> groups = Arrays.asList("Users", "Admins");
        when(userService.getUserGroups("testuser")).thenReturn(groups);

        mockMvc.perform(get("/api/users/testuser/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Users"))
                .andExpect(jsonPath("$[1]").value("Admins"));
    }

    @Test
    void addUserToGroupTest() throws Exception {
        when(userService.addUserToGroup("testuser", "Admins")).thenReturn(userResponse);

        mockMvc.perform(post("/api/users/testuser/groups/Admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void removeUserFromGroupTest() throws Exception {
        when(userService.removeUserFromGroup("testuser", "Admins")).thenReturn(userResponse);

        mockMvc.perform(delete("/api/users/testuser/groups/Admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }
} 