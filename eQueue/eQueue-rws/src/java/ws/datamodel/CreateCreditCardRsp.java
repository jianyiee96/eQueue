package ws.datamodel;

public class CreateCreditCardRsp {

    private Long creditCardId;

    public CreateCreditCardRsp() {
    }

    public CreateCreditCardRsp(Long creditCardId) {
        this.creditCardId = creditCardId;
    }

    public Long getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(Long creditCardId) {
        this.creditCardId = creditCardId;
    }

}
