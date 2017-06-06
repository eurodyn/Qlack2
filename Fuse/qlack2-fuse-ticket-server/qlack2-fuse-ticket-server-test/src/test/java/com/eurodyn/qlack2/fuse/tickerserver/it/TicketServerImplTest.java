package com.eurodyn.qlack2.fuse.tickerserver.it;

import com.eurodyn.qlack2.fuse.ticketserver.api.criteria.TicketSearchCriteria;
import com.eurodyn.qlack2.fuse.ticketserver.api.TicketDTO;
import com.eurodyn.qlack2.fuse.ticketserver.api.TicketServerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import java.util.*;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class TicketServerImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    TicketServerService ticketServerService;

    @Test
    public void createTicket(){
        TicketDTO ticketDTO = TestUtilities.createTicketDTO();
        String ticketID = ticketServerService.createTicket(ticketDTO);
        Assert.assertNotNull(ticketID);
    }

    @Test
    public void deleteTicket(){
        TicketDTO ticketDTO = TestUtilities.createTicketDTO();
        String ticketID = ticketServerService.createTicket(ticketDTO);
        Assert.assertNotNull(ticketID);

        ticketServerService.deleteTicket(ticketID);
        Assert.assertNull(ticketServerService.getTicket(ticketID));
    }

    @Test
    public void deleteTickets(){
        TicketDTO ticketDTO = TestUtilities.createTicketDTO();
        String ticketID = ticketServerService.createTicket(ticketDTO);
        Assert.assertNotNull(ticketID);

        Collection<String> ticketIDs = new ArrayList<>();
        ticketIDs.add(ticketID);

        ticketServerService.deleteTickets(ticketIDs);
        Assert.assertNull(ticketServerService.getTicket(ticketID));
    }

    @Test
    public void isValid(){
        Calendar endCale = new GregorianCalendar(2017, 9, 21);
        Date endDate = endCale.getTime();

        TicketDTO ticketDTO = TestUtilities.createTicketDTO();
        ticketDTO.setValidUntil(endDate.getTime());
        String ticketID = ticketServerService.createTicket(ticketDTO);
        Assert.assertNotNull(ticketID);

        Assert.assertTrue(ticketServerService.isValid(ticketID));
    }

    @Test
    public void getValidUntil(){
        TicketDTO ticketDTO = TestUtilities.createTicketDTO();
        Long validUntil = new Date().getTime();
        ticketDTO.setValidUntil(validUntil);
        String ticketID = ticketServerService.createTicket(ticketDTO);
        Assert.assertNotNull(ticketID);

        Assert.assertEquals(validUntil,ticketServerService.getValidUntil(ticketID));
    }

    @Test
    public void revoke(){
        TicketDTO ticketDTO = TestUtilities.createTicketDTO();
        String ticketID = ticketServerService.createTicket(ticketDTO);
        Assert.assertNotNull(ticketID);

        ticketServerService.revoke(ticketID);
        Assert.assertNotEquals(ticketDTO.getLastModifiedAt(),ticketServerService.getTicket(ticketID).getLastModifiedAt());
    }

    @Test
    public void revokeIds(){
        TicketDTO ticketDTO = TestUtilities.createTicketDTO();
        String ticketID = ticketServerService.createTicket(ticketDTO);
        Assert.assertNotNull(ticketID);

        Collection<String> ticketRevIDs = new ArrayList<>();
        ticketRevIDs.add(ticketID);

        ticketServerService.revoke(ticketRevIDs);
        Assert.assertNotEquals(ticketDTO.getLastModifiedAt(),ticketServerService.getTicket(ticketID).getLastModifiedAt());
    }

    @Test
    public void extendValidity(){
        TicketDTO ticketDTO = TestUtilities.createTicketDTO();
        Long validUntil = new Date().getTime();
        String ticketID = ticketServerService.createTicket(ticketDTO);
        Assert.assertNotNull(ticketID);

        ticketServerService.extendValidity(ticketID,validUntil);
        Assert.assertEquals(validUntil,ticketServerService.getTicket(ticketID).getValidUntil());
    }

    @Test
    public void extendAutoExtendValidity(){
        Calendar endCale = new GregorianCalendar(2017, 9, 21);
        Date endDate = endCale.getTime();

        TicketDTO ticketDTO = TestUtilities.createTicketDTO();
        String ticketID = ticketServerService.createTicket(ticketDTO);
        Assert.assertNotNull(ticketID);

        ticketServerService.extendAutoExtendValidity(ticketID,endDate.getTime());
        Assert.assertNotEquals(ticketDTO.getValidUntil(),ticketServerService.getTicket(ticketID).getAutoExtendDuration());
    }

    @Test
    public void getTicket(){
        TicketDTO ticketDTO = TestUtilities.createTicketDTO();
        String ticketID = ticketServerService.createTicket(ticketDTO);
        Assert.assertNotNull(ticketID);

        Assert.assertNotNull(ticketServerService.getTicket(ticketID));
    }

    @Test
    public void findTickets(){
        TicketSearchCriteria ticketSearchCriteria = TicketSearchCriteria.TicketSearchCriteriaBuilder.createCriteria().build();

        TicketDTO ticketDTO = TestUtilities.createTicketDTO();
        String ticketID = ticketServerService.createTicket(ticketDTO);
        Assert.assertNotNull(ticketID);

        Assert.assertNotNull(ticketServerService.findTickets(ticketSearchCriteria));
    }

    @Test
    public void cleanupExpired(){
        TicketDTO ticketDTO = TestUtilities.createTicketDTO();
        String ticketID = ticketServerService.createTicket(ticketDTO);
        Assert.assertNotNull(ticketID);

        ticketServerService.cleanupExpired();
        Assert.assertNull(ticketServerService.getTicket(ticketID));
    }

    @Test
    public void cleanupRevoked(){
        TicketDTO ticketDTO = TestUtilities.createTicketDTO();
        String ticketID = ticketServerService.createTicket(ticketDTO);
        Assert.assertNotNull(ticketID);

        ticketServerService.revoke(ticketID);
        ticketServerService.cleanupRevoked();
        Assert.assertNull(ticketServerService.getTicket(ticketID));
    }

}

