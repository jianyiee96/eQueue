package jsf.managedBean;

import ejb.session.stateless.StoreManagementSessionBeanLocal;
import entity.Store;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exceptions.InputDataValidationException;
import util.exceptions.StoreNotInitializedException;

@Named(value = "storeDetailsManagedBean")
@ViewScoped
public class StoreDetailsManagedBean implements Serializable {

    @EJB
    private StoreManagementSessionBeanLocal storeManagementSessionBeanLocal;

    private Store store;
    private Boolean isEditState;

    public StoreDetailsManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        isEditState = false;
        try {
            store = storeManagementSessionBeanLocal.retrieveStore();
        } catch (StoreNotInitializedException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the store variables: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void updateStore() {
        try {
            storeManagementSessionBeanLocal.updateStore(store);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Store updated successfully", ""));
        } catch (InputDataValidationException | StoreNotInitializedException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Boolean getIsEditState() {
        return isEditState;
    }

    public void setIsEditState(Boolean isEditState) {
        this.isEditState = isEditState;
    }

}
