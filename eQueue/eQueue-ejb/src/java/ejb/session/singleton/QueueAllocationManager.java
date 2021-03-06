package ejb.session.singleton;

import ejb.session.stateless.DiningTableSessionBeanLocal;
import ejb.session.stateless.NotificationSessionBeanLocal;
import ejb.session.stateless.QueueSessionBeanLocal;
import ejb.session.stateless.StoreManagementSessionBeanLocal;
import entity.DiningTable;
import entity.Notification;
import entity.Queue;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import util.enumeration.NotificationTypeEnum;
import util.enumeration.QueueStatusEnum;
import util.enumeration.TableStatusEnum;
import util.exceptions.QueueDoesNotExistException;
import util.exceptions.StoreNotInitializedException;
import util.exceptions.UnableToCreateNotificationException;

@Singleton
@LocalBean
@Startup

public class QueueAllocationManager {

    @EJB
    private QueueSessionBeanLocal queueSessionBeanLocal;
    @EJB
    private DiningTableSessionBeanLocal diningTableSessionBeanLocal;
    @EJB
    private StoreManagementSessionBeanLocal storeManagementSessionBeanLocal;
    @EJB
    private NotificationSessionBeanLocal notificationSessionBeanLocal;

    @Resource
    private TimerService timerService;

    @PostConstruct
    public void initialTrigger() {

        allocationTimer();
    }

    @Schedule(hour = "*", minute = "*/1", info = "allocationTimer")
    public void allocationTimer() {
        int allocated = 0;
        Long oldTimeStamp = new Date().getTime();
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

        System.out.println("============================================================================");
        System.out.println("- allocationTimer: Timeout at " + timeStamp);

        List<Queue> activeQueues = queueSessionBeanLocal.retrieveAllActiveQueues();
        List<DiningTable> unallocatedDiningTables = diningTableSessionBeanLocal.retrieveAllUnfrozenUnoccupiedTables();
        System.out.println("-");
        System.out.println("- Active Queues: " + activeQueues.size());
        for (Queue queue : activeQueues) {
            System.out.println("-\t" + queue.getQueueId() + "-> Queue Pax: " + queue.getNumberOfPax() + " Time: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(queue.getStartDateTime()) + " Active? " + (queue.getQueueStatus() == QueueStatusEnum.ACTIVE ? "YES" : "NO"));
        }

        System.out.println("- Active Tables: " + unallocatedDiningTables.size());
        for (DiningTable diningTable : unallocatedDiningTables) {
            System.out.println("-\t" + diningTable.getDiningTableId() + "-> " + "TableCapacity: " + diningTable.getSeatingCapacity() + "|Status:" + diningTable.getTableStatus());
        }
        System.out.println("-");
        for (Queue queue : activeQueues) {

            for (DiningTable diningTable : unallocatedDiningTables) {

                if (diningTable.getSeatingCapacity() >= queue.getNumberOfPax()
                        && diningTable.getTableStatus() == TableStatusEnum.UNFROZEN_UNOCCUPIED
                        && queue.getQueueStatus() == QueueStatusEnum.ACTIVE) {
                    System.out.println("- (!!!) [Allocating] Queue(id): " + queue.getQueueId() + ", Customer(id): " + queue.getCustomer().getCustomerId() + " to Table(id): " + diningTable.getDiningTableId());

                    diningTableSessionBeanLocal.allocateTableToCustomer(diningTable.getDiningTableId(), queue.getCustomer().getCustomerId());
                    queueSessionBeanLocal.allocateQueue(queue.getQueueId());

                    allocated++;

                    TimerConfig timerConfig = new TimerConfig(queue.getCustomer().getCustomerId().toString(), true);

                    try {

                        int timeOutMillis = storeManagementSessionBeanLocal.retrieveStore().getAllocationGraceWaitingMinutes().intValue() * 60000;
                        Timer timer = timerService.createSingleActionTimer(timeOutMillis, timerConfig); // to be change to store variable timeout duration.
                        System.out.println("- Grace time: " + (timeOutMillis / 60000) + " minutes.");
                        System.out.println("- Created Queue Invalidation Timer to trigger at : " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(timer.getNextTimeout()) + " Timer info(Customer id): " + timer.getInfo());

                        
                    Notification n = new Notification("Queue Allocated", "You have been allocated to table no: " + diningTable.getDiningTableId() + " and may check in now.", NotificationTypeEnum.QUEUE_EXPIRED);
                    notificationSessionBeanLocal.createNewNotification(n, queue.getCustomer().getCustomerId());
                        
                    } catch (StoreNotInitializedException ex) {
                        System.out.println("Store not initialized, unable to create Invalidation Timer");
                    } catch (UnableToCreateNotificationException ex) {
                        System.out.println("Unable to create notification: " + ex.getMessage());
                    }

                } else {
                    String reason = "";
                    if (diningTable.getSeatingCapacity() < queue.getNumberOfPax()) {
                        reason = "[Size Constraint]";
                    } else if (diningTable.getTableStatus() != TableStatusEnum.UNFROZEN_UNOCCUPIED) {
                        reason = "[Table allocated]";
                    } else if (queue.getQueueStatus() != QueueStatusEnum.ACTIVE) {
                        reason = "[Queue allocated]";
                    } else {
                        reason = "[Unknown]";
                    }

                    System.out.print("- (!!!) [Skipping] Queue(id): " + queue.getQueueId() + ", Customer(id): " + queue.getCustomer().getCustomerId() + " to Table(id): " + diningTable.getDiningTableId() + " " + reason);

                }
            }
        }

        Long newTimeStamp = new Date().getTime();
        System.out.println("- Allocated " + allocated + " customers.");
        System.out.println("- Completed task in " + (newTimeStamp - oldTimeStamp) + " ms.");
        System.out.println("============================================================================");
    }

    @Timeout
    private void queueInvalidationTimer(Timer timer) {
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("- Queue Invalidation Timer triggered! Customer(id): " + timer.getInfo());
        try {

            Notification n = new Notification("Queue Expired", "You have failed to check in to your table on time. Thus, your queue has been invalidated.", NotificationTypeEnum.QUEUE_EXPIRED);

            queueSessionBeanLocal.invalidateCustomerQueue(Long.parseLong(timer.getInfo().toString()));
            notificationSessionBeanLocal.createNewNotification(n, Long.parseLong(timer.getInfo().toString()));

            System.out.println("- Sent notification to customer.");
            System.out.println("- Successfully invalidated and deleted queue.");
        } catch (UnableToCreateNotificationException ex) {
            System.out.println("- Unable to create notification: " + ex.toString());
        } catch (QueueDoesNotExistException ex) {
            System.out.println("- Queue no longer exist. [Customer might have checked-in to dining table/left the queue]");
            System.out.println("- Nothing to do");
        }
        System.out.println("----------------------------------------------------------------------------");
    }

}
