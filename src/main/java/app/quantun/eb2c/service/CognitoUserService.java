package app.quantun.eb2c.service;

import app.quantun.eb2c.model.contract.contract.request.PaginationRequest;
import app.quantun.eb2c.model.contract.contract.request.UserRequest;
import app.quantun.eb2c.model.contract.contract.response.PagedResponse;
import app.quantun.eb2c.model.contract.contract.response.UserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

import java.util.List;

public interface CognitoUserService {
    UserResponse createUser(UserRequest userRequest);

    UserResponse getUserByUsername(String username);

    PagedResponse<UserResponse> listUsers(PaginationRequest pagination);

    UserResponse updateUser(String username, UserRequest userRequest);

    void deleteUser(String username);

    UserResponse enableUser(String username);

    UserResponse disableUser(String username);

    UserResponse resetPassword(String username);

    UserResponse addUserToGroup(String username, String groupName);

    UserResponse removeUserFromGroup(String username, String groupName);

    List<String> getUserGroups(String username);

    UserResponse mapToUserResponse(UserType userType);

    String getAttributeValue(List<AttributeType> attributes, String attributeName);

    void sortUsers(List<UserResponse> users, String sortBy, String sortDirection);
}
