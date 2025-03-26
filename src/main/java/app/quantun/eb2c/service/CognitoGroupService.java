package app.quantun.eb2c.service;


import app.quantun.eb2c.exception.CognitoException;
import app.quantun.eb2c.exception.InvalidSortFieldException;
import app.quantun.eb2c.exception.ResourceNotFoundException;
import app.quantun.eb2c.model.contract.contract.request.GroupRequest;
import app.quantun.eb2c.model.contract.contract.request.PaginationRequest;
import app.quantun.eb2c.model.contract.contract.response.GroupResponse;
import app.quantun.eb2c.model.contract.contract.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CognitoGroupService {

    private static final Set<String> VALID_SORT_FIELDS = Set.of(
            "groupName", "description", "precedence", "creationDate"
    );
    private final CognitoIdentityProviderClient cognitoClient;
    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    /**
     * Creates a new group in AWS Cognito
     *
     * @param groupRequest Group data to create
     * @return Information about the created group
     */
    public GroupResponse createGroup(GroupRequest groupRequest) {
        try {
            CreateGroupRequest.Builder requestBuilder = CreateGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupRequest.getGroupName());

            if (groupRequest.getDescription() != null) {
                requestBuilder.description(groupRequest.getDescription());
            }

            if (groupRequest.getPrecedence() != null) {
                requestBuilder.precedence(groupRequest.getPrecedence());
            }

            CreateGroupResponse response = cognitoClient.createGroup(requestBuilder.build());

            return mapToGroupResponse(response.group());
        } catch (GroupExistsException e) {
            throw new CognitoException("Group already exists: " + groupRequest.getGroupName(), e);
        } catch (InvalidParameterException e) {
            throw new CognitoException("Invalid parameter: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new CognitoException("Error creating group: " + e.getMessage(), e);
        }
    }

    /**
     * Gets a group by its name
     *
     * @param groupName Group name
     * @return Group information
     */
    public GroupResponse getGroupByName(String groupName) {
        try {
            GetGroupRequest request = GetGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName)
                    .build();

            GetGroupResponse response = cognitoClient.getGroup(request);

            return mapToGroupResponse(response.group());
        } catch (software.amazon.awssdk.services.cognitoidentityprovider.model.ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Group not found: " + groupName);
        } catch (Exception e) {
            throw new CognitoException("Error getting group: " + e.getMessage(), e);
        }
    }

    /**
     * Lists groups with pagination, filtering, and sorting
     *
     * @param pagination Pagination parameters
     * @return Paginated list of groups
     */
    public PagedResponse<GroupResponse> listGroups(PaginationRequest pagination) {
        try {
            // Validate sort field
            if (pagination.getSortBy() != null && !VALID_SORT_FIELDS.contains(pagination.getSortBy())) {
                throw new InvalidSortFieldException("Invalid sort field: " + pagination.getSortBy() +
                        ". Valid values: " + String.join(", ", VALID_SORT_FIELDS));
            }

            // Build the request to Cognito
            ListGroupsRequest.Builder requestBuilder = ListGroupsRequest.builder()
                    .userPoolId(userPoolId)
                    .limit(Math.min(60, pagination.getSize() * 3)); // Request more to allow filtering

            ListGroupsResponse response = cognitoClient.listGroups(requestBuilder.build());

            // Map results
            List<GroupResponse> groups = response.groups().stream()
                    .map(this::mapToGroupResponse)
                    .collect(Collectors.toList());

            // Filter results if there is a filter
            if (pagination.getFilter() != null && !pagination.getFilter().isEmpty()) {
                String filter = pagination.getFilter().toLowerCase();
                groups = groups.stream()
                        .filter(group ->
                                group.getGroupName().toLowerCase().contains(filter) ||
                                        (group.getDescription() != null && group.getDescription().toLowerCase().contains(filter))
                        )
                        .collect(Collectors.toList());
            }

            // Sort results according to the specified criteria
            if (pagination.getSortBy() != null) {
                sortGroups(groups, pagination.getSortBy(), pagination.getSortDirection());
            }

            // Apply pagination
            int totalElements = groups.size();
            int totalPages = (int) Math.ceil((double) totalElements / pagination.getSize());

            int fromIndex = pagination.getPage() * pagination.getSize();
            if (fromIndex >= totalElements) {
                return new PagedResponse<>(
                        Collections.emptyList(),
                        pagination.getPage(),
                        pagination.getSize(),
                        totalElements,
                        totalPages,
                        true,
                        pagination.getSortBy(),
                        pagination.getSortDirection(),
                        pagination.getFilter()
                );
            }

            int toIndex = Math.min(fromIndex + pagination.getSize(), totalElements);
            List<GroupResponse> pagedContent = groups.subList(fromIndex, toIndex);

            return new PagedResponse<>(
                    pagedContent,
                    pagination.getPage(),
                    pagination.getSize(),
                    totalElements,
                    totalPages,
                    pagination.getPage() >= totalPages - 1,
                    pagination.getSortBy(),
                    pagination.getSortDirection(),
                    pagination.getFilter()
            );
        } catch (InvalidSortFieldException e) {
            throw e;
        } catch (Exception e) {
            throw new CognitoException("Error listing groups: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing group
     *
     * @param groupName    Name of the group to update
     * @param groupRequest Updated group data
     * @return Updated group information
     */
    public GroupResponse updateGroup(String groupName, GroupRequest groupRequest) {
        try {
            // Verify that the group exists
            getGroupByName(groupName);

            UpdateGroupRequest.Builder requestBuilder = UpdateGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName);

            if (groupRequest.getDescription() != null) {
                requestBuilder.description(groupRequest.getDescription());
            }

            if (groupRequest.getPrecedence() != null) {
                requestBuilder.precedence(groupRequest.getPrecedence());
            }

            cognitoClient.updateGroup(requestBuilder.build());

            return getGroupByName(groupName);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CognitoException("Error updating group: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a group from AWS Cognito
     *
     * @param groupName Name of the group to delete
     */
    public void deleteGroup(String groupName) {
        try {
            // Verify that the group exists
            getGroupByName(groupName);

            DeleteGroupRequest request = DeleteGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName)
                    .build();

            cognitoClient.deleteGroup(request);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CognitoException("Error deleting group: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the list of users in a group
     *
     * @param groupName Group name
     * @return List of usernames belonging to the group
     */
    public List<String> getGroupUsers(String groupName) {
        try {
            // Verify that the group exists
            getGroupByName(groupName);

            ListUsersInGroupRequest request = ListUsersInGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName)
                    .build();

            ListUsersInGroupResponse response = cognitoClient.listUsersInGroup(request);

            if (response == null || response.users() == null) {
                return Collections.emptyList();
            }

            return response.users().stream()
                    .map(UserType::username)
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CognitoException("Error getting users in group: " + e.getMessage(), e);
        }
    }

    /**
     * Adds a user to a group
     *
     * @param groupName Group name
     * @param username  Username
     * @return Updated group information
     */
    public GroupResponse addUserToGroup(String groupName, String username) {
        try {
            // Verify that the group exists
            getGroupByName(groupName);

            AdminAddUserToGroupRequest request = AdminAddUserToGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName)
                    .username(username)
                    .build();

            cognitoClient.adminAddUserToGroup(request);

            // Get updated group with user list
            GetGroupRequest getRequest = GetGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName)
                    .build();

            GetGroupResponse response = cognitoClient.getGroup(getRequest);
            return mapToGroupResponseWithUsers(response.group());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CognitoException("Error adding user to group: " + e.getMessage(), e);
        }
    }

    /**
     * Removes a user from a group
     *
     * @param groupName Group name
     * @param username  Username
     * @return Updated group information
     */
    public GroupResponse removeUserFromGroup(String groupName, String username) {
        try {
            // Verify that the group exists
            getGroupByName(groupName);

            AdminRemoveUserFromGroupRequest request = AdminRemoveUserFromGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName)
                    .username(username)
                    .build();

            cognitoClient.adminRemoveUserFromGroup(request);

            // Get updated group with user list
            GetGroupRequest getRequest = GetGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName)
                    .build();

            GetGroupResponse response = cognitoClient.getGroup(getRequest);
            return mapToGroupResponseWithUsers(response.group());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CognitoException("Error removing user from group: " + e.getMessage(), e);
        }
    }

    // Helper methods

    private GroupResponse mapToGroupResponse(GroupType groupType) {
        return GroupResponse.builder()
                .groupName(groupType.groupName())
                .description(groupType.description())
                .precedence(groupType.precedence())
                .creationDate(groupType.creationDate())
                .lastModifiedDate(groupType.lastModifiedDate())
                .build();
    }

    /**
     * Maps a Cognito group to the response DTO and includes the users list
     */
    private GroupResponse mapToGroupResponseWithUsers(GroupType groupType) {
        GroupResponse response = mapToGroupResponse(groupType);
        response.setUsers(getGroupUsers(groupType.groupName()));
        return response;
    }

    private void sortGroups(List<GroupResponse> groups, String sortBy, String sortDirection) {
        Comparator<GroupResponse> comparator = switch (sortBy) {
            case "groupName" ->
                    Comparator.comparing(GroupResponse::getGroupName, Comparator.nullsLast(String::compareTo));
            case "description" ->
                    Comparator.comparing(GroupResponse::getDescription, Comparator.nullsLast(String::compareTo));
            case "precedence" ->
                    Comparator.comparing(GroupResponse::getPrecedence, Comparator.nullsLast(Integer::compareTo));
            case "creationDate" ->
                    Comparator.comparing(GroupResponse::getCreationDate, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(GroupResponse::getGroupName, Comparator.nullsLast(String::compareTo));
        };

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        groups.sort(comparator);
    }
} 