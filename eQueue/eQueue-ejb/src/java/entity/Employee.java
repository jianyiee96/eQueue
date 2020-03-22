package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.EmployeeRoleEnum;
import util.security.CryptographicHelper;

@Entity
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Column(nullable = false, length = 64)
    @NotNull(message = "First Name must be between length 2 to 64")
    @Size(min = 2, max = 64, message = "First Name must be between length 2 to 64")
    private String firstName;

    @Column(nullable = false, length = 64)
    @NotNull(message = "Last Name must be between length 2 to 64")
    @Size(min = 2, max = 64, message = "Last Name must be between length 2 to 64")
    private String lastName;

    @Column(nullable = false, unique = true, length = 64)
    @NotNull(message = "Proper email with length no more than 64 must be provided")
    @Size(max = 64, message = "Proper email with length no more than 64 must be provided")
    @Email(message = "Proper email with length no more than 64 must be provided")
    private String email;

    @Column(nullable = false, unique = true, length = 64)
    @NotNull(message = "Username must be between length 4 to 64")
    @Size(min = 4, max = 64, message = "Username must be between length 4 to 64")
    private String username;

    @Column(columnDefinition = "CHAR(64) NOT NULL")
    @NotNull(message = "Password must be between length 4 to 64")
    @Size(min = 4, max = 64, message = "Password must be between length 4 to 64")
    private String password;

    @Column()
    private String imagePath;

    @Column(columnDefinition = "CHAR(64) NOT NULL")
    @NotNull
    private String salt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Role must be provided")
    private EmployeeRoleEnum employeeRole;

    @OneToMany(mappedBy = "employee")
    private List<PaymentTransaction> paymentTransactions;

    public Employee() {
        this.salt = CryptographicHelper.getInstance().generateRandomString(32);
        this.paymentTransactions = new ArrayList<>();
    }

    public Employee(String firstName, String lastName, String email, String username, String password, EmployeeRoleEnum employeeRole, String imagePath) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        setPassword(password);
        this.employeeRole = employeeRole;
        this.imagePath = imagePath;
    }
    
    public Employee(Employee another) {
        this();
        this.employeeId = another.employeeId;
        this.firstName = another.firstName;
        this.lastName = another.lastName;
        this.email = another.email;
        this.username = another.username;
        this.password = another.password;
        this.employeeRole = another.employeeRole;
        this.imagePath = another.imagePath;
    }
    
    public Employee(String firstName, String lastName, String email, String username, String password, EmployeeRoleEnum employeeRole) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        setPassword(password);
        this.employeeRole = employeeRole;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null) {
            this.password = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + this.salt));
        } else {
            this.password = null;
        }
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public EmployeeRoleEnum getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(EmployeeRoleEnum employeeRole) {
        this.employeeRole = employeeRole;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<PaymentTransaction> getPaymentTransactions() {
        return paymentTransactions;
    }

    public void setPaymentTransactions(List<PaymentTransaction> paymentTransactions) {
        this.paymentTransactions = paymentTransactions;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (employeeId != null ? employeeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the employeeId fields are not set
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee other = (Employee) object;
        if ((this.employeeId == null && other.employeeId != null) || (this.employeeId != null && !this.employeeId.equals(other.employeeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Employee[ id=" + employeeId + " ]";
    }

}
