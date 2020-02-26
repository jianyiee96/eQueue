package jsf.managedBean;

import ejb.session.stateless.StoreManagementSessionBeanLocal;
import entity.StoreVariables;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exceptions.StoreNotInitializedException;

@Named(value = "updateStoreVariablesManagedBean")
@ViewScoped
public class UpdateStoreVariablesManagedBean implements Serializable {

    @EJB
    private StoreManagementSessionBeanLocal storeManagementSessionBeanLocal;

    private StoreVariables storeVariablesToUpdate;

    public UpdateStoreVariablesManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {

        try {
            storeVariablesToUpdate = storeManagementSessionBeanLocal.retrieveStoreVariables();
        } catch (StoreNotInitializedException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the store variables: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void foo() {

    }

    public void back(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewStoreVariables.xhtml");
    }

    public void updateVariables() {
        try {
            storeManagementSessionBeanLocal.updateStoreVariables(storeVariablesToUpdate);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Variables updated successfully", null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public StoreManagementSessionBeanLocal getStoreManagementSessionBeanLocal() {
        return storeManagementSessionBeanLocal;
    }

    public void setStoreManagementSessionBeanLocal(StoreManagementSessionBeanLocal storeManagementSessionBeanLocal) {
        this.storeManagementSessionBeanLocal = storeManagementSessionBeanLocal;
    }

    public StoreVariables getStoreVariablesToUpdate() {
        return storeVariablesToUpdate;
    }

    public void setStoreVariablesToUpdate(StoreVariables storeVariablesToUpdate) {
        this.storeVariablesToUpdate = storeVariablesToUpdate;
    }

}
