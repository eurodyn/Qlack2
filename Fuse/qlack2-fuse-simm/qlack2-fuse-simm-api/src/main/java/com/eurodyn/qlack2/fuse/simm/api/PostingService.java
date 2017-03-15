/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.qlack2.fuse.simm.api;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.simm.api.dto.PostItemDTO;
import com.eurodyn.qlack2.fuse.simm.api.exception.QSIMMException;

/**
 *
 * @author European Dynamics SA
 */
public interface PostingService {

    /**
     * Creates a new home page activity
     * @param piDTO The object holding the information of the new activity to be created
     * @return The information of the newly created activity
     * @throws QSIMMException
     */
    public PostItemDTO createActivity(PostItemDTO piDTO) throws QSIMMException;


    /**
     * Returns the activities of a specific home page
     * @param homepageID The id of the home page whose activities will be returned
     * @param paging The paging parameters to use while returning the activities or null if
     * no paging is required
     * @param status The statuses of the activities which will be returned or null if
     * all activities, regardless of status, are to be returned
     * @param includeChildren If true the activity's children will be included in the object being
     * returned for each activity, otherwise the children property of all returned activities will be null
     * @return The activities which satisfy the criteria defined by the method's parameters.
     */
    public PostItemDTO[] getHomePageActivities(String homepageID, PagingParams paging,
            byte[] status, boolean includeChildren, boolean includeBinary);


    /**
     * Returns the activities of a list of homepages
     * @param homepageIDs The ids of the home pages whose activities will be returned
     * @param paging The paging parameters to use while returning the activities or null if
     * no paging is required
     * @param status The statuses of the activities which will be returned or null if
     * all activities, regardless of status, are to be returned
     * @param includeChildren If true the activity's children will be included in the object being
     * returned for each activity, otherwise the children property of all returned activities will be null
     * @return The activities which satisfy the criteria defined by the method's parameters.
     */
    public PostItemDTO[] getHomePagesActivities(String[] homepageIDs, PagingParams paging,
            byte[] status, boolean includeChildren, boolean includeBinary);


    /**
     * Returns the children of a specific activity
     * @param parentId The id of the activity whose children to return
     * @param paging The paging parameters to use while returning the activities or null if
     * no paging is required
     * @param status The statuses of the activities which will be returned or null if
     * all activities, regardless of status, are to be returned
     * @param orderAscending If true the returned activities will be ordered by date ascending,
     * otherwise they will be ordered by date descending
     * @return The activities which satisfy the criteria defined by the method's parameters.
     */
    public PostItemDTO[] getActivityChildren(String parentId, PagingParams paging, byte[] status, boolean orderAscending);


    /**
     * Returns the children of a specific activity which are of a specific type
     * @param parentId The id of the activity whose children to return
     * @param paging The paging parameters to use while returning the activities or null if
     * no paging is required
     * @param status The statuses of the activities which will be returned or null if
     * all activities, regardless of status, are to be returned
     * @param orderAscending If true the returned activities will be ordered by date ascending,
     * otherwise they will be ordered by date descending
     * @param activityCategoryID The id of the category of the activities to be returned
     * @return The activities which satisfy the criteria defined by the method's parameters.
     */
    public PostItemDTO[] getActivityChildren(String parentId, PagingParams paging,
            byte[] status, boolean orderAscending, String activityCategoryID);


    /**
     * Returns the number of children of a specific activity
     * @param parentId The id of the activity whose children to return
     * @param status The statuses of the activities which will be returned or null if
     * all activities, regardless of status, are to be returned
     * @return The activities which satisfy the criteria defined by the method's parameters.
     */
    public long getChildrenNumber(String parentId, byte[] status);


    /**
     * Returns the number of children of a specific activity which are of a specific type
     * @param parentId The id of the activity whose children to return
     * @param status The statuses of the activities which will be returned or null if
     * all activities, regardless of status, are to be returned
     * @param activityCategoryID The id of the category of the activities to be returned
     * @return The activities which satisfy the criteria defined by the method's parameters.
     */
    public long getChildrenNumber(String parentId, byte[] status, String activityCategoryID);


    /**
     * Returns the last activity of a specific type posted on a homepage
     * @param homepageID The id of the homepage whose last activity to return
     * @param activityCategoryID The id of the category of the activity to be returned
     * @param status The statuses of the activities to take into account
     * @return The last activity of the defined homepage which satisfies the criteria
     * defined by the method's parameters.
     */
    PostItemDTO getLastActivityOfType(String homepageID, String activityCategoryID, byte[] status);


    /**
     * Sets an activity's status to approved (HOME_PAGE_ACTIVITY_STATUS_APPROVED = 1)
     * @param homePageActivityID The id of the activity to approve
     */
    public void approveActivity(String homePageActivityID);


    /**
     * Updates an activity
     * @param activity An object holding the id of the activity to update as well as the new
     * (updated) information of this activity.
     * @throws QSIMMException
     */
    public void updateActivity(PostItemDTO activity) throws QSIMMException;


    /**
     * Deletes a homepage activity
     * @param activity The activity to delete. This method takes into account the id
     * of the activity and the srcUserId
     */
    public void deleteActivity(PostItemDTO activity) throws QSIMMException;


    /**
     * Retrieves a specific homepage activity
     * @param activityId The id of the activity to retrieve
     * @return
     */
    public PostItemDTO getActivity(String activityId, boolean includeChildren,
        boolean includeBinary);
}
