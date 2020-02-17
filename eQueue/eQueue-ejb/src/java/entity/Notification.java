package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.NotificationTypeEnum;

@Entity
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date notificationDate;

    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String title;

    @Column(nullable = false, length = 128)
    @NotNull
    @Size(max = 128)
    private String message;

    @Column(nullable = false)
    @NotNull
    private boolean isRead;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Customer customer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private NotificationTypeEnum notificationType;

    public Notification() {
        this.notificationDate = new Date();
        this.isRead = false;
    }

    public Notification(String title, String message, NotificationTypeEnum notificationType) {
        this();
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Date getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        if (this.customer != null) {
            if (this.customer.getNotifications().contains(this)) {
                this.customer.getNotifications().remove(this);
            }
        }

        this.customer = customer;

        if (this.customer != null) {
            if (!this.customer.getNotifications().contains(this)) {
                this.customer.getNotifications().add(this);
            }
        }
    }

    public NotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationTypeEnum notificationType) {
        this.notificationType = notificationType;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (notificationId != null ? notificationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the notificationId fields are not set
        if (!(object instanceof Notification)) {
            return false;
        }
        Notification other = (Notification) object;
        if ((this.notificationId == null && other.notificationId != null) || (this.notificationId != null && !this.notificationId.equals(other.notificationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Notification[ id=" + notificationId + " ]";
    }

}
