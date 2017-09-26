package com.eurodyn.qlack2.fuse.chatim.tests;

import com.eurodyn.qlack2.fuse.chatim.api.RoomService;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomPropertyDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomWordFilterDTO;
import com.eurodyn.qlack2.fuse.chatim.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.chatim.util.TestUtilities;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

import java.util.UUID;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class RoomServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    RoomService roomService;

    @Test
    public void createRoom(){
        RoomDTO roomDTO = TestUtilities.createRoomDTO();
        String roomID = roomService.createRoom(roomDTO);
        Assert.assertNotNull(roomID);
    }

    @Test(expected=NoResultException.class)
    public void removeRoom(){
        RoomDTO roomDTO = TestUtilities.createRoomDTO();
        roomDTO.setId(UUID.randomUUID().toString());
        String roomID = roomService.createRoom(roomDTO);
        Assert.assertNotNull(roomID);

        RoomWordFilterDTO roomWordFilterDTO = TestUtilities.createRoomWordFilterDTO();
        roomWordFilterDTO.setRoomId(roomID);
        roomService.setRoomFilter(roomWordFilterDTO);

        roomService.removeRoom(roomID);

        //expect no NoResultException, roomID doesnt exist
        Assert.assertNotNull(roomService.getRoomFilter(roomID,roomDTO.getSrcUserId()));
    }

    @Test
    public void joinRoom(){
        RoomDTO roomDTO = TestUtilities.createRoomDTO();
        String roomID = roomService.createRoom(roomDTO);
        Assert.assertNotNull(roomID);

        roomService.joinRoom(roomID,roomDTO.getSrcUserId());
        Assert.assertNotNull(roomService.getRoomUsers(roomID));
    }

    @Test
    public void leaveRoom(){
        RoomDTO roomDTO = TestUtilities.createRoomDTO();
        String roomID = roomService.createRoom(roomDTO);
        Assert.assertNotNull(roomID);

        roomService.joinRoom(roomID,roomDTO.getSrcUserId());
        roomService.leaveRoom(roomDTO.getSrcUserId(),roomID);
        Assert.assertNotNull(roomService.getRoomUsers(roomID));
    }

    @Test
    public void listAvailableRoomsForGroups(){
        RoomDTO roomDTO = TestUtilities.createRoomDTO();
        String roomID = roomService.createRoom(roomDTO);
        Assert.assertNotNull(roomID);

        String[] ids = {roomDTO.getTargetCommunityID()};
        Assert.assertNotNull(roomService.listAvailableRoomsForGroups(ids));
    }

    @Test
    public void setRoomProperty(){
        RoomDTO roomDTO = TestUtilities.createRoomDTO();
        String roomID = roomService.createRoom(roomDTO);
        Assert.assertNotNull(roomID);

        RoomPropertyDTO roomPropertyDTO = TestUtilities.createRoomPropertyDTO();
        roomPropertyDTO.setRoomId(roomID);
        Assert.assertNotNull(roomPropertyDTO);

        roomService.setRoomProperty(roomPropertyDTO);
        Assert.assertNotNull(roomService.getRoomProperty(roomID,roomPropertyDTO.getPropertyName(),roomPropertyDTO.getSrcUserId()));
    }

    @Test
    public void getRoomProperty(){
        RoomDTO roomDTO = TestUtilities.createRoomDTO();
        String roomID = roomService.createRoom(roomDTO);
        Assert.assertNotNull(roomID);

        RoomPropertyDTO roomPropertyDTO = TestUtilities.createRoomPropertyDTO();
        roomPropertyDTO.setRoomId(roomID);
        Assert.assertNotNull(roomPropertyDTO);

        roomService.setRoomProperty(roomPropertyDTO);
        Assert.assertNotNull(roomService.getRoomProperty(roomID,roomPropertyDTO.getPropertyName(),roomPropertyDTO.getSrcUserId()));
    }

    @Test
    public void setRoomFilter(){
        RoomDTO roomDTO = TestUtilities.createRoomDTO();
        String roomID = roomService.createRoom(roomDTO);
        Assert.assertNotNull(roomID);

        RoomWordFilterDTO roomWordFilterDTO = TestUtilities.createRoomWordFilterDTO();
        roomWordFilterDTO.setRoomId(roomID);
        roomService.setRoomFilter(roomWordFilterDTO);

        Assert.assertNotNull(roomService.getRoomFilter(roomID,roomDTO.getSrcUserId()));
    }

    @Test
    public void getRoomFilter(){
        RoomDTO roomDTO = TestUtilities.createRoomDTO();
        String roomID = roomService.createRoom(roomDTO);
        Assert.assertNotNull(roomID);

        RoomWordFilterDTO roomWordFilterDTO = TestUtilities.createRoomWordFilterDTO();
        roomWordFilterDTO.setRoomId(roomID);
        roomService.setRoomFilter(roomWordFilterDTO);

        Assert.assertNotNull(roomService.getRoomFilter(roomID,roomDTO.getSrcUserId()));
    }

    @Test(expected=NoResultException.class)
    public void removeRoomFilter(){
        RoomDTO roomDTO = TestUtilities.createRoomDTO();
        String roomID = roomService.createRoom(roomDTO);
        Assert.assertNotNull(roomID);

        RoomWordFilterDTO roomWordFilterDTO = TestUtilities.createRoomWordFilterDTO();
        roomWordFilterDTO.setRoomId(roomID);
        roomService.setRoomFilter(roomWordFilterDTO);

        roomService.removeRoomFilter(roomID);
        //expect NoResult exception, after removing of Filter from room
        Assert.assertNull(roomService.getRoomFilter(roomID,roomDTO.getSrcUserId()));
    }

}
