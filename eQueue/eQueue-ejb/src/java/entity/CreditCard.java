package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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

    @Column(nullable = false, length = 26)
    @NotNull
    @Size(max = 26)
    private String creditCardName;

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
    @Positive
    private Long expiryYear;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private Customer customer;

    public CreditCard() {

    }

    public CreditCard(String creditCardName, String creditCardNumber, String cvv, Long expiryMonth, Long expiryYear) {
        this();
        this.creditCardName = creditCardName;
        this.creditCardNumber = creditCardNumber;
        this.cvv = cvv;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
    }

    public String getCreditCardName() {
        return creditCardName;
    }

    public void setCreditCardName(String creditCardName) {
        this.creditCardName = creditCardName;
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

    public Long getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(Long expiryYear) {
        this.expiryYear = expiryYear;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
