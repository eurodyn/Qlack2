package com.eurodyn.qlack2.fuse.chatim.it;

import com.eurodyn.qlack2.fuse.chatim.api.MessageService;
import com.eurodyn.qlack2.fuse.chatim.api.RoomService;
import com.eurodyn.qlack2.fuse.chatim.api.dto.MessageDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import java.util.UUID;
import java.util.*;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class MessageServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    MessageService messageService;

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

        Assert.assertNotNull(messageService.sendMessage(messageDTO));
    }

}
