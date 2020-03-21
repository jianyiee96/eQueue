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
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import util.exceptions.EmployeeInvalidEnteredCurrentPasswordException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UpdateEmployeeException;

@Named(value = "employeeProfilePageManagedBean")
@ViewScoped
public class EmployeeProfilePageManagedBean implements Serializable {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private Employee currentEmployee;
    private String enteredCurrentPassword;
    private String enteredNewPassword;
    private List<String> pathsToDelete;

    public EmployeeProfilePageManagedBean() {
        enteredCurrentPassword = "";
        enteredNewPassword = "";
    }

    @PostConstruct
    public void postConstruct() {
        Employee temp = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        this.currentEmployee = new Employee(temp);
        pathsToDelete = new ArrayList<>();
    }
    
    @PreDestroy
    public void preDestroy() {
        System.out.println("ENTERED PREDESTROY");
        String newPath = ((Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee")).getImagePath();
        if (pathsToDelete.contains(newPath)) {
            System.out.println("DETECTED NEW PATH IN LIST");
            System.out.println(pathsToDelete.remove(newPath));
        }
        for(String oldPath: pathsToDelete) {
            System.out.println("Deteling file " + oldPath);
            System.out.println(deteleFileInDir(oldPath));
        }
        System.out.println("EXIT PREDESTROY");
    }

    public void navigate() throws IOException {
//        currentEmployee = (Employee)event.getComponent().getAttributes().get("currentEmployee");
        FacesContext.getCurrentInstance().getExternalContext().redirect("employeeProfilePage.xhtml");
    }

    public void updatePersonalInformation() {
        try {
            
            employeeSessionBeanLocal.updateEmployee(this.currentEmployee);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", this.currentEmployee);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Employee updated successfully", null));
        } catch (EmployeeNotFoundException | UpdateEmployeeException | InputDataValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating employee: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void changePassword() {
        try {
            employeeSessionBeanLocal.updateEmployeePassword(this.currentEmployee.getUsername(), this.enteredCurrentPassword, this.enteredNewPassword);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", this.currentEmployee);

            enteredCurrentPassword = "";
            enteredNewPassword = "";

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Password has been updated successfully!", null));
        } catch (EmployeeInvalidEnteredCurrentPasswordException | EmployeeNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while changing password: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void updatePersonalProfilePhoto(FileUploadEvent event) {
        try {
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
            
            String oldPath = this.currentEmployee.getImagePath();
            if (oldPath != null && !oldPath.equals(event.getFile().getFileName()) && !pathsToDelete.contains(oldPath)) {
                pathsToDelete.add(oldPath);
                System.out.println("OLD PATH: " + oldPath);
            }
            pathsToDelete.add(event.getFile().getFileName());
            
            this.currentEmployee.setImagePath(event.getFile().getFileName());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile picture uploaded successfully", ""));
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Profile picture upload error: " + ex.getMessage(), ""));
        }
    }

    private boolean deteleFileInDir(String imagePath) {
        String newFilePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        Matcher m = Pattern.compile("eQueue").matcher(newFilePath);
        List<Integer> positions = new ArrayList<>();
        while (m.find()) {
            positions.add(m.end());
        }
        newFilePath = newFilePath.substring(0, positions.get(positions.size() - 3)) + "/eQueue-war/web/resources/images/profiles/" + imagePath;
        File file = new File(newFilePath);
        return file.delete();
    }

    public void removeProfilePhoto() {
        try {
            if(currentEmployee.getImagePath() != null) {
                deteleFileInDir(currentEmployee.getImagePath());
            }
            this.currentEmployee.setImagePath(null);

            employeeSessionBeanLocal.updateEmployee(this.currentEmployee);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", this.currentEmployee);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile picture removed successfully", ""));
        } catch (EmployeeNotFoundException | UpdateEmployeeException | InputDataValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating employee: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public void setCurrentEmployee(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }

    public String getEnteredCurrentPassword() {
        return enteredCurrentPassword;
    }

    public void setEnteredCurrentPassword(String enteredCurrentPassword) {
        this.enteredCurrentPassword = enteredCurrentPassword;
    }

    public String getEnteredNewPassword() {
        return enteredNewPassword;
    }

    public void setEnteredNewPassword(String enteredNewPassword) {
        this.enteredNewPassword = enteredNewPassword;
    }

}
