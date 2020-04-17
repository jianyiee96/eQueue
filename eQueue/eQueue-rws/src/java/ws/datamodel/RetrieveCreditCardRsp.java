package ws.datamodel;

import entity.CreditCard;

public class RetrieveCreditCardRsp {

    private CreditCard creditCard;

    public RetrieveCreditCardRsp() {
    }

    public RetrieveCreditCardRsp(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

}
