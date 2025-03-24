package app.quantun.eb2c.service;


import app.quantun.eb2c.exception.CognitoException;
import app.quantun.eb2c.exception.InvalidSortFieldException;
import app.quantun.eb2c.exception.ResourceNotFoundException;
import app.quantun.eb2c.model.contract.contract.request.PaginationRequest;
import app.quantun.eb2c.model.contract.contract.request.UserRequest;
import app.quantun.eb2c.model.contract.contract.response.PagedResponse;
import app.quantun.eb2c.model.contract.contract.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CognitoUserServiceTest {

    private final String USER_POOL_ID = "test-user-pool-id";
    private final String CLIENT_ID = "test-client-id";
    @Mock
    private CognitoIdentityProviderClient cognitoClient;
    @InjectMocks
    private CognitoUserService userService;
    private UserRequest userRequest;
    private UserType userType;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "userPoolId", USER_POOL_ID);
        // ReflectionTestUtils.setField(userService, "clientId", CLIENT_ID);

        // Initialize test data
        userRequest = new UserRequest();
        userRequest.setUsername("testuser");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("Password123!");
        userRequest.setPhoneNumber("+12345678901");
        userRequest.setEmailVerified(true);
        userRequest.setPhoneNumberVerified(true);
        userRequest.setAttributes(new HashMap<>());

        // Initialize mock user type for Cognito responses
        List<AttributeType> attributes = new ArrayList<>();
        attributes.add(AttributeType.builder().name("email").value("test@example.com").build());
        attributes.add(AttributeType.builder().name("phone_number").value("+12345678901").build());
        attributes.add(AttributeType.builder().name("email_verified").value("true").build());
        attributes.add(AttributeType.builder().name("phone_number_verified").value("true").build());

        userType = UserType.builder()
                .username("testuser")
                .enabled(true)
                .userStatus(UserStatusType.CONFIRMED)
                .userCreateDate(Instant.now())
                .userLastModifiedDate(Instant.now())
                .attributes(attributes)
                .build();
    }

    @Test
    void createUserTest() {
        // Arrange
        AdminCreateUserResponse response = AdminCreateUserResponse.builder()
                .user(userType)
                .build();

        when(cognitoClient.adminCreateUser(any(AdminCreateUserRequest.class))).thenReturn(response);

        // Mock getUserGroups as it's called within createUser
        when(cognitoClient.adminListGroupsForUser(any(AdminListGroupsForUserRequest.class)))
                .thenReturn(AdminListGroupsForUserResponse.builder()
                        .groups(Collections.singletonList(GroupType.builder().groupName("Users").build()))
                        .build());

        // Act
        UserResponse result = userService.createUser(userRequest);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("+12345678901", result.getPhoneNumber());
        assertTrue(result.getEmailVerified());
        assertTrue(result.getPhoneNumberVerified());
        assertEquals(UserStatusType.CONFIRMED.toString(), result.getUserStatus());
        verify(cognitoClient).adminCreateUser(any(AdminCreateUserRequest.class));
    }

    @Test
    void createUserThrowsExceptionWhenUserExists() {
        // Arrange
        when(cognitoClient.adminCreateUser(any(AdminCreateUserRequest.class)))
                .thenThrow(UsernameExistsException.builder().message("User already exists").build());

        // Act & Assert
        assertThrows(CognitoException.class, () -> userService.createUser(userRequest));
        verify(cognitoClient).adminCreateUser(any(AdminCreateUserRequest.class));
    }

    @Test
    void getUserByUsernameTest() {
        // Arrange
        AdminGetUserResponse response = AdminGetUserResponse.builder()
                .username("testuser")
                .enabled(true)
                .userStatus(UserStatusType.CONFIRMED)
                .userCreateDate(Instant.now())
                .userLastModifiedDate(Instant.now())
                .userAttributes(userType.attributes())
                .build();

        when(cognitoClient.adminGetUser(any(AdminGetUserRequest.class))).thenReturn(response);
        when(cognitoClient.adminListGroupsForUser(any(AdminListGroupsForUserRequest.class)))
                .thenReturn(AdminListGroupsForUserResponse.builder()
                        .groups(Collections.singletonList(GroupType.builder().groupName("Users").build()))
                        .build());

        // Act
        UserResponse result = userService.getUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("+12345678901", result.getPhoneNumber());
        assertTrue(result.getEmailVerified());
        assertTrue(result.getPhoneNumberVerified());
        assertEquals(UserStatusType.CONFIRMED.toString(), result.getUserStatus());
        assertEquals(1, result.getGroups().size());
        assertEquals("Users", result.getGroups().get(0));
        verify(cognitoClient).adminGetUser(any(AdminGetUserRequest.class));
    }

    @Test
    void getUserByUsernameNotFoundTest() {
        // Arrange
        when(cognitoClient.adminGetUser(any(AdminGetUserRequest.class)))
                .thenThrow(UserNotFoundException.builder().message("User not found").build());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername("nonexistentuser"));
        verify(cognitoClient).adminGetUser(any(AdminGetUserRequest.class));
    }

    @Test
    void listUsersTest() {
        // Arrange
        ListUsersResponse response = ListUsersResponse.builder()
                .users(Collections.singletonList(userType))
                .build();

        when(cognitoClient.listUsers(any(ListUsersRequest.class))).thenReturn(response);
        when(cognitoClient.adminListGroupsForUser(any(AdminListGroupsForUserRequest.class)))
                .thenReturn(AdminListGroupsForUserResponse.builder()
                        .groups(Collections.singletonList(GroupType.builder().groupName("Users").build()))
                        .build());

        PaginationRequest pagination = new PaginationRequest(0, 20, "username", "asc", null);

        // Act
        PagedResponse<UserResponse> result = userService.listUsers(pagination);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("testuser", result.getContent().get(0).getUsername());
        assertEquals(0, result.getPage());
        assertEquals(20, result.getSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLast());
        verify(cognitoClient).listUsers(any(ListUsersRequest.class));
    }

    @Test
    void listUsersInvalidSortFieldTest() {
        // Arrange
        PaginationRequest pagination = new PaginationRequest(0, 20, "invalidField", "asc", null);

        // Act & Assert
        assertThrows(InvalidSortFieldException.class, () -> userService.listUsers(pagination));
    }

    @Test
    void deleteUserTest() {
        // Arrange
        // Mock the getUserByUsername call first
        AdminGetUserResponse getUserResponse = AdminGetUserResponse.builder()
                .username("testuser")
                .userStatus(UserStatusType.CONFIRMED)
                .enabled(true)
                .userCreateDate(Instant.now())
                .userLastModifiedDate(Instant.now())
                .userAttributes(userType.attributes())
                .build();

        when(cognitoClient.adminGetUser(any(AdminGetUserRequest.class))).thenReturn(getUserResponse);
        when(cognitoClient.adminListGroupsForUser(any(AdminListGroupsForUserRequest.class)))
                .thenReturn(AdminListGroupsForUserResponse.builder()
                        .groups(Collections.singletonList(GroupType.builder().groupName("Users").build()))
                        .build());

        AdminDeleteUserResponse deleteResponse = AdminDeleteUserResponse.builder().build();
        when(cognitoClient.adminDeleteUser(any(AdminDeleteUserRequest.class))).thenReturn(deleteResponse);

        // Act
        userService.deleteUser("testuser");

        // Assert
        verify(cognitoClient).adminGetUser(any(AdminGetUserRequest.class));
        verify(cognitoClient).adminDeleteUser(any(AdminDeleteUserRequest.class));
    }

    @Test
    void enableUserTest() {
        // Arrange
        AdminEnableUserResponse enableResponse = AdminEnableUserResponse.builder().build();
        when(cognitoClient.adminEnableUser(any(AdminEnableUserRequest.class))).thenReturn(enableResponse);

        // Mock the getUserByUsername behavior
        AdminGetUserResponse getUserResponse = AdminGetUserResponse.builder()
                .username("testuser")
                .userStatus(UserStatusType.CONFIRMED)
                .enabled(true)
                .userCreateDate(Instant.now())
                .userLastModifiedDate(Instant.now())
                .userAttributes(userType.attributes())
                .build();

        when(cognitoClient.adminGetUser(any(AdminGetUserRequest.class))).thenReturn(getUserResponse);
        when(cognitoClient.adminListGroupsForUser(any(AdminListGroupsForUserRequest.class)))
                .thenReturn(AdminListGroupsForUserResponse.builder()
                        .groups(Collections.singletonList(GroupType.builder().groupName("Users").build()))
                        .build());

        // Act
        UserResponse result = userService.enableUser("testuser");

        // Assert
        assertNotNull(result);
        assertTrue(result.getEnabled());
        verify(cognitoClient).adminEnableUser(any(AdminEnableUserRequest.class));
        verify(cognitoClient, times(2)).adminGetUser(any(AdminGetUserRequest.class));
    }

    @Test
    void disableUserTest() {
        // Arrange
        AdminDisableUserResponse disableResponse = AdminDisableUserResponse.builder().build();
        when(cognitoClient.adminDisableUser(any(AdminDisableUserRequest.class))).thenReturn(disableResponse);

        // Mock the getUserByUsername behavior
        AdminGetUserResponse getUserResponse = AdminGetUserResponse.builder()
                .username("testuser")
                .userStatus(UserStatusType.CONFIRMED)
                .enabled(false) // User is now disabled
                .userCreateDate(Instant.now())
                .userLastModifiedDate(Instant.now())
                .userAttributes(userType.attributes())
                .build();

        when(cognitoClient.adminGetUser(any(AdminGetUserRequest.class))).thenReturn(getUserResponse);
        when(cognitoClient.adminListGroupsForUser(any(AdminListGroupsForUserRequest.class)))
                .thenReturn(AdminListGroupsForUserResponse.builder()
                        .groups(Collections.singletonList(GroupType.builder().groupName("Users").build()))
                        .build());

        // Act
        UserResponse result = userService.disableUser("testuser");

        // Assert
        assertNotNull(result);
        assertFalse(result.getEnabled());
        verify(cognitoClient).adminDisableUser(any(AdminDisableUserRequest.class));
        verify(cognitoClient, times(2)).adminGetUser(any(AdminGetUserRequest.class));
    }

    @Test
    void resetPasswordTest() {
        // Arrange
        AdminResetUserPasswordResponse resetResponse = AdminResetUserPasswordResponse.builder().build();
        when(cognitoClient.adminResetUserPassword(any(AdminResetUserPasswordRequest.class))).thenReturn(resetResponse);

        // Mock the getUserByUsername behavior
        AdminGetUserResponse getUserResponse = AdminGetUserResponse.builder()
                .username("testuser")
                .userStatus(UserStatusType.RESET_REQUIRED) // Password reset changes status
                .enabled(true)
                .userCreateDate(Instant.now())
                .userLastModifiedDate(Instant.now())
                .userAttributes(userType.attributes())
                .build();

        when(cognitoClient.adminGetUser(any(AdminGetUserRequest.class))).thenReturn(getUserResponse);
        when(cognitoClient.adminListGroupsForUser(any(AdminListGroupsForUserRequest.class)))
                .thenReturn(AdminListGroupsForUserResponse.builder()
                        .groups(Collections.singletonList(GroupType.builder().groupName("Users").build()))
                        .build());

        // Act
        UserResponse result = userService.resetPassword("testuser");

        // Assert
        assertNotNull(result);
        assertEquals(UserStatusType.RESET_REQUIRED.toString(), result.getUserStatus());
        verify(cognitoClient).adminResetUserPassword(any(AdminResetUserPasswordRequest.class));
        verify(cognitoClient, times(2)).adminGetUser(any(AdminGetUserRequest.class));
    }

    @Test
    void getUserGroupsTest() {
        // Arrange
        AdminListGroupsForUserResponse response = AdminListGroupsForUserResponse.builder()
                .groups(Arrays.asList(
                        GroupType.builder().groupName("Users").build(),
                        GroupType.builder().groupName("Admins").build()
                ))
                .build();

        when(cognitoClient.adminListGroupsForUser(any(AdminListGroupsForUserRequest.class))).thenReturn(response);

        // Act
        List<String> result = userService.getUserGroups("testuser");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Users"));
        assertTrue(result.contains("Admins"));
        verify(cognitoClient).adminListGroupsForUser(any(AdminListGroupsForUserRequest.class));
    }

    @Test
    void addUserToGroupTest() {
        // Arrange
        AdminAddUserToGroupResponse addResponse = AdminAddUserToGroupResponse.builder().build();
        when(cognitoClient.adminAddUserToGroup(any(AdminAddUserToGroupRequest.class))).thenReturn(addResponse);

        // Mock the getUserByUsername behavior
        AdminGetUserResponse getUserResponse = AdminGetUserResponse.builder()
                .username("testuser")
                .userStatus(UserStatusType.CONFIRMED)
                .enabled(true)
                .userCreateDate(Instant.now())
                .userLastModifiedDate(Instant.now())
                .userAttributes(userType.attributes())
                .build();

        when(cognitoClient.adminGetUser(any(AdminGetUserRequest.class))).thenReturn(getUserResponse);

        // Updated group list after addition
        when(cognitoClient.adminListGroupsForUser(any(AdminListGroupsForUserRequest.class)))
                .thenReturn(AdminListGroupsForUserResponse.builder()
                        .groups(Arrays.asList(
                                GroupType.builder().groupName("Users").build(),
                                GroupType.builder().groupName("Admins").build()
                        ))
                        .build());

        // Act
        UserResponse result = userService.addUserToGroup("testuser", "Admins");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getGroups().size());
        assertTrue(result.getGroups().contains("Admins"));
        verify(cognitoClient).adminAddUserToGroup(any(AdminAddUserToGroupRequest.class));
        verify(cognitoClient, times(2)).adminGetUser(any(AdminGetUserRequest.class));
    }

    @Test
    void removeUserFromGroupTest() {
        // Arrange
        AdminRemoveUserFromGroupResponse removeResponse = AdminRemoveUserFromGroupResponse.builder().build();
        when(cognitoClient.adminRemoveUserFromGroup(any(AdminRemoveUserFromGroupRequest.class))).thenReturn(removeResponse);

        // Mock the getUserByUsername behavior
        AdminGetUserResponse getUserResponse = AdminGetUserResponse.builder()
                .username("testuser")
                .userStatus(UserStatusType.CONFIRMED)
                .enabled(true)
                .userCreateDate(Instant.now())
                .userLastModifiedDate(Instant.now())
                .userAttributes(userType.attributes())
                .build();

        when(cognitoClient.adminGetUser(any(AdminGetUserRequest.class))).thenReturn(getUserResponse);

        // Updated group list after removal (only Users remains)
        when(cognitoClient.adminListGroupsForUser(any(AdminListGroupsForUserRequest.class)))
                .thenReturn(AdminListGroupsForUserResponse.builder()
                        .groups(Collections.singletonList(GroupType.builder().groupName("Users").build()))
                        .build());

        // Act
        UserResponse result = userService.removeUserFromGroup("testuser", "Admins");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getGroups().size());
        assertEquals("Users", result.getGroups().get(0));
        verify(cognitoClient).adminRemoveUserFromGroup(any(AdminRemoveUserFromGroupRequest.class));
        verify(cognitoClient, times(2)).adminGetUser(any(AdminGetUserRequest.class));
    }
} 