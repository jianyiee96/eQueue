package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Entity
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String storeName;

    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    @Email
    private String storeEmail;

    @Column(nullable = false)
    @NotNull
    private String storeAddress;

    @Column(nullable = true)
    private String messageOfTheDay;
    
    @Column(nullable = false, length = 64)
    @Size(max = 64)
    @NotNull
    private String storeContact;

    // To be left out? (Wee Keat)
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(1)
    private Long estimatedQueueUnitWaitingMinutes;
    
    // To be left out? (Wee Keat)
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(1)
    private Long allocationGraceWaitingMinutes;
    
    public Store(){
        this.estimatedQueueUnitWaitingMinutes = 5l;
        this.allocationGraceWaitingMinutes = 1l;
    }
    
    public Store(String storeName, String storeEmail, String storeAddress, String messageOfTheDay, String storeContact) {
        this();
        this.storeName = storeName;
        this.storeEmail = storeEmail;
        this.storeAddress = storeAddress;
        this.messageOfTheDay = messageOfTheDay;
        this.storeContact = storeContact;
        
    }
    
    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getMessageOfTheDay() {
        return messageOfTheDay;
    }

    public void setMessageOfTheDay(String messageOfTheDay) {
        this.messageOfTheDay = messageOfTheDay;
    }

    public String getStoreContact() {
        return storeContact;
    }

    public void setStoreContact(String storeContact) {
        this.storeContact = storeContact;
    }

    public Long getEstimatedQueueUnitWaitingMinutes() {
        return estimatedQueueUnitWaitingMinutes;
    }

    public void setEstimatedQueueUnitWaitingMinutes(Long estimatedQueueUnitWaitingMinutes) {
        this.estimatedQueueUnitWaitingMinutes = estimatedQueueUnitWaitingMinutes;
    }

    public Long getAllocationGraceWaitingMinutes() {
        return allocationGraceWaitingMinutes;
    }

    public void setAllocationGraceWaitingMinutes(Long allocationGraceWaitingMinutes) {
        this.allocationGraceWaitingMinutes = allocationGraceWaitingMinutes;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (storeId != null ? storeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the storeId fields are not set
        if (!(object instanceof Store)) {
            return false;
        }
        Store other = (Store) object;
        if ((this.storeId == null && other.storeId != null) || (this.storeId != null && !this.storeId.equals(other.storeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.StoreVariables[ id=" + storeId + " ]";
    }

}
