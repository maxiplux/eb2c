package app.quantun.eb2c.service;

import app.quantun.eb2c.model.contract.contract.request.GroupRequest;
import app.quantun.eb2c.model.contract.contract.request.PaginationRequest;
import app.quantun.eb2c.model.contract.contract.response.GroupResponse;
import app.quantun.eb2c.model.contract.contract.response.PagedResponse;

import java.util.List;

public interface CognitoGroupService {
    GroupResponse createGroup(GroupRequest groupRequest);

    GroupResponse getGroupByName(String groupName);

    PagedResponse<GroupResponse> listGroups(PaginationRequest pagination);

    GroupResponse updateGroup(String groupName, GroupRequest groupRequest);

    void deleteGroup(String groupName);

    List<String> getGroupUsers(String groupName);
}
