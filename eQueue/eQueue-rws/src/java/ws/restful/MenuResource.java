package ws.restful;

import ejb.session.stateless.MenuCategorySessionBeanLocal;
import ejb.session.stateless.MenuItemSessionBeanLocal;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

@Path("Menu")
public class MenuResource {

    private final SessionBeanLookup sessionBeanLookup;
    
    private final MenuCategorySessionBeanLocal menuCategorySessionBeanLocal;
    private final MenuItemSessionBeanLocal menuItemSessionBeanLocal;
    
    @Context
    private UriInfo context;

    
    public MenuResource() {
        
        sessionBeanLookup = new SessionBeanLookup();
        menuCategorySessionBeanLocal = sessionBeanLookup.lookupMenuCategorySessionBeanLocal();
        menuItemSessionBeanLocal = sessionBeanLookup.lookupMenuItemSessionBeanLocal();
        
    }

    
}
