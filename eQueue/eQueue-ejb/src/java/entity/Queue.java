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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import util.enumeration.QueueStatusEnum;

@Entity
public class Queue implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long queueId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date startDateTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date allocatedDateTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date seatedDateTime;

    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(1)
    private Long numberOfPax;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private QueueStatusEnum queueStatus;

    @OneToOne(optional = false)
    @JoinColumn (nullable = false)
    private Customer customer;
    

    public Queue() {

        this.startDateTime = new Date();
        this.queueStatus = QueueStatusEnum.ACTIVE;

    }

    public Queue(Long numberOfPax) {

        this();
        this.numberOfPax = numberOfPax;

    }

    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getAllocatedDateTime() {
        return allocatedDateTime;
    }

    public void setAllocatedDateTime(Date allocatedDateTime) {
        this.allocatedDateTime = allocatedDateTime;
    }

    public Date getSeatedDateTime() {
        return seatedDateTime;
    }

    public void setSeatedDateTime(Date seatedDateTime) {
        this.seatedDateTime = seatedDateTime;
    }

    public Long getNumberOfPax() {
        return numberOfPax;
    }

    public void setNumberOfPax(Long numberOfPax) {
        this.numberOfPax = numberOfPax;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        if (this.customer != null) {
            this.customer.setCurrentQueue(null);
        }

        this.customer = customer;

        if (this.customer != null) {
            this.customer.setCurrentQueue(this);
        }
    }    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (queueId != null ? queueId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the queueId fields are not set
        if (!(object instanceof Queue)) {
            return false;
        }
        Queue other = (Queue) object;
        if ((this.queueId == null && other.queueId != null) || (this.queueId != null && !this.queueId.equals(other.queueId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Queue[ id=" + queueId + " ]";
    }

}
