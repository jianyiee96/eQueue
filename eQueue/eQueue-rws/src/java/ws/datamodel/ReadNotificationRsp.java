/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

/**
 *
 * @author User
 */
public class ReadNotificationRsp {
    
    private Boolean change;

    public ReadNotificationRsp() {
    }

    public ReadNotificationRsp(Boolean change) {
        this.change = change;
    }

    public Boolean getChange() {
        return change;
    }

    public void setChange(Boolean change) {
        this.change = change;
    }
    
    
    
}
