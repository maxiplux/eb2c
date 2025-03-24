package app.quantun.eb2c.service;


import app.quantun.eb2c.exception.CognitoException;
import app.quantun.eb2c.exception.InvalidSortFieldException;
import app.quantun.eb2c.model.contract.contract.request.GroupRequest;
import app.quantun.eb2c.model.contract.contract.request.PaginationRequest;
import app.quantun.eb2c.model.contract.contract.response.GroupResponse;
import app.quantun.eb2c.model.contract.contract.response.PagedResponse;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CognitoGroupServiceTest {

    private final String USER_POOL_ID = "test-user-pool-id";
    @Mock
    private CognitoIdentityProviderClient cognitoClient;
    @InjectMocks
    private CognitoGroupService groupService;
    private GroupRequest groupRequest;
    private GroupType groupType;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(groupService, "userPoolId", USER_POOL_ID);

        // Initialize test data
        groupRequest = new GroupRequest();
        groupRequest.setGroupName("TestGroup");
        groupRequest.setDescription("A test group");
        groupRequest.setPrecedence(10);

        // Create mock group type
        groupType = GroupType.builder()
                .groupName("TestGroup")
                .description("A test group")
                .precedence(10)
                .creationDate(Instant.now())
                .lastModifiedDate(Instant.now())
                .build();

        // Mock listUsersInGroup - use lenient() to prevent UnnecessaryStubbingException
        ListUsersInGroupResponse listUsersResponse = ListUsersInGroupResponse.builder()
                .users(Arrays.asList(
                        UserType.builder().username("user1").build(),
                        UserType.builder().username("user2").build()
                ))
                .build();
        lenient().when(cognitoClient.listUsersInGroup(any(ListUsersInGroupRequest.class)))
                .thenReturn(listUsersResponse);
    }

    @Test
    void createGroupTest() {
        // Arrange
        CreateGroupResponse response = CreateGroupResponse.builder()
                .group(groupType)
                .build();

        when(cognitoClient.createGroup(any(CreateGroupRequest.class))).thenReturn(response);

        // Act
        GroupResponse result = groupService.createGroup(groupRequest);

        // Assert
        assertNotNull(result);
        assertEquals("TestGroup", result.getGroupName());
        assertEquals("A test group", result.getDescription());
        assertEquals(10, result.getPrecedence());
        verify(cognitoClient).createGroup(any(CreateGroupRequest.class));
    }

    @Test
    void createGroupThrowsExceptionWhenGroupExists() {
        // Arrange
        when(cognitoClient.createGroup(any(CreateGroupRequest.class)))
                .thenThrow(GroupExistsException.builder().message("Group already exists").build());

        // Act & Assert
        assertThrows(CognitoException.class, () -> groupService.createGroup(groupRequest));
        verify(cognitoClient).createGroup(any(CreateGroupRequest.class));
    }

    @Test
    void getGroupByNameTest() {
        // Arrange
        GetGroupResponse response = GetGroupResponse.builder()
                .group(groupType)
                .build();

        when(cognitoClient.getGroup(any(GetGroupRequest.class))).thenReturn(response);

        // Act
        GroupResponse result = groupService.getGroupByName("TestGroup");

        // Assert
        assertNotNull(result);
        assertEquals("TestGroup", result.getGroupName());
        assertEquals("A test group", result.getDescription());
        assertEquals(10, result.getPrecedence());
        verify(cognitoClient).getGroup(any(GetGroupRequest.class));
    }

    @Test
    void getGroupByNameThrowsExceptionWhenGroupNotFound() {
        // Arrange
        when(cognitoClient.getGroup(any(GetGroupRequest.class)))
                .thenThrow(software.amazon.awssdk.services.cognitoidentityprovider.model.ResourceNotFoundException
                        .builder().message("Group not found").build());

        // Act & Assert
        app.quantun.eb2c.exception.ResourceNotFoundException exception = assertThrows(
                app.quantun.eb2c.exception.ResourceNotFoundException.class,
                () -> groupService.getGroupByName("NonExistentGroup")
        );

        assertEquals("Group not found: NonExistentGroup", exception.getMessage());
        verify(cognitoClient).getGroup(any(GetGroupRequest.class));
    }

    @Test
    void listGroupsTest() {
        // Arrange
        ListGroupsResponse response = ListGroupsResponse.builder()
                .groups(Collections.singletonList(groupType))
                .build();

        when(cognitoClient.listGroups(any(ListGroupsRequest.class))).thenReturn(response);

        PaginationRequest pagination = new PaginationRequest(0, 20, "groupName", "asc", null);

        // Act
        PagedResponse<GroupResponse> result = groupService.listGroups(pagination);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("TestGroup", result.getContent().get(0).getGroupName());
        assertEquals(0, result.getPage());
        assertEquals(20, result.getSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLast());
        verify(cognitoClient).listGroups(any(ListGroupsRequest.class));
    }

    @Test
    void listGroupsInvalidSortFieldTest() {
        // Arrange
        PaginationRequest pagination = new PaginationRequest(0, 20, "invalidField", "asc", null);

        // Act & Assert
        assertThrows(InvalidSortFieldException.class, () -> groupService.listGroups(pagination));
    }

    @Test
    void updateGroupTest() {
        // Arrange
        UpdateGroupResponse updateResponse = UpdateGroupResponse.builder().build();
        when(cognitoClient.updateGroup(any(UpdateGroupRequest.class))).thenReturn(updateResponse);

        // Mock the getGroupByName behavior
        GetGroupResponse getResponse = GetGroupResponse.builder()
                .group(groupType)
                .build();
        when(cognitoClient.getGroup(any(GetGroupRequest.class))).thenReturn(getResponse);

        // Act
        GroupResponse result = groupService.updateGroup("TestGroup", groupRequest);

        // Assert
        assertNotNull(result);
        assertEquals("TestGroup", result.getGroupName());
        assertEquals("A test group", result.getDescription());
        assertEquals(10, result.getPrecedence());
        verify(cognitoClient).updateGroup(any(UpdateGroupRequest.class));
        verify(cognitoClient, atLeastOnce()).getGroup(any(GetGroupRequest.class));
    }

    @Test
    void deleteGroupTest() {
        // Arrange
        DeleteGroupResponse deleteResponse = DeleteGroupResponse.builder().build();
        when(cognitoClient.deleteGroup(any(DeleteGroupRequest.class))).thenReturn(deleteResponse);

        // Mock the getGroupByName behavior
        GetGroupResponse getResponse = GetGroupResponse.builder()
                .group(groupType)
                .build();
        when(cognitoClient.getGroup(any(GetGroupRequest.class))).thenReturn(getResponse);

        // Act
        groupService.deleteGroup("TestGroup");

        // Assert
        verify(cognitoClient).deleteGroup(any(DeleteGroupRequest.class));
    }

    @Test
    void getGroupUsersTest() {
        // Arrange
        ListUsersInGroupResponse response = ListUsersInGroupResponse.builder()
                .users(Arrays.asList(
                        UserType.builder().username("user1").build(),
                        UserType.builder().username("user2").build(),
                        UserType.builder().username("user3").build()
                ))
                .build();

        // Override the mock from setUp for this specific test
        when(cognitoClient.listUsersInGroup(any(ListUsersInGroupRequest.class))).thenReturn(response);

        // Mock getGroup for the verification
        GetGroupResponse getResponse = GetGroupResponse.builder()
                .group(groupType)
                .build();
        when(cognitoClient.getGroup(any(GetGroupRequest.class))).thenReturn(getResponse);

        // Act
        List<String> result = groupService.getGroupUsers("TestGroup");

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("user1"));
        assertTrue(result.contains("user2"));
        assertTrue(result.contains("user3"));
        verify(cognitoClient).listUsersInGroup(any(ListUsersInGroupRequest.class));
        verify(cognitoClient).getGroup(any(GetGroupRequest.class));
    }

    @Test
    void addUserToGroupTest() {
        // Arrange
        AdminAddUserToGroupResponse addResponse = AdminAddUserToGroupResponse.builder().build();
        when(cognitoClient.adminAddUserToGroup(any(AdminAddUserToGroupRequest.class))).thenReturn(addResponse);

        // Mock the getGroupByName behavior
        GetGroupResponse getResponse = GetGroupResponse.builder()
                .group(groupType)
                .build();
        when(cognitoClient.getGroup(any(GetGroupRequest.class))).thenReturn(getResponse);

        // Act
        GroupResponse result = groupService.addUserToGroup("TestGroup", "testuser");

        // Assert
        assertNotNull(result);
        assertEquals("TestGroup", result.getGroupName());
        assertNotNull(result.getUsers());
        assertEquals(2, result.getUsers().size()); // Should match the users we mocked in setUp
        verify(cognitoClient).adminAddUserToGroup(any(AdminAddUserToGroupRequest.class));
        verify(cognitoClient, atLeastOnce()).getGroup(any(GetGroupRequest.class));
        verify(cognitoClient).listUsersInGroup(any(ListUsersInGroupRequest.class));
    }

    @Test
    void removeUserFromGroupTest() {
        // Arrange
        AdminRemoveUserFromGroupResponse removeResponse = AdminRemoveUserFromGroupResponse.builder().build();
        when(cognitoClient.adminRemoveUserFromGroup(any(AdminRemoveUserFromGroupRequest.class))).thenReturn(removeResponse);

        // Mock the getGroupByName behavior
        GetGroupResponse getResponse = GetGroupResponse.builder()
                .group(groupType)
                .build();
        when(cognitoClient.getGroup(any(GetGroupRequest.class))).thenReturn(getResponse);

        // Act
        GroupResponse result = groupService.removeUserFromGroup("TestGroup", "testuser");

        // Assert
        assertNotNull(result);
        assertEquals("TestGroup", result.getGroupName());
        assertNotNull(result.getUsers());
        assertEquals(2, result.getUsers().size()); // Should match the users we mocked in setUp
        verify(cognitoClient).adminRemoveUserFromGroup(any(AdminRemoveUserFromGroupRequest.class));
        verify(cognitoClient, atLeastOnce()).getGroup(any(GetGroupRequest.class));
        verify(cognitoClient).listUsersInGroup(any(ListUsersInGroupRequest.class));
    }
} 