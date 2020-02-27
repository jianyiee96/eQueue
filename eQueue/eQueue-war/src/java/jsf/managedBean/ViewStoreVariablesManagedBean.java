/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author User
 */
@Named(value = "viewStoreVariablesManagedBean")
@ViewScoped
public class ViewStoreVariablesManagedBean implements Serializable {

    @EJB
    private StoreManagementSessionBeanLocal storeManagementSessionBeanLocal;

    private StoreVariables storeVariablesToView;

    public ViewStoreVariablesManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {

        try {
            storeVariablesToView = storeManagementSessionBeanLocal.retrieveStoreVariables();
        } catch (StoreNotInitializedException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the store variables: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void foo() {

    }

    public void back(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
    }

    public void updateVariables(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("updateVariables.xhtml");
    }

    public StoreVariables getStoreVariablesToView() {
        return storeVariablesToView;
    }

    public void setStoreVariablesToView(StoreVariables storeVariablesToView) {
        this.storeVariablesToView = storeVariablesToView;
    }

}
