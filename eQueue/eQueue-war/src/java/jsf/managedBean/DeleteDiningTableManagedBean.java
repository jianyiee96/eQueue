package jsf.managedBean;

import ejb.session.stateless.DiningTableSessionBeanLocal;
import entity.DiningTable;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exceptions.DeleteDiningTableException;
import util.exceptions.DiningTableNotFoundException;

@Named(value = "deleteDiningTableManagedBean")
@ViewScoped
public class DeleteDiningTableManagedBean implements Serializable {

    @EJB
    private DiningTableSessionBeanLocal diningTableSessionBeanLocal;

    private Long diningTableIdToDelete;
    private DiningTable diningTableToDelete;

    public DeleteDiningTableManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        diningTableIdToDelete = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("diningTableIdToDelete");

        try {
            diningTableToDelete = diningTableSessionBeanLocal.retrieveDiningTableById(diningTableIdToDelete);
        } catch (DiningTableNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the dining table details: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void deleteProduct(ActionEvent event) {
        try {

            diningTableSessionBeanLocal.deleteDiningTable(diningTableIdToDelete);
            diningTableToDelete = null;

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Dining Table deleted successfully", null));
        } catch (DiningTableNotFoundException | DeleteDiningTableException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting dining table: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void back(ActionEvent event) throws IOException {
        if (diningTableToDelete == null) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("viewAllDiningTables.xhtml");
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("diningTableIdToView", diningTableIdToDelete);
            FacesContext.getCurrentInstance().getExternalContext().redirect("viewDiningTableDetails.xhtml");
        }
    }

    public void foo() {
    }

    public Long getDiningTableIdToDelete() {
        return diningTableIdToDelete;
    }

    public void setDiningTableIdToDelete(Long diningTableIdToDelete) {
        this.diningTableIdToDelete = diningTableIdToDelete;
    }

    public DiningTable getDiningTableToDelete() {
        return diningTableToDelete;
    }

    public void setDiningTableToDelete(DiningTable diningTableToDelete) {
        this.diningTableToDelete = diningTableToDelete;
    }

}
