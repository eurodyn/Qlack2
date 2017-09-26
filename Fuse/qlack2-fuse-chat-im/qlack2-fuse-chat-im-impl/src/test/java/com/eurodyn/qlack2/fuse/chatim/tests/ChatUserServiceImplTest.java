package com.eurodyn.qlack2.fuse.chatim.tests;

import com.eurodyn.qlack2.fuse.chatim.api.ChatUserService;
import com.eurodyn.qlack2.fuse.chatim.api.RoomService;
import com.eurodyn.qlack2.fuse.chatim.api.dto.ActionOnUserDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomDTO;
import com.eurodyn.qlack2.fuse.chatim.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.chatim.util.TestUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class ChatUserServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    ChatUserService chatUserService;

    @Inject
    @Filter(timeout = 1200000)
    RoomService roomService;

    @Test
    public void performAction(){
        RoomDTO roomDTO = TestUtilities.createRoomDTO();
        String roomID = roomService.createRoom(roomDTO);
        Assert.assertNotNull(roomID);

        ActionOnUserDTO actionOnUserDTO = TestUtilities.createActionOnUserDTO();
        Assert.assertNotNull(chatUserService.performAction(roomID,roomDTO.getSrcUserId(),actionOnUserDTO.getActionId(),actionOnUserDTO.getReason(),actionOnUserDTO.getActionPeriod(),actionOnUserDTO.getActionName()));
    }

}
