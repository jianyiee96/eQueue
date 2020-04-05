
package ws.restful;

import java.util.Set;
import javax.ws.rs.core.Application;


@javax.ws.rs.ApplicationPath("Resources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(ws.restful.CustomerResource.class);
        resources.add(ws.restful.DiningTableResource.class);
        resources.add(ws.restful.NotificationResource.class);
        resources.add(ws.restful.QueueResource.class);
        resources.add(ws.restful.StoreResource.class);
    }
    
}
