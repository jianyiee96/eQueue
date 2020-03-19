package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import util.enumeration.TableStatusEnum;

@Entity
public class DiningTable implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diningTableId;

    @Column(nullable = false, unique = false)
    @NotNull
    private String qrCode;

    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(1)
    @Max(8)
    private Long seatingCapacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private TableStatusEnum tableStatus;

    @OneToOne(optional = true)
    private Customer customer;
    
    public DiningTable(){
        
        this.tableStatus = TableStatusEnum.FROZEN_UNOCCUPIED;
        this.qrCode = "default.jpg";
        
    }
    
    public DiningTable(Long seatingCapacity){
        
        this();
        this.seatingCapacity = seatingCapacity;
        
    }
    
    public Long getDiningTableId() {
        return diningTableId;
    }

    public void setDiningTableId(Long diningTableId) {
        this.diningTableId = diningTableId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Long getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Long seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public TableStatusEnum getTableStatus() {
        return tableStatus;
    }

    public void setTableStatus(TableStatusEnum tableStatus) {
        this.tableStatus = tableStatus;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        if (this.customer != null) {
            this.customer.setAllocatedDiningTable(null);
        }

        this.customer = customer;

        if (this.customer != null) {
            this.customer.setAllocatedDiningTable(this);
        }
    }

    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (diningTableId != null ? diningTableId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the diningTableId fields are not set
        if (!(object instanceof DiningTable)) {
            return false;
        }
        DiningTable other = (DiningTable) object;
        if ((this.diningTableId == null && other.diningTableId != null) || (this.diningTableId != null && !this.diningTableId.equals(other.diningTableId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Table[ id=" + diningTableId + " ]";
    }

}
