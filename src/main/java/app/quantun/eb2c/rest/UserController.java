package app.quantun.eb2c.rest;


import app.quantun.eb2c.model.contract.contract.request.PaginationRequest;
import app.quantun.eb2c.model.contract.contract.request.UserRequest;
import app.quantun.eb2c.model.contract.contract.response.PagedResponse;
import app.quantun.eb2c.model.contract.contract.response.UserResponse;
import app.quantun.eb2c.service.CognitoUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "API for user management in AWS Cognito")
public class UserController {

    private final CognitoUserService userService;

    @Operation(summary = "Create a new user", description = "Creates a new user in the Cognito user pool")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> createUser(
            @Parameter(description = "User data to create", required = true)
            @Valid @RequestBody UserRequest userRequest) {
        UserResponse createdUser = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(summary = "Get a user by username", description = "Retrieves the details of a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> getUserByUsername(
            @Parameter(description = "Username", required = true)
            @PathVariable String username) {
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "List users", description = "Retrieves a paginated list of users with filtering and sorting options")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User list successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResponse<UserResponse>> listUsers(
            @Parameter(description = "Page number (starting from 0)")
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Page size")
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @Parameter(description = "Field to sort by (username, email, status, enabled, createDate)")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction (asc, desc)")
            @RequestParam(required = false, defaultValue = "asc") String sortDirection,
            @Parameter(description = "Text filter (searches in username and email)")
            @RequestParam(required = false) String filter) {

        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, sortDirection, filter);
        PagedResponse<UserResponse> users = userService.listUsers(pagination);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Update a user", description = "Updates the attributes of an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "Username", required = true)
            @PathVariable String username,
            @Parameter(description = "Updated user data", required = true)
            @Valid @RequestBody UserRequest userRequest) {
        UserResponse updatedUser = userService.updateUser(username, userRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete a user", description = "Deletes a user from the Cognito user pool")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "Username", required = true)
            @PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Enable a user", description = "Enables an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully enabled"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/{username}/enable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> enableUser(
            @Parameter(description = "Username", required = true)
            @PathVariable String username) {
        UserResponse enabledUser = userService.enableUser(username);
        return ResponseEntity.ok(enabledUser);
    }

    @Operation(summary = "Disable a user", description = "Disables an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully disabled"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/{username}/disable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> disableUser(
            @Parameter(description = "Username", required = true)
            @PathVariable String username) {
        UserResponse disabledUser = userService.disableUser(username);
        return ResponseEntity.ok(disabledUser);
    }

    @Operation(summary = "Reset password", description = "Resets a user's password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password successfully reset"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/{username}/reset-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> resetPassword(
            @Parameter(description = "Username", required = true)
            @PathVariable String username) {
        UserResponse user = userService.resetPassword(username);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get user groups", description = "Gets the list of groups a user belongs to")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Group list successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/{username}/groups", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getUserGroups(
            @Parameter(description = "Username", required = true)
            @PathVariable String username) {
        List<String> groups = userService.getUserGroups(username);
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Add user to a group", description = "Adds a user to a specific group")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully added to the group"),
            @ApiResponse(responseCode = "404", description = "User or group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/{username}/groups/{groupName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> addUserToGroup(
            @Parameter(description = "Username", required = true)
            @PathVariable String username,
            @Parameter(description = "Group name", required = true)
            @PathVariable String groupName) {
        UserResponse user = userService.addUserToGroup(username, groupName);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Remove user from a group", description = "Removes a user from a specific group")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully removed from the group"),
            @ApiResponse(responseCode = "404", description = "User or group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{username}/groups/{groupName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> removeUserFromGroup(
            @Parameter(description = "Username", required = true)
            @PathVariable String username,
            @Parameter(description = "Group name", required = true)
            @PathVariable String groupName) {
        UserResponse user = userService.removeUserFromGroup(username, groupName);
        return ResponseEntity.ok(user);
    }
} 