package app.quantun.eb2c.service;


import app.quantun.eb2c.exception.CognitoException;
import app.quantun.eb2c.exception.InvalidSortFieldException;
import app.quantun.eb2c.exception.ResourceNotFoundException;
import app.quantun.eb2c.model.contract.contract.request.PaginationRequest;
import app.quantun.eb2c.model.contract.contract.request.UserRequest;
import app.quantun.eb2c.model.contract.contract.response.PagedResponse;
import app.quantun.eb2c.model.contract.contract.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CognitoUserService {

    private static final Set<String> VALID_SORT_FIELDS = Set.of(
            "username", "email", "status", "enabled", "createDate"
    );
    private final CognitoIdentityProviderClient cognitoClient;
    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;


    /**
     * Creates a new user in AWS Cognito
     *
     * @param userRequest User data to create
     * @return Information about the created user
     */
    public UserResponse createUser(UserRequest userRequest) {
        try {
            AdminCreateUserRequest.Builder requestBuilder = AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(userRequest.getUsername())
                    .temporaryPassword(userRequest.getPassword())
                    .messageAction(MessageActionType.SUPPRESS);

            List<AttributeType> userAttributes = new ArrayList<>();
            userAttributes.add(AttributeType.builder().name("email").value(userRequest.getEmail()).build());
            userAttributes.add(AttributeType.builder().name("email_verified").value(
                    Optional.ofNullable(userRequest.getEmailVerified()).orElse(false).toString()).build());

            if (userRequest.getPhoneNumber() != null) {
                userAttributes.add(AttributeType.builder().name("phone_number").value(userRequest.getPhoneNumber()).build());
                userAttributes.add(AttributeType.builder().name("phone_number_verified").value(
                        Optional.ofNullable(userRequest.getPhoneNumberVerified()).orElse(false).toString()).build());
            }

            if (userRequest.getAttributes() != null) {
                userRequest.getAttributes().forEach((name, value) ->
                        userAttributes.add(AttributeType.builder().name(name).value(value).build())
                );
            }

            requestBuilder.userAttributes(userAttributes);

            AdminCreateUserResponse response = cognitoClient.adminCreateUser(requestBuilder.build());
            UserType userType = response.user();

            return mapToUserResponse(userType);
        } catch (UsernameExistsException e) {
            throw new CognitoException("User already exists: " + userRequest.getUsername(), e);
        } catch (InvalidParameterException e) {
            throw new CognitoException("Invalid parameter: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new CognitoException("Error creating user: " + e.getMessage(), e);
        }
    }

    /**
     * Gets a user by username
     *
     * @param username Username
     * @return User information
     */
    public UserResponse getUserByUsername(String username) {
        try {
            AdminGetUserRequest request = AdminGetUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .build();

            AdminGetUserResponse response = cognitoClient.adminGetUser(request);

            return UserResponse.builder()
                    .username(response.username())
                    .userStatus(response.userStatusAsString())
                    .enabled(response.enabled())
                    .userCreateDate(response.userCreateDate())
                    .userLastModifiedDate(response.userLastModifiedDate())
                    .attributes(response.userAttributes().stream()
                            .collect(Collectors.toMap(AttributeType::name, AttributeType::value)))
                    .email(getAttributeValue(response.userAttributes(), "email"))
                    .phoneNumber(getAttributeValue(response.userAttributes(), "phone_number"))
                    .emailVerified(Boolean.parseBoolean(getAttributeValue(response.userAttributes(), "email_verified")))
                    .phoneNumberVerified(Boolean.parseBoolean(getAttributeValue(response.userAttributes(), "phone_number_verified")))
                    .groups(getUserGroups(username))
                    .build();
        } catch (UserNotFoundException e) {
            throw new ResourceNotFoundException("User not found: " + username);
        } catch (Exception e) {
            throw new CognitoException("Error getting user: " + e.getMessage(), e);
        }
    }

    /**
     * Lists users with pagination, filtering, and sorting
     *
     * @param pagination Pagination parameters
     * @return Paginated list of users
     */
    public PagedResponse<UserResponse> listUsers(PaginationRequest pagination) {
        try {
            // Validate sort field
            if (pagination.getSortBy() != null && !VALID_SORT_FIELDS.contains(pagination.getSortBy())) {
                throw new InvalidSortFieldException("Invalid sort field: " + pagination.getSortBy() +
                        ". Valid values: " + String.join(", ", VALID_SORT_FIELDS));
            }

            // Build the request to Cognito
            ListUsersRequest.Builder requestBuilder = ListUsersRequest.builder()
                    .userPoolId(userPoolId)
                    .limit(Math.min(60, pagination.getSize() * 3)); // Request more to allow filtering

            // Add filter if it exists
            if (pagination.getFilter() != null && !pagination.getFilter().isEmpty()) {
                requestBuilder.filter("username ^= \"" + pagination.getFilter() + "\" or email ^= \"" +
                        pagination.getFilter() + "\"");
            }

            ListUsersResponse response = cognitoClient.listUsers(requestBuilder.build());

            // Map and filter results
            List<UserResponse> users = response.users().stream()
                    .map(this::mapToUserResponse)
                    .collect(Collectors.toList());

            // Sort results according to the specified criteria
            if (pagination.getSortBy() != null) {
                sortUsers(users, pagination.getSortBy(), pagination.getSortDirection());
            }

            // Apply pagination
            int totalElements = users.size();
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
            List<UserResponse> pagedContent = users.subList(fromIndex, toIndex);

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
            throw new CognitoException("Error listing users: " + e.getMessage(), e);
        }
    }


    public UserResponse updateUser(String username, UserRequest userRequest) {
        try {
            // Verify that the user exists
            getUserByUsername(username);

            List<AttributeType> userAttributes = new ArrayList<>();

            if (userRequest.getEmail() != null) {
                userAttributes.add(AttributeType.builder().name("email").value(userRequest.getEmail()).build());
            }

            if (userRequest.getEmailVerified() != null) {
                userAttributes.add(AttributeType.builder().name("email_verified").value(userRequest.getEmailVerified().toString()).build());
            }

            if (userRequest.getPhoneNumber() != null) {
                userAttributes.add(AttributeType.builder().name("phone_number").value(userRequest.getPhoneNumber()).build());
            }

            if (userRequest.getPhoneNumberVerified() != null) {
                userAttributes.add(AttributeType.builder().name("phone_number_verified").value(userRequest.getPhoneNumberVerified().toString()).build());
            }

            if (userRequest.getAttributes() != null) {
                userRequest.getAttributes().forEach((name, value) ->
                        userAttributes.add(AttributeType.builder().name(name).value(value).build())
                );
            }

            if (!userAttributes.isEmpty()) {
                AdminUpdateUserAttributesRequest attributesRequest = AdminUpdateUserAttributesRequest.builder()
                        .userPoolId(userPoolId)
                        .username(username)
                        .userAttributes(userAttributes)
                        .build();

                cognitoClient.adminUpdateUserAttributes(attributesRequest);
            }

            return getUserByUsername(username);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CognitoException("Error updating user: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a user from AWS Cognito
     *
     * @param username Username of the user to delete
     */
    public void deleteUser(String username) {
        try {
            // Verify that the user exists
            getUserByUsername(username);

            AdminDeleteUserRequest request = AdminDeleteUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .build();

            cognitoClient.adminDeleteUser(request);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CognitoException("Error deleting user: " + e.getMessage(), e);
        }
    }

    /**
     * Enables a user
     *
     * @param username Username of the user to enable
     * @return Enabled user information
     */
    public UserResponse enableUser(String username) {
        try {
            // Verify that the user exists
            getUserByUsername(username);

            AdminEnableUserRequest request = AdminEnableUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .build();

            cognitoClient.adminEnableUser(request);

            return getUserByUsername(username);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CognitoException("Error enabling user: " + e.getMessage(), e);
        }
    }

    /**
     * Disables a user
     *
     * @param username Username of the user to disable
     * @return Disabled user information
     */
    public UserResponse disableUser(String username) {
        try {
            // Verify that the user exists
            getUserByUsername(username);

            AdminDisableUserRequest request = AdminDisableUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .build();

            cognitoClient.adminDisableUser(request);

            return getUserByUsername(username);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CognitoException("Error disabling user: " + e.getMessage(), e);
        }
    }

    /**
     * Resets a user's password
     *
     * @param username Username of the user
     * @return User information
     */
    public UserResponse resetPassword(String username) {
        try {
            // Verify that the user exists
            getUserByUsername(username);

            AdminResetUserPasswordRequest request = AdminResetUserPasswordRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .build();

            cognitoClient.adminResetUserPassword(request);

            return getUserByUsername(username);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CognitoException("Error resetting password: " + e.getMessage(), e);
        }
    }

    /**
     * Adds a user to a group
     *
     * @param username  Username
     * @param groupName Group name
     * @return Updated user information
     */
    public UserResponse addUserToGroup(String username, String groupName) {
        try {
            // Verify that the user exists
            getUserByUsername(username);

            AdminAddUserToGroupRequest request = AdminAddUserToGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .groupName(groupName)
                    .build();

            cognitoClient.adminAddUserToGroup(request);

            return getUserByUsername(username);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CognitoException("Error adding user to group: " + e.getMessage(), e);
        }
    }

    /**
     * Removes a user from a group
     *
     * @param username  Username
     * @param groupName Group name
     * @return Updated user information
     */
    public UserResponse removeUserFromGroup(String username, String groupName) {
        try {
            // Verify that the user exists
            getUserByUsername(username);

            AdminRemoveUserFromGroupRequest request = AdminRemoveUserFromGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .groupName(groupName)
                    .build();

            cognitoClient.adminRemoveUserFromGroup(request);

            return getUserByUsername(username);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CognitoException("Error removing user from group: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the groups of a user
     *
     * @param username Username
     * @return List of groups the user belongs to
     */
    public List<String> getUserGroups(String username) {
        try {
            AdminListGroupsForUserRequest request = AdminListGroupsForUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .build();

            AdminListGroupsForUserResponse response = cognitoClient.adminListGroupsForUser(request);

            return response.groups().stream()
                    .map(GroupType::groupName)
                    .collect(Collectors.toList());
        } catch (UserNotFoundException e) {
            throw new ResourceNotFoundException("User not found: " + username);
        } catch (Exception e) {
            throw new CognitoException("Error getting user groups: " + e.getMessage(), e);
        }
    }

    // Helper methods

    private UserResponse mapToUserResponse(UserType userType) {
        return UserResponse.builder()
                .username(userType.username())
                .userId(userType.username())
                .userStatus(userType.userStatusAsString())
                .enabled(userType.enabled())
                .userCreateDate(userType.userCreateDate())
                .userLastModifiedDate(userType.userLastModifiedDate())
                .attributes(userType.attributes().stream()
                        .collect(Collectors.toMap(AttributeType::name, AttributeType::value)))
                .email(getAttributeValue(userType.attributes(), "email"))
                .phoneNumber(getAttributeValue(userType.attributes(), "phone_number"))
                .emailVerified(Boolean.parseBoolean(getAttributeValue(userType.attributes(), "email_verified")))
                .phoneNumberVerified(Boolean.parseBoolean(getAttributeValue(userType.attributes(), "phone_number_verified")))
                .groups(getUserGroups(userType.username()))
                .build();
    }

    private String getAttributeValue(List<AttributeType> attributes, String attributeName) {
        return attributes.stream()
                .filter(attr -> attr.name().equals(attributeName))
                .map(AttributeType::value)
                .findFirst()
                .orElse(null);
    }

    private void sortUsers(List<UserResponse> users, String sortBy, String sortDirection) {
        Comparator<UserResponse> comparator = switch (sortBy) {
            case "username" -> Comparator.comparing(UserResponse::getUsername, Comparator.nullsLast(String::compareTo));
            case "email" -> Comparator.comparing(UserResponse::getEmail, Comparator.nullsLast(String::compareTo));
            case "status" -> Comparator.comparing(UserResponse::getUserStatus, Comparator.nullsLast(String::compareTo));
            case "enabled" -> Comparator.comparing(UserResponse::getEnabled, Comparator.nullsLast(Boolean::compareTo));
            case "createDate" ->
                    Comparator.comparing(UserResponse::getUserCreateDate, Comparator.nullsLast(Instant::compareTo));
            default -> Comparator.comparing(UserResponse::getUsername, Comparator.nullsLast(String::compareTo));
        };

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        users.sort(comparator);
    }
} 