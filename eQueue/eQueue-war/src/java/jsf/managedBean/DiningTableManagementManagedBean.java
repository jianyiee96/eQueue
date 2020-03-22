package jsf.managedBean;

import ejb.session.stateless.DiningTableSessionBeanLocal;
import entity.DiningTable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.enumeration.TableStatusEnum;
import util.exceptions.DeleteDiningTableException;
import util.exceptions.DiningTableNotFoundException;
import util.exceptions.InputDataValidationException;
import util.exceptions.InvalidTableStatusException;
import util.exceptions.UnknownPersistenceException;

@Named
@ViewScoped
public class DiningTableManagementManagedBean implements Serializable {

    @EJB
    private DiningTableSessionBeanLocal diningTableSessionBeanLocal;

    private List<DiningTable> diningTables;
    private List<DiningTable> filteredDiningTables;

    private DiningTable newDiningTable;

    private DiningTable selectedDiningTableToUpdate;

    private List<TableStatusEnum> tableStatuses = new ArrayList();

    private String filePath;

    public DiningTableManagementManagedBean() {
        newDiningTable = new DiningTable();
        selectedDiningTableToUpdate = new DiningTable();
        tableStatuses.add(TableStatusEnum.FROZEN_UNOCCUPIED);
        tableStatuses.add(TableStatusEnum.FROZEN_OCCUPIED);
        tableStatuses.add(TableStatusEnum.FROZEN_ALLOCATED);
        tableStatuses.add(TableStatusEnum.UNFROZEN_UNOCCUPIED);
        tableStatuses.add(TableStatusEnum.UNFROZEN_OCCUPIED);
        tableStatuses.add(TableStatusEnum.UNFROZEN_ALLOCATED);
    }

    @PostConstruct
    public void postConstruct() {

        diningTables = diningTableSessionBeanLocal.retrieveAllTables();
        generateTableQrCodes();

    }

    private void generateTableQrCodes() {
        filePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        Matcher m = Pattern.compile("eQueue").matcher(filePath);
        List<Integer> positions = new ArrayList<>();
        while (m.find()) {
            positions.add(m.end());
        }
        filePath = filePath.substring(0, positions.get(positions.size() - 3)) + "\\eQueue-war\\web\\resources\\images\\qrcode\\";
        diningTables.forEach(t -> diningTableSessionBeanLocal.generateQrCode(t.getQrCode(), filePath+t.getQrCode()+".png"));
    }

    public void viewDiningTableDetails(ActionEvent event) throws IOException {
        Long diningTableIdToView = (Long) event.getComponent().getAttributes().get("diningTableId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("diningTableIdToView", diningTableIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewDiningTableDetails.xhtml");
    }

    public void createNewDiningTable(ActionEvent event) {

        try {
            
            diningTableSessionBeanLocal.generateQrCode(newDiningTable.getQrCode(), filePath+newDiningTable.getQrCode()+".png");
            Long diningTableId = diningTableSessionBeanLocal.createNewDiningTable(newDiningTable, false);
            DiningTable dt = diningTableSessionBeanLocal.retrieveDiningTableById(diningTableId);
            diningTables.add(dt);

            if (filteredDiningTables != null) {
                filteredDiningTables.add(dt);
            }

            newDiningTable = new DiningTable();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New dining table created successfully (DiningTable ID: " + dt.getDiningTableId() + ")", null));
        } catch (InputDataValidationException | DiningTableNotFoundException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new dining table: " + ex.getMessage(), null));
        }
    }

    public void doUpdateDiningTable(ActionEvent event) {
        selectedDiningTableToUpdate = (DiningTable) event.getComponent().getAttributes().get("diningTableToUpdate");
    }

    public void updateDiningTable(ActionEvent event) {
        try {
            diningTableSessionBeanLocal.generateQrCode(selectedDiningTableToUpdate.getQrCode(), filePath+selectedDiningTableToUpdate.getQrCode()+".png");
            diningTableSessionBeanLocal.updateDiningTableInformation(selectedDiningTableToUpdate);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Dining Table updated successfully", null));
        } catch (DiningTableNotFoundException ex) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating dining table: " + ex.getMessage(), null));
        } catch (Exception ex) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void updateFreezeStatus(ActionEvent event) {
        try {
            selectedDiningTableToUpdate = (DiningTable) event.getComponent().getAttributes().get("diningTableToUpdate");

            if (selectedDiningTableToUpdate.getTableStatus() == TableStatusEnum.FROZEN_ALLOCATED) {
                selectedDiningTableToUpdate.setTableStatus(TableStatusEnum.UNFROZEN_ALLOCATED);
            } else if (selectedDiningTableToUpdate.getTableStatus() == TableStatusEnum.UNFROZEN_ALLOCATED) {
                selectedDiningTableToUpdate.setTableStatus(TableStatusEnum.FROZEN_ALLOCATED);
            } else if (selectedDiningTableToUpdate.getTableStatus() == TableStatusEnum.FROZEN_OCCUPIED) {
                selectedDiningTableToUpdate.setTableStatus(TableStatusEnum.UNFROZEN_OCCUPIED);
            } else if (selectedDiningTableToUpdate.getTableStatus() == TableStatusEnum.UNFROZEN_OCCUPIED) {
                selectedDiningTableToUpdate.setTableStatus(TableStatusEnum.FROZEN_OCCUPIED);

            } else if (selectedDiningTableToUpdate.getTableStatus() == TableStatusEnum.FROZEN_UNOCCUPIED) {
                selectedDiningTableToUpdate.setTableStatus(TableStatusEnum.UNFROZEN_UNOCCUPIED);
            } else if (selectedDiningTableToUpdate.getTableStatus() == TableStatusEnum.UNFROZEN_UNOCCUPIED) {
                selectedDiningTableToUpdate.setTableStatus(TableStatusEnum.FROZEN_UNOCCUPIED);
            }

            diningTableSessionBeanLocal.updateDiningTableInformation(selectedDiningTableToUpdate);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Table [ID " + selectedDiningTableToUpdate.getDiningTableId() + "] freeze status changed successfully.", null));
        } catch (DiningTableNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating dining table freeze status: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void cleanDiningTable(ActionEvent event) {
        try {
            selectedDiningTableToUpdate = (DiningTable) event.getComponent().getAttributes().get("diningTableToUpdate");

            if (selectedDiningTableToUpdate.getTableStatus() == TableStatusEnum.FROZEN_OCCUPIED) {
                selectedDiningTableToUpdate.setTableStatus(TableStatusEnum.FROZEN_UNOCCUPIED);
            } else if (selectedDiningTableToUpdate.getTableStatus() == TableStatusEnum.UNFROZEN_OCCUPIED) {
                selectedDiningTableToUpdate.setTableStatus(TableStatusEnum.UNFROZEN_UNOCCUPIED);
            } else {
                throw new InvalidTableStatusException("Table [ID" + selectedDiningTableToUpdate.getDiningTableId() + "] is already cleaned");
            }

            diningTableSessionBeanLocal.updateDiningTableInformation(selectedDiningTableToUpdate);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Table [ID " + selectedDiningTableToUpdate.getDiningTableId() + "] status changed successfully.", null));
        } catch (InvalidTableStatusException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating dining table status: " + ex.getMessage(), null));
        } catch (DiningTableNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating dining table status: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void deleteDiningTable(ActionEvent event) {
        try {
            DiningTable diningTableToDelete = (DiningTable) event.getComponent().getAttributes().get("diningTableToDelete");
            diningTableSessionBeanLocal.deleteDiningTable(diningTableToDelete.getDiningTableId());

            diningTables.remove(diningTableToDelete);

            if (filteredDiningTables != null) {
                filteredDiningTables.remove(diningTableToDelete);
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Dining table deleted successfully", null));
        } catch (DiningTableNotFoundException | DeleteDiningTableException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting dining table: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public List<DiningTable> getDiningTables() {
        return diningTables;
    }

    public void setDiningTables(List<DiningTable> diningTables) {
        this.diningTables = diningTables;
    }

    public List<DiningTable> getFilteredDiningTables() {
        return filteredDiningTables;
    }

    public void setFilteredDiningTables(List<DiningTable> filteredDiningTables) {
        this.filteredDiningTables = filteredDiningTables;
    }

    public DiningTable getNewDiningTable() {
        return newDiningTable;
    }

    public void setNewDiningTable(DiningTable newDiningTable) {
        this.newDiningTable = newDiningTable;
    }

    public DiningTable getSelectedDiningTableToUpdate() {
        return selectedDiningTableToUpdate;
    }

    public void setSelectedDiningTableToUpdate(DiningTable selectedDiningTableToUpdate) {
        this.selectedDiningTableToUpdate = selectedDiningTableToUpdate;
    }

    public List<TableStatusEnum> getTableStatuses() {
        return tableStatuses;
    }

    public void setTableStatuses(List<TableStatusEnum> tableStatuses) {
        this.tableStatuses = tableStatuses;
    }

}
