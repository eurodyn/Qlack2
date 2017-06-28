package com.eurodyn.qlack2.fuse.simm.it;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.simm.api.NotificationService;
import com.eurodyn.qlack2.fuse.simm.api.dto.NotificationDTO;
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
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class NotificationServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    NotificationService notificationService;

    @Test
    public void createNotification(){
        NotificationDTO notificationDTO = TestUtilities.createNotificationDTO();
        String notificationID = notificationService.createNotification(notificationDTO);
        Assert.assertNotNull(notificationID);
    }

    @Test
    public void getPendingNotifications(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        NotificationDTO notificationDTO = TestUtilities.createNotificationDTO();
        String notificationID = notificationService.createNotification(notificationDTO);
        Assert.assertNotNull(notificationID);

        Assert.assertNotNull(notificationService.getPendingNotifications(notificationDTO.getSrcUserId(),paging));
    }

    @Test
    public void getNotificationsByTime(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        NotificationDTO notificationDTO = TestUtilities.createNotificationDTO();
        String notificationID = notificationService.createNotification(notificationDTO);
        Assert.assertNotNull(notificationID);

        Assert.assertNotNull(notificationService.getNotificationsByTime(notificationDTO.getSrcUserId(),new Date().getTime(),paging));
    }

    @Test
    public void getNotificationsByNumber(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        NotificationDTO notificationDTO = TestUtilities.createNotificationDTO();
        String notificationID = notificationService.createNotification(notificationDTO);
        Assert.assertNotNull(notificationID);

        Assert.assertNotNull(notificationService.getNotificationsByNumber(notificationDTO.getSrcUserId(),1));
    }

    @Test
    public void getNotifications(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        NotificationDTO notificationDTO = TestUtilities.createNotificationDTO();
        String notificationID = notificationService.createNotification(notificationDTO);
        Assert.assertNotNull(notificationID);

        Assert.assertNotNull(notificationService.getNotifications(notificationDTO.getSrcUserId(),paging));
    }

    @Test
    public void markPendingNotificationsAsRead(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        NotificationDTO notificationDTO = TestUtilities.createNotificationDTO();
        String notificationID = notificationService.createNotification(notificationDTO);
        Assert.assertNotNull(notificationID);

        notificationService.markPendingNotificationsAsRead(notificationDTO.getSrcUserId());

        NotificationDTO[] notifId = notificationService.getNotificationsForAType(notificationDTO.getSrcUserId(),notificationDTO.getType(),new Date().getTime());
        Assert.assertNotNull(notifId);
    }

    @Test
    public void markNotificationsAsRead(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        NotificationDTO notificationDTO = TestUtilities.createNotificationDTO();
        String notificationID = notificationService.createNotification(notificationDTO);
        Assert.assertNotNull(notificationID);

        String[] notifIds = {notificationID};
        notificationService.markNotificationsAsRead(notifIds);

        NotificationDTO[] notifId = notificationService.getNotificationsForAType(notificationDTO.getSrcUserId(),notificationDTO.getType(),new Date().getTime());
        Assert.assertNotNull(notifId);
    }

    @Test
    public void getNotificationsForAType(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        NotificationDTO notificationDTO = TestUtilities.createNotificationDTO();
        String notificationID = notificationService.createNotification(notificationDTO);
        Assert.assertNotNull(notificationID);

        Assert.assertNotNull(notificationService.getNotificationsForAType(notificationDTO.getSrcUserId(),notificationDTO.getType(),new Date().getTime()));
    }

}


