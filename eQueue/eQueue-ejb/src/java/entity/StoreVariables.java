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
public class StoreVariables implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeVariableId;

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

    
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(1)
    private Long estimatedQueueUnitWaitingMinutes;
    
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(1)
    private Long allocationGraceWaitingMinutes;
    
    public StoreVariables(){
        this.estimatedQueueUnitWaitingMinutes = 0l;
        this.allocationGraceWaitingMinutes = 15l;
    }
    
    public StoreVariables(String storeName, String storeEmail, String storeAddress, String messageOfTheDay, String storeContact) {
        this();
        this.storeName = storeName;
        this.storeEmail = storeEmail;
        this.storeAddress = storeAddress;
        this.messageOfTheDay = messageOfTheDay;
        this.storeContact = storeContact;
        
    }
    
    public Long getStoreVariableId() {
        return storeVariableId;
    }

    public void setStoreVariableId(Long storeVariableId) {
        this.storeVariableId = storeVariableId;
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
        hash += (storeVariableId != null ? storeVariableId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the storeVariableId fields are not set
        if (!(object instanceof StoreVariables)) {
            return false;
        }
        StoreVariables other = (StoreVariables) object;
        if ((this.storeVariableId == null && other.storeVariableId != null) || (this.storeVariableId != null && !this.storeVariableId.equals(other.storeVariableId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.StoreVariables[ id=" + storeVariableId + " ]";
    }

}
