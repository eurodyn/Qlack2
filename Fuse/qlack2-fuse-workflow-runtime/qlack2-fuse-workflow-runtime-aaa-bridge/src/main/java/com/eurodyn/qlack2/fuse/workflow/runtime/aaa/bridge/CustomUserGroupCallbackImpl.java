package com.eurodyn.qlack2.fuse.workflow.runtime.aaa.bridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.workflow.runtime.api.CustomUserGroupCallback;

public class CustomUserGroupCallbackImpl implements CustomUserGroupCallback {
	private static final Logger LOGGER = Logger.getLogger(CustomUserGroupCallbackImpl.class.getName());
	private UserService userService;
	private UserGroupService groupService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void setGroupService(UserGroupService groupService) {
		this.groupService = groupService;
	}

    public boolean existsUser(String userId) {   
    	LOGGER.log(Level.INFO, "existsUser: UserId: " + userId);
        return userService.getUserByName(userId)!=null;
    }

    public boolean existsGroup(String groupId) {
    	LOGGER.log(Level.INFO, "existsGroup: GroupId: " + groupId);
        return groupService.getGroupByName(groupId, true)!=null;
    }

    public List<String> getGroupsForUser(String userId, List<String> groupIds, List<String> allExistingGroupIds) {
    	LOGGER.log(Level.INFO, "getGroupsForUser: UserId: " + userId);
    	List<String> list = null;
    	if (userService.getUserByName(userId)!=null)
    	{
    		Set<String> userGroupIds = groupService.getUserGroupsIds(userService.getUserByName(userId).getId());
    		if (!userGroupIds.isEmpty())
    		{
    			list = new ArrayList<String>();
    			for (String groupId : userGroupIds)
    				list.add(groupService.getGroupByID(groupId, true).getName());
    		}
    	}
    	return list;
    }
}

