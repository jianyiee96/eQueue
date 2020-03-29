package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import util.enumeration.MenuItemAvailabilityEnum;

@Entity
public class MenuItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuItemId;

    @Column(nullable = false, unique = true, length = 5)
    @NotNull
    @Size(min = 5, max = 5)
    private String menuItemCode;

    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String menuItemName;

    @Column(nullable = true, length = 128)
    @Size(max = 128)
    private String menuItemDescription;

    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 9, fraction = 2)
    private Double menuItemPrice;

    @Column()
    private String imagePath;

    @Column(nullable = true)
    @Positive
    @Min(1)
    private Long waitingTimeMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private MenuItemAvailabilityEnum availability;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private MenuCategory menuCategory;

    public MenuItem() {

        this.availability = MenuItemAvailabilityEnum.UNAVAILABLE;

    }

    public MenuItem(String menuItemCode, String menuItemName, Double menuItemPrice, Long waitingTimeMinutes, MenuItemAvailabilityEnum availability) {

        this();
        this.menuItemCode = menuItemCode;
        this.menuItemName = menuItemName;
        this.menuItemPrice = menuItemPrice;
        this.waitingTimeMinutes = waitingTimeMinutes;
        this.availability = availability;

    }

    public MenuItem(String menuItemCode, String menuItemName, Double menuItemPrice, Long waitingTimeMinutes, MenuItemAvailabilityEnum availability, String imagePath) {

        this();
        this.menuItemCode = menuItemCode;
        this.menuItemName = menuItemName;
        this.menuItemPrice = menuItemPrice;
        this.waitingTimeMinutes = waitingTimeMinutes;
        this.availability = availability;
        this.imagePath = imagePath;

    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getMenuItemCode() {
        return menuItemCode;
    }

    public void setMenuItemCode(String menuItemCode) {
        this.menuItemCode = menuItemCode;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public String getMenuItemDescription() {
        return menuItemDescription;
    }

    public void setMenuItemDescription(String menuItemDescription) {
        this.menuItemDescription = menuItemDescription;
    }

    public Double getMenuItemPrice() {
        return menuItemPrice;
    }

    public void setMenuItemPrice(Double menuItemPrice) {
        this.menuItemPrice = menuItemPrice;
    }

    public Long getWaitingTimeMinutes() {
        return waitingTimeMinutes;
    }

    public void setWaitingTimeMinutes(Long waitingTimeMinutes) {
        this.waitingTimeMinutes = waitingTimeMinutes;
    }

    public MenuItemAvailabilityEnum getAvailability() {
        return availability;
    }

    public void setAvailability(MenuItemAvailabilityEnum availability) {
        this.availability = availability;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public MenuCategory getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(MenuCategory menuCategory) {
        if (this.menuCategory != null) {
            if (this.menuCategory.getMenuItems().contains(this)) {
                this.menuCategory.getMenuItems().remove(this);
            }
        }

        this.menuCategory = menuCategory;

        if (this.menuCategory != null) {
            if (!this.menuCategory.getMenuItems().contains(this)) {
                this.menuCategory.getMenuItems().add(this);
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (menuItemId != null ? menuItemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the menuItemId fields are not set
        if (!(object instanceof MenuItem)) {
            return false;
        }
        MenuItem other = (MenuItem) object;
        if ((this.menuItemId == null && other.menuItemId != null) || (this.menuItemId != null && !this.menuItemId.equals(other.menuItemId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.MenuItem[ id=" + menuItemId + " ]";
    }

}
