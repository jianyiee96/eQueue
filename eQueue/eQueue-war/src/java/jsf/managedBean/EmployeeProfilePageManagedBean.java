package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.UploadedFile;
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

    private Boolean deleteOldPhoto;
    private Boolean cropOldPhoto;
    private CroppedImage croppedImage;
    private UploadedFile picturePreview;
    private String imageContents;
    private String imagePreviewContents;

    public EmployeeProfilePageManagedBean() {
        enteredCurrentPassword = "";
        enteredNewPassword = "";
    }

    @PostConstruct
    public void postConstruct() {
        Employee temp = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        this.currentEmployee = new Employee(temp);
        if (this.currentEmployee.getImagePath() != null) {
            try {
                imageContents = getImageContentsAsBase64(Files.readAllBytes(getFileInDir(this.currentEmployee.getImagePath()).toPath()));
                //System.out.println("FILE: " + this.currentEmployee.getImagePath() + " successfully retrieved!");
            } catch (Exception ex) {
                //System.out.println("FILE DOES NOT EXIST! ===========> " + this.currentEmployee.getImagePath());
            }
        }
        this.deleteOldPhoto = false;
        this.cropOldPhoto = false;
    }

    public void updatePersonalInformation() {
        try {
            String oldPath = this.currentEmployee.getImagePath();

            // Handling the picture upload of preview
            if (picturePreview != null) {
                System.out.println("Uploading picture preview.....");
                String fileName = this.currentEmployee.getEmployeeId() + " - " + picturePreview.getFileName();
                createFileInDirWithByte(picturePreview.getContents(), fileName);

                this.currentEmployee.setImagePath(fileName);
                if (oldPath != null && !oldPath.equals(fileName)) {
                    deteleFileInDir(oldPath);
                } else if (oldPath == null) {
                    System.out.println("Employee " + this.currentEmployee.getFirstName() + " does not have an old photo...");
                } else if (oldPath.equals(fileName)) {
                    System.out.println("File: " + oldPath + " was overriden!");
                }
                this.imageContents = imagePreviewContents;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile picture uploaded successfully", ""));
            } else if (cropOldPhoto && croppedImage != null) {
                System.out.println("Uploading cropped image.....");
                createFileInDirWithByte(croppedImage.getBytes(), oldPath);
                this.imageContents = getImageContentsAsBase64(croppedImage.getBytes());
            }

            if (deleteOldPhoto) {
                if (oldPath != null) {
                    System.out.println("Deleting Old Photo.....");
                    if (picturePreview != null) {
                        System.out.println("=====> Picture deletion already handled by picture preview");
                    } else if (picturePreview == null) {
                        System.out.println("=====> No picture preview detected");
                        deteleFileInDir(oldPath);
                        this.currentEmployee.setImagePath(null);
                        imageContents = null;
                    }
                } else {
                    System.out.println("Employee " + this.currentEmployee.getFirstName() + " does not have an old photo...");
                }
            }

            employeeSessionBeanLocal.updateEmployee(this.currentEmployee);
            this.picturePreview = null;
            this.imagePreviewContents = null;
            this.deleteOldPhoto = false;
            this.cropOldPhoto = false;
            this.croppedImage = null;
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", this.currentEmployee);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Employee updated successfully", null));
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Profile picture upload error: " + ex.getMessage(), ""));
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
        this.picturePreview = event.getFile();
        this.imagePreviewContents = getImageContentsAsBase64(picturePreview.getContents());
    }

    private void createFileInDirWithByte(byte[] bytes, String fileName) throws IOException {
        FileImageOutputStream imageOutput;
        String newFilePath = getWorkingDirPath() + fileName;

        File newFile = new File(newFilePath);

        imageOutput = new FileImageOutputStream(newFile);
        imageOutput.write(bytes, 0, bytes.length);
        imageOutput.close();
        System.out.println("File: " + fileName + " created successfully!");
    }

    public String getImageContentsAsBase64(byte[] contents) {
        return Base64.getEncoder().encodeToString(contents);
    }

    private boolean deteleFileInDir(String imagePath) {
        String filePath = getWorkingDirPath() + imagePath;
        File file = new File(filePath);
        Boolean fileDeletedSuccessfully = file.delete();
        if (fileDeletedSuccessfully) {
            System.out.println("File: " + imagePath + " was successfully deleted!");
        } else {
            System.out.println("File: " + imagePath + " was not deleted!");
        }
        return fileDeletedSuccessfully;
    }

    private File getFileInDir(String imagePath) {
        String filePath = getWorkingDirPath() + imagePath;
        File file = new File(filePath);
        return file;
    }

    private String getWorkingDirPath() {
        String newFilePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        Matcher m = Pattern.compile("eQueue").matcher(newFilePath);
        List<Integer> positions = new ArrayList<>();
        while (m.find()) {
            positions.add(m.end());
        }
        
        return newFilePath.substring(0, positions.get(positions.size() - 3)) + "/eQueue-war/web/resources/images/profiles/";
    }

    public void removeProfilePhoto() {
        this.deleteOldPhoto = true;
        System.out.println("=====> Set to remove Profile Photo!");
    }

    public void cropProfilePhoto() {
        this.cropOldPhoto = true;
        System.out.println("=====> Set to crop Profile Photo!");
    }

    public CroppedImage getCroppedImage() {
        return croppedImage;
    }

    public void setCroppedImage(CroppedImage croppedImage) {
        this.croppedImage = croppedImage;
    }

    public Boolean getCropOldPhoto() {
        return cropOldPhoto;
    }

    public void setCropOldPhoto(Boolean cropOldPhoto) {
        this.cropOldPhoto = cropOldPhoto;
    }

    public Boolean getDeleteOldPhoto() {
        return deleteOldPhoto;
    }

    public String getImageContents() {
        return imageContents;
    }

    public String getImagePreviewContents() {
        return imagePreviewContents;
    }

    public UploadedFile getPicturePreview() {
        return picturePreview;
    }

    public void setPicturePreview(UploadedFile picturePreview) {
        this.picturePreview = picturePreview;
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
