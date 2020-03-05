/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.DiningTableSessionBeanLocal;
import entity.DiningTable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

/**
 *
 * @author keith
 */
@Named(value = "createNewDiningTableManagedBean")
@RequestScoped
public class CreateNewDiningTableManagedBean {

    @EJB
    private DiningTableSessionBeanLocal diningTableSessionBeanLocal;

    private DiningTable newDiningTable;

    public CreateNewDiningTableManagedBean() {
        newDiningTable = new DiningTable();
    }

    public void createNewDiningTable(ActionEvent event) {

        try {
            Long diningTableId = diningTableSessionBeanLocal.createNewDiningTable(getNewDiningTable());
            setNewDiningTable(new DiningTable());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New dining table created successfully (Dining Table ID: " + diningTableId + ")", null));
        } catch (InputDataValidationException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new dining table: " + ex.getMessage(), null));
        }
    }

    public DiningTable getNewDiningTable() {
        return newDiningTable;
    }

    public void setNewDiningTable(DiningTable newDiningTable) {
        this.newDiningTable = newDiningTable;
    }
}
