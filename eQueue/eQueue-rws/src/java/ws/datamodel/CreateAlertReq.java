package ws.datamodel;

import entity.Alert;

public class CreateAlertReq {

    private Alert alert;

    public CreateAlertReq() {
    }

    public CreateAlertReq(Alert alert) {
        this.alert = alert;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

}
