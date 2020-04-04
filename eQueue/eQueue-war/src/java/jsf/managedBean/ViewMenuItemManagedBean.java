package jsf.managedBean;

import entity.MenuItem;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "viewMenuItemManagedBean")
@ViewScoped
public class ViewMenuItemManagedBean implements Serializable {

    private MenuItem menuItemToView;
    private String viewMenuItemCurrentPhotoContents;

    public ViewMenuItemManagedBean() {
        menuItemToView = new MenuItem();
    }

    @PostConstruct
    public void postConstruct() {
    }

    public String getViewMenuItemCurrentPhotoContents() {
        return viewMenuItemCurrentPhotoContents;
    }

    public MenuItem getMenuItemToView() {
        return menuItemToView;
    }

    public void setMenuItemToView(MenuItem menuItemToView) {
        this.menuItemToView = menuItemToView;
        if (menuItemToView.getImagePath() != null) {
            try {
                viewMenuItemCurrentPhotoContents = getImageContentsAsBase64(Files.readAllBytes(getFileInDir(menuItemToView.getImagePath()).toPath()));
            } catch (Exception ex) {
                System.out.println("FILE DOES NOT EXIST! ===========> " + menuItemToView.getImagePath());
            }
        }
    }

    public String getImageContentsAsBase64(byte[] contents) {
        return Base64.getEncoder().encodeToString(contents);
    }

    private File getFileInDir(String imagePath) {
        String newFilePath = getWorkingDirPath() + imagePath;
        File file = new File(newFilePath);
        return file;
    }

    private String getWorkingDirPath() {
        String newFilePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        Matcher m = Pattern.compile("eQueue").matcher(newFilePath);
        List<Integer> positions = new ArrayList<>();
        while (m.find()) {
            positions.add(m.end());
        }

        return newFilePath.substring(0, positions.get(positions.size() - 3)) + "/eQueue-war/web/resources/images/food/";
    }

}
