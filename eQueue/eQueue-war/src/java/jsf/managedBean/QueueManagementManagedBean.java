/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.DiningTableSessionBeanLocal;
import ejb.session.stateless.NotificationSessionBeanLocal;
import ejb.session.stateless.QueueSessionBeanLocal;
import entity.DiningTable;
import entity.Notification;
import entity.Queue;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import util.enumeration.NotificationTypeEnum;
import util.enumeration.QueueStatusEnum;
import util.enumeration.TableStatusEnum;
import util.exceptions.UnableToCreateNotificationException;

@Named(value = "queueManagementManagedBean")
@ApplicationScoped
public class QueueManagementManagedBean {

    @EJB
    private DiningTableSessionBeanLocal diningTableSessionBean;

    @EJB
    private NotificationSessionBeanLocal notificationSessionBean;

    @EJB
    private QueueSessionBeanLocal queueSessionBean;

    private List<Queue> queues;
    private List<DiningTable> diningTables;
    private Queue selectedQueue;
    private String notificationTitle;
    private String notificationMessage;

    public QueueManagementManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        retrieveQueues();
    }

    public void retrieveQueues() {
        this.queues = queueSessionBean.retrieveAllQueues();
        this.diningTables = diningTableSessionBean.retrieveAllTables();
    }

    public String dateDiff(Date date) {

        Date d1 = date;
        Date d2 = new Date();

        //in milliseconds
        long diff = d2.getTime() - d1.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;

        String seconds = (diffSeconds < 10) ? "0" + diffSeconds : "" + diffSeconds;
        String minutes = (diffMinutes < 10) ? "0" + diffMinutes : "" + diffMinutes;
        String hours = (diffHours < 10) ? "0" + diffHours : "" + diffHours;

        return hours + ":" + minutes + ":" + seconds;

    }

    public String formatDate(Date date) {

        String minutes = (date.getMinutes() < 10) ? "0" + date.getMinutes() : "" + date.getMinutes();
        String hours = (date.getHours() < 10) ? "0" + date.getHours() : "" + date.getHours();

        return hours + ":" + minutes;
    }

    public void doCreateNotification(ActionEvent event) {
        selectedQueue = (Queue) event.getComponent().getAttributes().get("selectedQueue");
        notificationTitle = "";
        notificationMessage = "";
    }

    public void createNotification() {
        String title = notificationTitle;
        String message = notificationMessage;

        try {
            notificationSessionBean.createNewNotification(new Notification(title, message, NotificationTypeEnum.GENERAL), selectedQueue.getCustomer().getCustomerId());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Notification Sent Successfully", null));
        } catch (UnableToCreateNotificationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating notification", null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public List<Queue> getQueues() {
        return queues;
    }

    public Long getNoOfActiveQueues() {
        return queues.stream().filter(x -> x.getQueueStatus() == QueueStatusEnum.ACTIVE).count();
    }

    public Long getNoOfAllocatedQueues() {
        return queues.stream().filter(x -> x.getQueueStatus() == QueueStatusEnum.ALLOCATED).count();
    }

    public void setQueues(List<Queue> queues) {
        this.queues = queues;
    }

    public List<DiningTable> getDiningTables() {
        return diningTables;
    }

    public Long getNoOfFrozenTables() {
        return diningTables.stream()
                .filter(x -> x.getTableStatus() == TableStatusEnum.FROZEN_ALLOCATED
                || x.getTableStatus() == TableStatusEnum.FROZEN_OCCUPIED
                || x.getTableStatus() == TableStatusEnum.FROZEN_UNOCCUPIED)
                .count();
    }

    public Long getNoOfUnoccupiedTables() {
        return diningTables.stream()
                .filter(x -> x.getTableStatus() == TableStatusEnum.FROZEN_UNOCCUPIED
                || x.getTableStatus() == TableStatusEnum.UNFROZEN_UNOCCUPIED)
                .count();
    }

    public Long getNoOfOccupiedTables() {
        return diningTables.stream()
                .filter(x -> x.getTableStatus() == TableStatusEnum.FROZEN_OCCUPIED
                || x.getTableStatus() == TableStatusEnum.UNFROZEN_OCCUPIED)
                .count();
    }

    public Long getNoOfAllocatedTables() {
        return diningTables.stream()
                .filter(x -> x.getTableStatus() == TableStatusEnum.FROZEN_ALLOCATED
                || x.getTableStatus() == TableStatusEnum.UNFROZEN_ALLOCATED)
                .count();
    }

    public Long getNoOfUnfrozenTables() {
        return this.diningTables.size() - getNoOfFrozenTables();
    }

    public void setDiningTables(List<DiningTable> diningTables) {
        this.diningTables = diningTables;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

}
