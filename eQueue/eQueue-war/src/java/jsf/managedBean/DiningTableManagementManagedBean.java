package jsf.managedBean;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.DiningTableSessionBeanLocal;
import entity.Customer;
import entity.CustomerOrder;
import entity.DiningTable;
import entity.OrderLineItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.enumeration.OrderStatusEnum;
import util.enumeration.TableStatusEnum;
import util.exceptions.CustomerNotFoundException;
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
    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @Inject
    private ViewDiningTableManagedBean viewDiningTableManagedBean;

    private List<DiningTable> diningTables;
    private List<DiningTable> filteredDiningTables;

    private DiningTable newDiningTable;

    private DiningTable selectedDiningTableToUpdate;

    private List<TableStatusEnum> tableStatuses = new ArrayList();

    private Customer selectedCustomer;
    private DiningTable selectedDiningTable;
    private List<CustomerOrder> selectedCustomerActiveOrders;

    public DiningTableManagementManagedBean() {
        newDiningTable = new DiningTable();
        selectedDiningTableToUpdate = new DiningTable();
        selectedCustomer = null;
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
        diningTables.add(new DiningTable());
        selectedCustomer = null;

    }

    public void refresh() {
        diningTables = diningTableSessionBeanLocal.retrieveAllTables();
        diningTables.add(new DiningTable());

        try {
            selectedCustomer = customerSessionBeanLocal.retrieveCustomerById(selectedCustomer.getCustomerId());
            this.selectedCustomerActiveOrders = new ArrayList<>();

            if (selectedCustomer != null) {
                for (CustomerOrder c : this.selectedCustomer.getCustomerOrders()) {

                    if (c.getOrderDate().after(selectedDiningTable.getSeatedTime())) {
                        this.selectedCustomerActiveOrders.add(c);
                    }
                }

            }
        } catch (CustomerNotFoundException ex) {
            System.out.println("Unexpected Error.");
        }
    }

    public void viewDiningTableDetails(ActionEvent event) throws IOException {
        Long diningTableIdToView = (Long) event.getComponent().getAttributes().get("diningTableId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("diningTableIdToView", diningTableIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewDiningTableDetails.xhtml");
    }

    public void createNewDiningTable(ActionEvent event) {

        try {
            Long diningTableId = diningTableSessionBeanLocal.createNewDiningTable(newDiningTable);
            DiningTable dt = diningTableSessionBeanLocal.retrieveDiningTableById(diningTableId);
            diningTables.add(diningTables.size() - 1, dt);

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
            diningTableSessionBeanLocal.updateDiningTableInformation(selectedDiningTableToUpdate);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Dining Table updated successfully", null));
        } catch (DiningTableNotFoundException ex) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating dining table: " + ex.getMessage(), null));
        } catch (Exception ex) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Please try again", null));
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

    public void cleanDiningTable() {
        try {

            if (selectedDiningTable.getTableStatus() != TableStatusEnum.FROZEN_OCCUPIED && selectedDiningTable.getTableStatus() != TableStatusEnum.UNFROZEN_OCCUPIED) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Table [ID" + selectedDiningTable.getDiningTableId() + "] is not occupied by any customer", null));
                throw new InvalidTableStatusException("Table [ID" + selectedDiningTable.getDiningTableId() + "] is not occupied by any customer");
            }

            for (CustomerOrder c : selectedCustomerActiveOrders) {

                if (!c.getIsCompleted()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to clear table, please check if all orders are paid and completed.", null));
                    return;
                }

                if (c.getStatus() == OrderStatusEnum.UNPAID) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to clear table, please check if all orders are paid and completed.", null));
                    return;
                }

            }

            diningTableSessionBeanLocal.removeCustomerTableRelationship(selectedDiningTable.getCustomer().getCustomerId());
            postConstruct();
            refresh();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Table [ID " + selectedDiningTable.getDiningTableId() + "] successfully cleared.", null));

        } catch (InvalidTableStatusException ex) {
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

    public void viewDetails(ActionEvent event) {

        this.selectedDiningTable = (DiningTable) event.getComponent().getAttributes().get("diningTableToView");

        this.selectedCustomer = selectedDiningTable.getCustomer();
        this.selectedCustomerActiveOrders = new ArrayList<>();

        System.out.println("Table id:" + selectedDiningTable.getDiningTableId());
        if (selectedCustomer != null) {
            for (CustomerOrder c : this.selectedCustomer.getCustomerOrders()) {

                if ((c.getOrderDate() != null && selectedDiningTable.getSeatedTime() != null) &&c.getOrderDate().after(selectedDiningTable.getSeatedTime())) {
                    this.selectedCustomerActiveOrders.add(c);
                }
            }

        }

    }

    public String dateDiff(Date date) {

        Date d1 = date;
        Date d2 = new Date();

        //in milliseconds
        long diff = d2.getTime() - d1.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;

        String seconds = (diffSeconds < 10) ? "0" + diffSeconds : "" + diffSeconds;
        String minutes = (diffMinutes < 10) ? "0" + diffMinutes : "" + diffMinutes;
        String hours = (diffHours < 10) ? "0" + diffHours : "" + diffHours;

        return hours + ":" + minutes + ":" + seconds;

    }

    public String statusOf(TableStatusEnum status) {

        if (status == TableStatusEnum.FROZEN_ALLOCATED || status == TableStatusEnum.UNFROZEN_ALLOCATED) {
            return "Allocated";
        } else if (status == TableStatusEnum.FROZEN_OCCUPIED || status == TableStatusEnum.UNFROZEN_OCCUPIED) {
            return "Occupied";
        } else if (status == TableStatusEnum.FROZEN_UNOCCUPIED || status == TableStatusEnum.UNFROZEN_UNOCCUPIED) {
            return "Unoccupied";
        }

        return "Unknown";
    }

    public String frozenStatusOf(TableStatusEnum status) {

        if (status == TableStatusEnum.FROZEN_ALLOCATED || status == TableStatusEnum.FROZEN_OCCUPIED || status == TableStatusEnum.FROZEN_UNOCCUPIED) {
            return "Frozen";
        } else if (status == TableStatusEnum.UNFROZEN_ALLOCATED || status == TableStatusEnum.UNFROZEN_OCCUPIED || status == TableStatusEnum.UNFROZEN_UNOCCUPIED) {
            return "Unfrozen";
        }
        return "Unknown";
    }

    public Boolean isFrozen(TableStatusEnum status) {

        if (status == TableStatusEnum.FROZEN_ALLOCATED || status == TableStatusEnum.FROZEN_OCCUPIED || status == TableStatusEnum.FROZEN_UNOCCUPIED) {
            return true;
        } else if (status == TableStatusEnum.UNFROZEN_ALLOCATED || status == TableStatusEnum.UNFROZEN_OCCUPIED || status == TableStatusEnum.UNFROZEN_UNOCCUPIED) {
            return false;
        }
        return null;
    }

    public void setViewDiningTableManagedBean(ViewDiningTableManagedBean viewDiningTableManagedBean) {
        this.viewDiningTableManagedBean = viewDiningTableManagedBean;
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

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public DiningTable getSelectedDiningTable() {
        return selectedDiningTable;
    }

    public void setSelectedDiningTable(DiningTable selectedDiningTable) {
        this.selectedDiningTable = selectedDiningTable;
    }

    public List<CustomerOrder> getSelectedCustomerActiveOrders() {
        return selectedCustomerActiveOrders;
    }

    public void setSelectedCustomerActiveOrders(List<CustomerOrder> selectedCustomerActiveOrders) {
        this.selectedCustomerActiveOrders = selectedCustomerActiveOrders;
    }

}
