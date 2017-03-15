package com.eurodyn.qlack2.fuse.workflow.runtime.api;

import org.kie.api.task.UserGroupCallback;
import java.util.List;

public interface CustomUserGroupCallback extends UserGroupCallback  {
	
	/**
	* Resolves existence of user id.
	* @param userId the user id assigned to the task
	* @return true if userId exists, false otherwise.
	*/
    boolean existsUser(String userId);
    
    /**
	* Resolves existence of group id.
	* @param groupId the group id assigned to the task
	* @return true if groupId exists, false otherwise.
	*/
    boolean existsGroup(String groupId);
    
    /**
	* Returns list of group ids for specified user id.
	* @param userId the user id assigned to the task
	* @param groupIds list of group ids assigned to the task
	* @param allExistingGroupIds list of all currently known group ids
	* @return List of group ids.
	*/
    List<String> getGroupsForUser(String userId, List<String> groupIds, List<String> allExistingGroupIds);
}
