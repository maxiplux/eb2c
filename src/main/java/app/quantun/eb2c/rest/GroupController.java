package app.quantun.eb2c.rest;


import app.quantun.eb2c.model.contract.contract.request.GroupRequest;
import app.quantun.eb2c.model.contract.contract.request.PaginationRequest;
import app.quantun.eb2c.model.contract.contract.response.GroupResponse;
import app.quantun.eb2c.model.contract.contract.response.PagedResponse;
import app.quantun.eb2c.service.CognitoGroupService;
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
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "API for group management in AWS Cognito")
public class GroupController {

    private final CognitoGroupService groupService;

    @Operation(summary = "Create a new group", description = "Creates a new group in the Cognito user pool")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Group successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupResponse> createGroup(
            @Parameter(description = "Group data to create", required = true)
            @Valid @RequestBody GroupRequest groupRequest) {
        GroupResponse createdGroup = groupService.createGroup(groupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }

    @Operation(summary = "Get a group by name", description = "Retrieves the details of a specific group")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Group found"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/{groupName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupResponse> getGroupByName(
            @Parameter(description = "Group name", required = true)
            @PathVariable String groupName) {
        GroupResponse group = groupService.getGroupByName(groupName);
        return ResponseEntity.ok(group);
    }

    @Operation(summary = "List groups", description = "Retrieves a paginated list of groups with filtering and sorting options")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Group list successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResponse<GroupResponse>> listGroups(
            @Parameter(description = "Page number (starting from 0)")
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Page size")
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @Parameter(description = "Field to sort by (groupName, description, precedence, creationDate)")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction (asc, desc)")
            @RequestParam(required = false, defaultValue = "asc") String sortDirection,
            @Parameter(description = "Text filter (searches in groupName and description)")
            @RequestParam(required = false) String filter) {

        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, sortDirection, filter);
        PagedResponse<GroupResponse> groups = groupService.listGroups(pagination);
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Update a group", description = "Updates the attributes of an existing group")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Group successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/{groupName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupResponse> updateGroup(
            @Parameter(description = "Group name", required = true)
            @PathVariable String groupName,
            @Parameter(description = "Updated group data", required = true)
            @Valid @RequestBody GroupRequest groupRequest) {
        GroupResponse updatedGroup = groupService.updateGroup(groupName, groupRequest);
        return ResponseEntity.ok(updatedGroup);
    }

    @Operation(summary = "Delete a group", description = "Deletes a group from the Cognito user pool")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Group successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{groupName}")
    public ResponseEntity<Void> deleteGroup(
            @Parameter(description = "Group name", required = true)
            @PathVariable String groupName) {
        groupService.deleteGroup(groupName);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get users in a group", description = "Gets the list of users belonging to a group")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User list successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/{groupName}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getGroupUsers(
            @Parameter(description = "Group name", required = true)
            @PathVariable String groupName) {
        List<String> users = groupService.getGroupUsers(groupName);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Add user to a group", description = "Adds a user to a specific group")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully added to the group"),
            @ApiResponse(responseCode = "404", description = "User or group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/{groupName}/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupResponse> addUserToGroup(
            @Parameter(description = "Group name", required = true)
            @PathVariable String groupName,
            @Parameter(description = "Username", required = true)
            @PathVariable String username) {
        GroupResponse group = groupService.addUserToGroup(groupName, username);
        return ResponseEntity.ok(group);
    }

    @Operation(summary = "Remove user from a group", description = "Removes a user from a specific group")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully removed from the group"),
            @ApiResponse(responseCode = "404", description = "User or group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{groupName}/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupResponse> removeUserFromGroup(
            @Parameter(description = "Group name", required = true)
            @PathVariable String groupName,
            @Parameter(description = "Username", required = true)
            @PathVariable String username) {
        GroupResponse group = groupService.removeUserFromGroup(groupName, username);
        return ResponseEntity.ok(group);
    }
} 