package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;


@Entity
public class CreditCard implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long creditCardId;

    //Format of 1234-1234-1234-1234
    @Column(nullable = false, length = 19)
    @NotNull
    @Size(min = 19, max = 19)
    private String creditCardNumber;
    
    @Column(nullable = false, length = 3)
    @NotNull
    @Size(min = 3, max = 3)
    private String cvv;
    
    @Column(nullable = false)
    @NotNull
    @Min(1)
    @Max(12)
    @Positive
    private Long expiryMonth;
    
    @Column(nullable = false)
    @NotNull
    @Min(1)
    @Max(31)
    @Positive
    private Long expiryDay;
    
    public CreditCard(){
        
    }
    
    public CreditCard(String creditCardNumber, String cvv, Long expiryMonth, Long expiryDay){
        this();
        this.creditCardNumber = creditCardNumber;
        this.cvv = cvv;
        this.expiryMonth = expiryMonth;
        this.expiryDay = expiryDay;
    }
    
    public Long getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(Long creditCardId) {
        this.creditCardId = creditCardId;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Long getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(Long expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public Long getExpiryDay() {
        return expiryDay;
    }

    public void setExpiryDay(Long expiryDay) {
        this.expiryDay = expiryDay;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (creditCardId != null ? creditCardId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the creditCardId fields are not set
        if (!(object instanceof CreditCard)) {
            return false;
        }
        CreditCard other = (CreditCard) object;
        if ((this.creditCardId == null && other.creditCardId != null) || (this.creditCardId != null && !this.creditCardId.equals(other.creditCardId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CreditCard[ id=" + creditCardId + " ]";
    }
    
}
