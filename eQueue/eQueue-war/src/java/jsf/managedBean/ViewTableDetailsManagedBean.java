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
import util.exceptions.DiningTableNotFoundException;

@Named(value = "viewTableDetailsManagedBean")
@ViewScoped
public class ViewTableDetailsManagedBean implements Serializable {

    @EJB
    private DiningTableSessionBeanLocal diningTableSessionBeanLocal;
    
    private Long diningTableIdToView;
    private DiningTable diningTableToView;
    
    public ViewTableDetailsManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        diningTableIdToView = (Long)FacesContext.getCurrentInstance().getExternalContext().getFlash().get("diningTableIdToView");
        
        try {
            diningTableToView = diningTableSessionBeanLocal.retrieveDiningTableById(diningTableIdToView);
        } catch(DiningTableNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occured while retrieving the table details: " + ex.getMessage(), null));
        } catch (Exception ex) {
               FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }
    
    public void back(ActionEvent event) throws IOException
    {
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewAllDiningTables.xhtml");
    }
    
    public void foo() {
        
    }
    
    public void updateDiningTable(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("diningTableIdToUpdate", diningTableIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("updateDiningTable.xhtml");
    }
    
    public void deleteDiningTable(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("diningTableIdToDelete", diningTableIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("deleteDiningTable.xhtml");
    }

    public DiningTable getDiningTableToView() {
        return diningTableToView;
    }

    public void setDiningTableToView(DiningTable diningTableToView) {
        this.diningTableToView = diningTableToView;
    }
}
