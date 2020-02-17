package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class MenuCategory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuCaregoryId;

    @Column(nullable = false, unique = true, length = 32)
    @NotNull
    @Size(max = 32)
    private String categoryName;

    @OneToMany(mappedBy = "parentMenuCategory")
    private List<MenuCategory> subMenuCategories;

    @ManyToOne(optional = true)
    private MenuCategory parentMenuCategory;

    @OneToMany(mappedBy = "menuCategory")
    private List<MenuItem> menuItems;

    public MenuCategory() {
        this.subMenuCategories = new ArrayList<>();
        this.menuItems = new ArrayList<>();
    }
    
    public MenuCategory(String categoryName){
        this();
        
        this.categoryName = categoryName;
    }
    
    public Long getMenuCaregoryId() {
        return menuCaregoryId;
    }

    public void setMenuCaregoryId(Long menuCaregoryId) {
        this.menuCaregoryId = menuCaregoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<MenuCategory> getSubMenuCategories() {
        return subMenuCategories;
    }

    public void setSubMenuCategories(List<MenuCategory> subMenuCategories) {
        this.subMenuCategories = subMenuCategories;
    }

    public MenuCategory getParentMenuCategory() {
        return parentMenuCategory;
    }

    public void setParentMenuCategory(MenuCategory parentMenuCategory) {
        if (this.parentMenuCategory != null) {
            if (this.parentMenuCategory.getSubMenuCategories().contains(this)) {
                this.parentMenuCategory.getSubMenuCategories().remove(this);
            }
        }

        this.parentMenuCategory = parentMenuCategory;

        if (this.parentMenuCategory != null) {
            if (!this.parentMenuCategory.getSubMenuCategories().contains(this)) {
                this.parentMenuCategory.getSubMenuCategories().add(this);
            }
        }
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (menuCaregoryId != null ? menuCaregoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the menuCaregoryId fields are not set
        if (!(object instanceof MenuCategory)) {
            return false;
        }
        MenuCategory other = (MenuCategory) object;
        if ((this.menuCaregoryId == null && other.menuCaregoryId != null) || (this.menuCaregoryId != null && !this.menuCaregoryId.equals(other.menuCaregoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.MenuCategory[ id=" + menuCaregoryId + " ]";
    }

}
