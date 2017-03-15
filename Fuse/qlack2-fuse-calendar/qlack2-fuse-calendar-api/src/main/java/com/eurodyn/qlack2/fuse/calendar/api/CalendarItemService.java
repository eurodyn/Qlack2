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
package com.eurodyn.qlack2.fuse.calendar.api;

import com.eurodyn.qlack2.fuse.calendar.api.dto.CalendarItemDTO;
import com.eurodyn.qlack2.fuse.calendar.api.dto.ParticipantDTO;
import com.eurodyn.qlack2.fuse.calendar.api.dto.SupportingObjectDTO;
import com.eurodyn.qlack2.fuse.calendar.api.exception.QCalendarException;

import java.util.Date;
import java.util.List;

/**
 *
 * @author European Dynamics SA
 */
public interface CalendarItemService {

    /**
     * Creates a new calendar item. It is the caller's responsibility to have set the
     * createdBy/On and lastModifiedBy/On properties. This method also posts a JMS message (subject to the
     * value of the realtime.JMS.notifications application property) of type MSGTYPE_ITEM_CREATED
     * to each one of the item participants and to the item creator (the participants are distinct).
     * Each message has the ITEM_ID, ITEM_NAME and ITEM_CATEGORY_ID properties set to the respective values of the new item
     * and uses the srcUserId property of the item as source user.
     * @param item The data of the item to create.
     * @throws QCalendarException If an error occurs while posting the JMS message
     * @return The newly created item.
     */
    CalendarItemDTO createItem(CalendarItemDTO item) throws QCalendarException;


    /**
     * Modifies an existing calendar item. It is the caller's responsibility to have set the
     * lastModifiedBy/On properties. This method also posts a JMS message (subject to the
     * value of the realtime.JMS.notifications application property) of type MSGTYPE_ITEM_UPDATED
     * to each one of the item participants and to the item creator (the participants are distinct).
     * Each message has the ITEM_ID, ITEM_NAME and ITEM_CATEGORY_ID properties set to the respective values of the item
     * and uses the srcUserId property of the item as source user.
     * @param item The data of the item to update. This method takes into account
     * the following properties of the CalendarItemDTO class if they are not null:
     * - id: The id of the item to update. This property should always be not null.
     * - categoryId
     * - name
     * - description
     * - location
     * - contactId
     * - startTime
     * - endTime
     * - allDay
     * - lastModifiedBy
     * - lastModifiedOn
     * Any CalendarItemDTO properties which are not set when calling this method will not
     * be modified.
     * @throws QCalendarException If a calendar item with the specified id does not exist
     */
    void updateItem(CalendarItemDTO item) throws QCalendarException;


    /**
     * Deletes a calendar item. This method also posts a JMS message (subject to the
     * value of the realtime.JMS.notifications application property) of type MSGTYPE_ITEM_DELETED
     * to each one of the item participants and to the item creator (the participants are distinct).
     * Each message has the ITEM_ID, ITEM_NAME and ITEM_CATEGORY_ID properties set to the respective values of the item
     * and uses the srcUserId property of the item as source user.
     * @param item The item to delete. This method takes into account the item it and the
     * srcUserId (for posting a JMS message).
     * @throws QCalendarException If a calendar item with the specified id does not exist
     */
    void deleteItem(CalendarItemDTO item) throws QCalendarException;


    /**
     * Retrieves a specific calendar item
     * @param itemId The id of the item to retrieve
     * @return The information of the retrieved item.
     */
    CalendarItemDTO getItem(String itemId);


    /**
     * Retrieves the items of a calendar
     * @param calendarId The id of the calendar whose items to retrieve
     * @param categoryIds The categories of the items which will be retrieved. If
     * this parameter is null then the items of the specified calendar
     * will be retrieved regardless of category.
     * @param startDate The minimum start date of the retrieved items. All items retrieved will
     * have a startTime no less than this startDate.
     * @param endDate The maximum start date of the retrieved items. All items retrieved will
     * have an startTime no more than this endDate.
     * @return A list with the information of the retrieved items.
     */
    List<CalendarItemDTO> getCalendarItems(String calendarId,
            String[] categoryIds, Date startDate, Date endDate);


    /**
     * Retrieves the calendar items having a specific user as participant or owner.
     * @param userId The id of the user participating in or owning the items which
     * will be retrieved.
     * @param categoryIds The categories of the items which will be retrieved. If
     * this parameter is null then calendar items will be retrieved regardless of category.
     * @param startDate The minimum start date of the retrieved items. All items retrieved will
     * have a startTime no less than this startDate.
     * @param endDate The maximum start date of the retrieved items. All items retrieved will
     * have an startTime no more than this endDate.
     * @return A list with the information of the retrieved items.
     */
    List<CalendarItemDTO> getItemsForUser(String userId,
            String[] categoryIds, Date startDate, Date endDate);


    /**
     * Retrieves the calendar items having a specific user as participant or owner and
     * being part of a specific calendar.
     * @param userId The id of the user participating in or owning the items which
     * will be retrieved.
     * @param calendarId The id of the calendar whose items to retrieve
     * @param categoryIds The categories of the items which will be retrieved. If
     * this parameter is null then calendar items will be retrieved regardless of category.
     * @param startDate The minimum start date of the retrieved items. All items retrieved will
     * have a startTime no less than this startDate.
     * @param endDate The maximum start date of the retrieved items. All items retrieved will
     * have an startTime no more than this endDate.
     * @return A list with the information of the retrieved items.
     */
    List<CalendarItemDTO> getItemsForUser(String userId,
            String calendarId, String[] categoryIds, Date startDate, Date endDate);


    /**
     * Adds a supporting object to a calendar item and updates
     * the lastModifiedBy and lastModifiedOn properties of the relative
     * calendar item using the srcUserId of the object passed to it subject to the
     * value of the updateItem parameter. This method also posts a JMS message of type
     * OBJECT_CREATED to all the item's (distinct) participants (subject to the
     * value of the realtime.JMS.notifications application property). Additionally, in case the
     * relative calendar item is updated, a JMS message of type MSGTYPE_ITEM_UPDATED is also posted.
     * @param object The new object to create
     * @param updateItem If true then the lastModifiedBy and lastModifiedOn properties of the relative item will be updated
     * @throws QCalendarException If the calendar item referenced by the specified object does not exist
     * @return The newly created object
     */
    SupportingObjectDTO addItemSupportingObject(SupportingObjectDTO object, boolean updateItem)
            throws QCalendarException;


    /**
     * Updates a supporting object of a calendar item. This method also updates the lastModifiedBy
     * and lastModifiedOn properties of the relative calendar item using the srcUserId of the object passed to it
     * subject to the value of the updateItem parameter. In case the
     * relative calendar item is updated, a JMS message of type MSGTYPE_ITEM_UPDATED is posted.
     * It is however the caller's responsibility to have correctly set the lastModifiedBy/On properties of the object.
     * @param object The data of the supporting object to update. This method takes into account
     * the following properties of the SupportingObjectDTO class:
     * - id: The id of the object to update.
     * - objectId
     * - filename
     * - mimetype
     * - objectData
     * - link
     * - lastModifiedBy: The id of the user performing this update. This property should always be not null.
     * Please note that all these properties are updated by this method, even if the passed values are null.
     * @param updateItem If true then the lastModifiedBy and lastModifiedOn properties of the relative item will be updated
     * @throws QCalendarException If the specified object does not exist
     */
    void updateItemSupportingObject(SupportingObjectDTO object, boolean updateItem)
            throws QCalendarException;


    /**
     * Removes a supporting object from a calendar item. This method also updates
     * the lastModifiedBy and lastModifiedOn properties of the relative calendar item using the srcUserId of the object passed to it
     * subject to the value of the updateItem parameter. In case the
     * relative calendar item is updated, a JMS message of type MSGTYPE_ITEM_UPDATED is posted.
     * @param object The supporting object to remove
     * @param updateItem If true then the lastModifiedBy and lastModifiedOn properties of the relative item will be updated
     * @throws QCalendarException If the specified object does not exist
     */
    void removeItemSupportingObject(SupportingObjectDTO object, boolean updateItem)
            throws QCalendarException;


    /**
     * Retrieves a supporting object of a calendar item.
     * @param objectId The id of the supporting object to retrieve
     * @return The requested supporting object
     * @throws QCalendarException If a supporting object with the specified id
     * does not exist.
     */
    SupportingObjectDTO getItemSupportingObject(String objectId)
            throws QCalendarException;


    /**
     * Retrieves the supporting objects of a calendar item.
     * @param itemId The id of the item whose objects to retrieve
     * @param categoryIds The ids of the categories of the supporting objects to retrieve.
     * If this parameter is null then all the supporting objects of the specified item
     * are retrieved.
     * @return A list with the requested supporting objects
     * @throws QCalendarException If an item with the specified id
     * does not exist.
     */
    List<SupportingObjectDTO> getItemSupportingObjects(String itemId, String[] categoryIds)
            throws QCalendarException;


    /**
     * Adds a participants to a calendar item. This method also updates the lastModifiedBy
     * and lastModifiedOn properties of the relative calendar item using the srcUserId of the object passed to it
     * subject to the value of the updateItem parameter. This method also posts a JMS message (subject to the
     * value of the realtime.JMS.notifications application property) of type MSGTYPE_PARTICIPANT_ADDED.
     * The message has the ITEM_ID, ITEM_NAME and ITEM_CATEGORY_ID properties set to the respective values
     * of the calendar item and has its PRIVATE_USERID set to the id of the participant.
     * @param participant The new participant to add to the calendar item
     * @param updateItem If true then the lastModifiedBy and lastModifiedOn properties of the relative item will be updated
     * @throws QCalendarException If the calendar item referenced by the specified participant does not exist
     * or if an error occurs while posting the JMS message.
     * @return The newly added participants.
     */
    ParticipantDTO addItemParticipant(ParticipantDTO participant, boolean updateItem)
            throws QCalendarException;


    /**
     * Updates a participant of a calendar item. This method also updates the lastModifiedBy
     * and lastModifiedOn properties of the relative calendar item using the srcUserId of the object passed to it
     * subject to the value of the updateItem parameter.
     * @param participant The data of the participant to update. This method takes into account
     * the following properties of the ParticipantDTO class if they are not null:
     * - id: The id of the participant to update. This property should always be not null.
     * - participantId
     * @param updateItem If true then the lastModifiedBy and lastModifiedOn properties of the relative item will be updated
     * @throws QCalendarException If the specified participant does not exist
     */
    void updateItemParticipant(ParticipantDTO participant, boolean updateItem)
            throws QCalendarException;


    /**
     * Removes a participants from a calendar item. This method also updates the lastModifiedBy
     * and lastModifiedOn properties of the relative calendar item using the srcUserId of the object passed to it
     * subject to the value of the updateItem parameter.
     * @param participant The participant to remove
     * @param updateItem If true then the lastModifiedBy and lastModifiedOn properties of the relative item will be updated
     * @throws QCalendarException If the specified participant does not exist
     */
    void removeItemParticipant(ParticipantDTO participant, boolean updateItem)
            throws QCalendarException;


    /**
     * Retrieves a participant of a calendar item.
     * @param participantId The id of the participant to retrieve
     * @return The requested participant
     * @throws QCalendarException If a participant with the specified id
     * does not exist.
     */
    ParticipantDTO getItemParticipant(String participantId)
            throws QCalendarException;


    /**
     * Retrieves the participants of a calendar item.
     * @param itemId The id of the item whose participants to retrieve
     * @return A list with the requested participants
     * @throws QCalendarException If an item with the specified id
     * does not exist.
     */
    List<ParticipantDTO> getItemParticipants(String itemId)
            throws QCalendarException;


    /**
     * Retrieves the participants with a specific user id
     * @param userId The user id of the participants to retrieve
     * @return A list of the retrieved participants
     */
    List<ParticipantDTO> getParticipantsForUser(String userId);


    /**
     * Retrieves the participants with a specific user id for a specific item
     * @param userId The user id of the participants to retrieve
     * @param itemId The id of the item whose participants to retrieve
     * @return A list of the retrieved participants
     */
    List<ParticipantDTO> getParticipantsForUser(String userId, String itemId);


    /**
     * Retrieves the status of a calendar item participant
     * @param participantId The id of the participant whose status will be retrieved
     * @return The status of the specified participant
     * @throws QCalendarException If the specified participant does not exist
     */
    short getParticipantStatus(String participantId) throws QCalendarException;


    /**
     * Updates the status of a calendar item participant. This method also posts a JMS message (subject to the
     * value of the realtime.JMS.notifications application property) of type MSGTYPE_PARTICIPANT_STATUS_MODIFIED.
     * The message has the ITEM_ID, ITEM_NAME and ITEM_CATEGORY_ID properties set to the respective values of the item,
     * the PARTICIPANT_ID and PARTICIPANT_STATUS properties set to the values passed to this method
     * and has its PRIVATE_USERID set to the id of the creator of the item this participant belongs to.
     * @param participantId The id of the participant whose status will be updated
     * @param status The new status of the participant.
     * @return True of the participant's status was successfully modified, false otherwise
     * (if the participant's status was already set to the value passed to this method).
     * @throws QCalendarException If the specified participant does not exist or
     * if an error occurs while posting the JMS message.
     */
    boolean updateParticipantStatus(String participantId, short status)
            throws QCalendarException;
}
