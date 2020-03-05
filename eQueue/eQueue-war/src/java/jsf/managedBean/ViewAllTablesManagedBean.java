
package jsf.managedBean;

import ejb.session.stateless.DiningTableSessionBeanLocal;
import entity.DiningTable;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;


@Named(value = "viewAllTablesManagedBean")
@RequestScoped
public class ViewAllTablesManagedBean {

    @EJB
    private DiningTableSessionBeanLocal diningTableSessionBeanLocal;
    
    private List<DiningTable> diningTables;

    
    public ViewAllTablesManagedBean() {
    }
    
    @PostConstruct
    public void postConstruct() {
        diningTables = diningTableSessionBeanLocal.retrieveAllTables();
    }
    
    public void viewDiningTableDetails(ActionEvent event) throws IOException {
        Long diningTableIdToView = (Long)event.getComponent().getAttributes().get("diningTableId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("diningTableIdToView", diningTableIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewDiningTableDetails.xhtml");
    }

    public List<DiningTable> getDiningTables() {
        return diningTables;
    }

    public void setDiningTables(List<DiningTable> diningTables) {
        this.diningTables = diningTables;
    }
}
