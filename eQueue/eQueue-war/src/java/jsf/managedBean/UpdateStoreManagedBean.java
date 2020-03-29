package jsf.managedBean;

import ejb.session.stateless.StoreManagementSessionBeanLocal;
import entity.Store;
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

@Named(value = "updateStoreManagedBean")
@ViewScoped
public class UpdateStoreManagedBean implements Serializable {

    @EJB
    private StoreManagementSessionBeanLocal storeManagementSessionBeanLocal;

    private Store storeToUpdate;

    public UpdateStoreManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {

        try {
            storeToUpdate = storeManagementSessionBeanLocal.retrieveStore();
        } catch (StoreNotInitializedException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the store variables: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void foo() {

    }

    public void back(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewStore.xhtml");
    }

    public void update() {
        try {
            storeManagementSessionBeanLocal.updateStore(storeToUpdate);
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

    public Store getStoreToUpdate() {
        return storeToUpdate;
    }

    public void setStoreToUpdate(Store storeToUpdate) {
        this.storeToUpdate = storeToUpdate;
    }

}
