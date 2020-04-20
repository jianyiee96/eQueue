package ws.datamodel;

import entity.CreditCard;

public class CreateCreditCardReq {

    private String email;
    private CreditCard creditCard;

    public CreateCreditCardReq() {
    }

    public CreateCreditCardReq(String email, CreditCard creditCard) {
        this.email = email;
        this.creditCard = creditCard;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

}
