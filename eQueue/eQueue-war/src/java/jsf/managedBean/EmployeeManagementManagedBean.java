package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.EmployeeUsernameExistException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Named(value = "employeeManagementManagedBean")
@ViewScoped
public class EmployeeManagementManagedBean implements Serializable {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private List<Employee> employees;
    private List<Employee> filteredEmployees;

    private Employee employeeToCreate;
    private Employee employeeToView;

    public EmployeeManagementManagedBean() {
        employeeToCreate = new Employee();
        employeeToView = new Employee();
    }

    @PostConstruct
    public void postConstruct() {
        employees = employeeSessionBeanLocal.retrieveAllEmployees();
    }

    public void createNewEmployee() {
        try {
            Long employeeId = employeeSessionBeanLocal.createNewEmployee(employeeToCreate);
            employeeToCreate = employeeSessionBeanLocal.retrieveEmployeeById(employeeId);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New employee created successfully (Employee ID: " + employeeToCreate.getEmployeeId() + ")", null));

            postConstruct();

            employeeToCreate = new Employee();
        } catch (EmployeeUsernameExistException | EmployeeNotFoundException | InputDataValidationException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new employee: " + ex.getMessage(), null));
        }
    }


    public void handleFileUpload(FileUploadEvent event) {
        try {
//            String newFilePath = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("alternatedocroot_1") + System.getProperty("file.separator") + event.getFile().getFileName();

            String newFilePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            Matcher m = Pattern.compile("eQueue").matcher(newFilePath);
            List<Integer> positions = new ArrayList<>();
            while (m.find()) {
                positions.add(m.end());
            }
            newFilePath = newFilePath.substring(0, positions.get(positions.size() - 3)) + "/eQueue-war/web/resources/images/profiles/" + event.getFile().getFileName();

            File file = new File(newFilePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            int a;
            int BUFFER_SIZE = 8192;
            byte[] buffer = new byte[BUFFER_SIZE];

            InputStream inputStream = event.getFile().getInputstream();

            while (true) {
                a = inputStream.read(buffer);

                if (a < 0) {
                    break;
                }

                fileOutputStream.write(buffer, 0, a);
                fileOutputStream.flush();
            }

            fileOutputStream.close();
            inputStream.close();

            this.employeeToCreate.setImagePath(event.getFile().getFileName());
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "File uploaded successfully", ""));
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File upload error: " + ex.getMessage(), ""));
        }
    }

    public Employee getEmployeeToCreate() {
        return employeeToCreate;
    }

    public void setEmployeeToCreate(Employee employeeToCreate) {
        this.employeeToCreate = employeeToCreate;
    }

    public Employee getEmployeeToView() {
        return employeeToView;
    }

    public void setEmployeeToView(Employee employeeToView) {
        this.employeeToView = employeeToView;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Employee> getFilteredEmployees() {
        return filteredEmployees;
    }

    public void setFilteredEmployees(List<Employee> filteredEmployees) {
        this.filteredEmployees = filteredEmployees;
    }
}
