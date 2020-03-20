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
import util.exceptions.DeleteEmployeeException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.EmployeeUsernameExistException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;
import util.exceptions.UpdateEmployeeException;

@Named(value = "employeeManagementManagedBean")
@ViewScoped
public class EmployeeManagementManagedBean implements Serializable {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private List<Employee> employees;
    private List<Employee> filteredEmployees;

    private Employee employeeToCreate;
    private Employee employeeToView;
    private Employee employeeToUpdate;
    private Employee employeeToDelete;

    public EmployeeManagementManagedBean() {
        employeeToCreate = new Employee();
        employeeToView = new Employee();
        employeeToUpdate = new Employee();
        employeeToDelete = new Employee();
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

            employees.add(employeeToCreate);
            postConstruct();

            employeeToCreate = new Employee();
        } catch (EmployeeUsernameExistException | EmployeeNotFoundException | InputDataValidationException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new employee: " + ex.getMessage(), null));
        }
    }

    public void createProfilePhoto(FileUploadEvent event) {
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

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile picture uploaded successfully", ""));
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Profile picture upload error: " + ex.getMessage(), ""));
        }
    }

    public void updateEmployee() {
        try {
            employeeSessionBeanLocal.updateEmployee(employeeToUpdate);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Employee updated successfully", null));

            postConstruct();

            employeeToUpdate = new Employee();
        } catch (EmployeeNotFoundException | UpdateEmployeeException | InputDataValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating employee: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void updateProfilePhoto(FileUploadEvent event) {
        try {
//            String newFilePath = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("alternatedocroot_1") + System.getProperty("file.separator") + event.getFile().getFileName();

            String newFilePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            Matcher m = Pattern.compile("eQueue").matcher(newFilePath);
            List<Integer> positions = new ArrayList<>();
            while (m.find()) {
                positions.add(m.end());
            }
            newFilePath = newFilePath.substring(0, positions.get(positions.size() - 3)) + "/eQueue-war/web/resources/images/profiles/" + event.getFile().getFileName();

//            System.out.println("newFilePath --> " + newFilePath);
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

            this.employeeToUpdate.setImagePath(event.getFile().getFileName());

//            System.out.println(this.employeeToUpdate.getImagePath());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile picture uploaded successfully", ""));
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Profile picture upload error: " + ex.getMessage(), ""));
        }
    }

    public void removeProfilePhoto() {
        try {
            this.employeeToUpdate.setImagePath(null);

            employeeSessionBeanLocal.updateEmployee(employeeToUpdate);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile picture removed successfully", ""));

//            employeeToUpdate = new Employee();
        } catch (EmployeeNotFoundException | UpdateEmployeeException | InputDataValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating employee: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void deleteEmployee() {
        try {
            employeeSessionBeanLocal.deleteEmployee(employeeToDelete.getEmployeeId());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Employee deleted successfully", null));

            postConstruct();

            employeeToDelete = new Employee();
        } catch (EmployeeNotFoundException | DeleteEmployeeException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting employee: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
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

    public Employee getEmployeeToUpdate() {
        return employeeToUpdate;
    }

    public void setEmployeeToUpdate(Employee employeeToUpdate) {
        this.employeeToUpdate = employeeToUpdate;
    }

    public Employee getEmployeeToDelete() {
        return employeeToDelete;
    }

    public void setEmployeeToDelete(Employee employeeToDelete) {
        this.employeeToDelete = employeeToDelete;
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
