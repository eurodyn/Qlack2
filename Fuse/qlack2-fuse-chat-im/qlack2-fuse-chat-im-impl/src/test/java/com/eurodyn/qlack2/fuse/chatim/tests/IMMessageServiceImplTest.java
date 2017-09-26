package com.eurodyn.qlack2.fuse.chatim.tests;

import com.eurodyn.qlack2.fuse.chatim.api.IMMessageService;
import com.eurodyn.qlack2.fuse.chatim.api.RoomService;
import com.eurodyn.qlack2.fuse.chatim.api.dto.MessageDTO;
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
import java.util.*;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class IMMessageServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    IMMessageService imMessageService;

    @Inject
    @Filter(timeout = 1200000)
    RoomService roomService;

    @Test
    public void sendMessage(){
        RoomDTO roomDTO = TestUtilities.createRoomDTO();
        String roomID = roomService.createRoom(roomDTO);
        Assert.assertNotNull(roomID);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(UUID.randomUUID().toString());
        messageDTO.setSrcUserId(UUID.randomUUID().toString());
        messageDTO.setDate(new Date().getTime());
        messageDTO.setFromID(UUID.randomUUID().toString());
        messageDTO.setMessage("test");
        messageDTO.setRoomID(roomID);
        messageDTO.setToID(UUID.randomUUID().toString());
        messageDTO.setAttribute("1","test");

        Assert.assertNotNull(imMessageService.sendMessage(messageDTO));
    }

}
