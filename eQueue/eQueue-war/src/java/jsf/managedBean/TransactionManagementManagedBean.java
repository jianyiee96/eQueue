package jsf.managedBean;

import ejb.session.stateless.DiningTableSessionBeanLocal;
import entity.DiningTable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.TableStatusEnum;

@Named(value = "transactionManagementManagedBean")
@ViewScoped
public class TransactionManagementManagedBean implements Serializable {

    @EJB(name = "DiningTableSessionBeanLocal")
    private DiningTableSessionBeanLocal diningTableSessionBeanLocal;

    private List<DiningTable> diningTables;

    public TransactionManagementManagedBean() {
        diningTables = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        this.diningTables = diningTableSessionBeanLocal.retrieveAllTables();
    }

    public List<DiningTable> getDiningTables() {
        return diningTables;
    }

    public void setDiningTables(List<DiningTable> diningTables) {
        this.diningTables = diningTables;
    }

    public String getDiningTableStatus(TableStatusEnum status) {

        if (status == TableStatusEnum.FROZEN_ALLOCATED || status == TableStatusEnum.UNFROZEN_ALLOCATED) {
            return "Allocated";
        } else if (status == TableStatusEnum.FROZEN_OCCUPIED || status == TableStatusEnum.UNFROZEN_OCCUPIED) {
            return "Occupied";
        } else if (status == TableStatusEnum.FROZEN_UNOCCUPIED || status == TableStatusEnum.UNFROZEN_UNOCCUPIED) {
            return "Unoccupied";
        }

        return "Unknown";
    }

}
