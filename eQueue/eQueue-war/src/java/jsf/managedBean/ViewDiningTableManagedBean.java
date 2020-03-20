package jsf.managedBean;

import entity.DiningTable;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "viewDiningTableManagedBean")
@ViewScoped
public class ViewDiningTableManagedBean implements Serializable{

    private DiningTable diningTableToView;

    public ViewDiningTableManagedBean() {
        diningTableToView = new DiningTable();
    }

    @PostConstruct
    public void postConstruct() {
    }

    public DiningTable getDiningTableToView() {
        return diningTableToView;
    }

    public void setDiningTableToView(DiningTable diningTableToView) {
        this.diningTableToView = diningTableToView;
    }

}
