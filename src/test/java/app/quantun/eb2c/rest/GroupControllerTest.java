package app.quantun.eb2c.rest;

import app.quantun.eb2c.Eb2cApplication;
import app.quantun.eb2c.TestConfig;
import app.quantun.eb2c.model.contract.contract.request.GroupRequest;
import app.quantun.eb2c.model.contract.contract.request.PaginationRequest;
import app.quantun.eb2c.model.contract.contract.response.GroupResponse;
import app.quantun.eb2c.model.contract.contract.response.PagedResponse;
import app.quantun.eb2c.service.CognitoGroupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
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
public class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CognitoGroupService groupService;

    @Autowired
    private ObjectMapper objectMapper;

    private GroupRequest groupRequest;
    private GroupResponse groupResponse;

    @BeforeEach
    void setUp() {
        // Initialize test data
        groupRequest = new GroupRequest();
        groupRequest.setGroupName("TestGroup");
        groupRequest.setDescription("A test group");
        groupRequest.setPrecedence(10);

        groupResponse = new GroupResponse();
        groupResponse.setGroupName("TestGroup");
        groupResponse.setDescription("A test group");
        groupResponse.setPrecedence(10);
        groupResponse.setCreationDate(Instant.now());
        groupResponse.setLastModifiedDate(Instant.now());
        groupResponse.setUsers(Arrays.asList("user1", "user2"));
    }

    @Test
    void createGroupTest() throws Exception {
        when(groupService.createGroup(any(GroupRequest.class))).thenReturn(groupResponse);

        mockMvc.perform(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.groupName").value("TestGroup"))
                .andExpect(jsonPath("$.description").value("A test group"));
    }

    @Test
    void getGroupByNameTest() throws Exception {
        when(groupService.getGroupByName("TestGroup")).thenReturn(groupResponse);

        mockMvc.perform(get("/api/groups/TestGroup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupName").value("TestGroup"))
                .andExpect(jsonPath("$.description").value("A test group"));
    }

    @Test
    void listGroupsTest() throws Exception {
        PagedResponse<GroupResponse> pagedResponse = new PagedResponse<>(
                Collections.singletonList(groupResponse),
                0, 20, 1, 1, true, "groupName", "asc", null);

        when(groupService.listGroups(any(PaginationRequest.class))).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].groupName").value("TestGroup"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void updateGroupTest() throws Exception {
        when(groupService.updateGroup(eq("TestGroup"), any(GroupRequest.class))).thenReturn(groupResponse);

        mockMvc.perform(put("/api/groups/TestGroup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupName").value("TestGroup"));
    }

    @Test
    void deleteGroupTest() throws Exception {
        doNothing().when(groupService).deleteGroup("TestGroup");

        mockMvc.perform(delete("/api/groups/TestGroup"))
                .andExpect(status().isNoContent());
    }

    @Test
    void listGroupUsersTest() throws Exception {
        List<String> usernames = Arrays.asList("user1", "user2", "user3");
        when(groupService.getGroupUsers("TestGroup")).thenReturn(usernames);

        mockMvc.perform(get("/api/groups/TestGroup/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("user1"))
                .andExpect(jsonPath("$[1]").value("user2"))
                .andExpect(jsonPath("$[2]").value("user3"));
    }
}