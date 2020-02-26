/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import javax.inject.Named;
import javax.enterprise.context.Dependent;

/**
 *
 * @author User
 */
@Named(value = "testManagedBean")
@Dependent
public class testManagedBean {

    /**
     * Creates a new instance of testManagedBean
     */
    public testManagedBean() {
    }
    
}
