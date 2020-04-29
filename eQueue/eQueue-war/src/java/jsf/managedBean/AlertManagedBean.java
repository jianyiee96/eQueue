package jsf.managedBean;

import ejb.session.stateless.AlertSessionBeanLocal;
import entity.Alert;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

@Named(value = "alertManagedBean")
@ApplicationScoped
public class AlertManagedBean {

    @EJB
    private AlertSessionBeanLocal alertSessionBeanLocal;

    private List<Alert> alerts;

    public AlertManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        alerts = alertSessionBeanLocal.retrieveAllAlerts();
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    public void deleteAlert(ActionEvent event) {
        try {
            Alert alertToDelete = (Alert) event.getComponent().getAttributes().get("alertToDelete");
            alertSessionBeanLocal.deleteAlert(alertToDelete.getAlertId());

            alerts.remove(alertToDelete);

        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }
}
