package jsf.managedBean;

import ejb.session.stateless.DiningTableSessionBeanLocal;
import entity.DiningTable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.TableStatusEnum;
import util.exceptions.DiningTableNotFoundException;

@Named(value = "updateDiningTableManagedBean")
@ViewScoped
public class UpdateDiningTableManagedBean implements Serializable {

    @EJB
    private DiningTableSessionBeanLocal diningTableSessionBeanLocal;

    private Long diningTableIdToUpdate;
    private DiningTable diningTableToUpdate;
    private List<TableStatusEnum> tableStatuses = new ArrayList();

    public UpdateDiningTableManagedBean() {
        tableStatuses.add(TableStatusEnum.FROZEN_UNOCCUPIED);
        tableStatuses.add(TableStatusEnum.FROZEN_OCCUPIED);
        tableStatuses.add(TableStatusEnum.FROZEN_ALLOCATED);
        tableStatuses.add(TableStatusEnum.UNFROZEN_UNOCCUPIED);
        tableStatuses.add(TableStatusEnum.UNFROZEN_OCCUPIED);
        tableStatuses.add(TableStatusEnum.UNFROZEN_ALLOCATED);
    }

    @PostConstruct
    public void postConstruct() {
        diningTableIdToUpdate = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("diningTableIdToUpdate");

        try {
            diningTableToUpdate = diningTableSessionBeanLocal.retrieveDiningTableById(diningTableIdToUpdate);

        } catch (DiningTableNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the dining table details: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void back(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("diningTableIdToView", diningTableIdToUpdate);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewDiningTableDetails.xhtml");
    }

    public void foo() {
    }

    public void updateDiningTable(ActionEvent event) {

        try {

            diningTableSessionBeanLocal.updateDiningTableInformation(diningTableToUpdate);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Dining table updated successfully", null));
        } catch (DiningTableNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating dining table: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public DiningTable getDiningTableToUpdate() {
        return diningTableToUpdate;
    }

    public void setDiningTableToUpdate(DiningTable diningTableToUpdate) {
        this.diningTableToUpdate = diningTableToUpdate;
    }

    public List<TableStatusEnum> getTableStatuses() {
        return tableStatuses;
    }

    public void setTableStatuses(List<TableStatusEnum> tableStatuses) {
        this.tableStatuses = tableStatuses;
    }

}
