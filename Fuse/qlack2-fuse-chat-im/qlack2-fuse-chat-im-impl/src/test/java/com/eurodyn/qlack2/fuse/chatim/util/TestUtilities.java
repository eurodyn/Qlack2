package com.eurodyn.qlack2.fuse.chatim.util;

import com.eurodyn.qlack2.fuse.chatim.api.dto.ActionOnUserDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomPropertyDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomWordFilterDTO;

import java.util.Date;
import java.util.UUID;

public class TestUtilities {

    public static RoomDTO createRoomDTO(){
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(UUID.randomUUID().toString());
        roomDTO.setCreatedOn(new Date().getTime());
        roomDTO.setTargetCommunityID(UUID.randomUUID().toString());
        roomDTO.setCreatedByUserID(UUID.randomUUID().toString());
        roomDTO.setSrcUserId(UUID.randomUUID().toString());
        roomDTO.setTitle(TestConst.generateRandomString());

        return roomDTO;
    }

    public static ActionOnUserDTO createActionOnUserDTO(){
        ActionOnUserDTO actionOnUserDTO = new ActionOnUserDTO();
        actionOnUserDTO.setActionDescription(TestConst.generateRandomString());
        actionOnUserDTO.setId(UUID.randomUUID().toString());
        actionOnUserDTO.setActionName(TestConst.generateRandomString());
        actionOnUserDTO.setActionId(UUID.randomUUID().toString());
        actionOnUserDTO.setActionPeriod(new Date().getTime());
        actionOnUserDTO.setReason(TestConst.generateRandomString());
        actionOnUserDTO.setCreatedOn(new Date().getTime());
        actionOnUserDTO.setSrcUserId(UUID.randomUUID().toString());

        return actionOnUserDTO;
    }

    public static RoomPropertyDTO createRoomPropertyDTO(){
      RoomPropertyDTO roomPropertyDTO = new RoomPropertyDTO();
      roomPropertyDTO.setId(UUID.randomUUID().toString());
      roomPropertyDTO.setSrcUserId(UUID.randomUUID().toString());
      roomPropertyDTO.setPropertyName(TestConst.generateRandomString());
      roomPropertyDTO.setPropertyValue(TestConst.generateRandomString());

      return roomPropertyDTO;
    }

    public static RoomWordFilterDTO createRoomWordFilterDTO(){
      RoomWordFilterDTO roomWordFilterDTO = new RoomWordFilterDTO();
      roomWordFilterDTO.setId(UUID.randomUUID().toString());
      roomWordFilterDTO.setSrcUserId(UUID.randomUUID().toString());
      roomWordFilterDTO.setFilter(TestConst.generateRandomString());

      return roomWordFilterDTO;
    }

}
