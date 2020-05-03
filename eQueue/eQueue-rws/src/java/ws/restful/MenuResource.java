package ws.restful;

import ejb.session.stateless.MenuCategorySessionBeanLocal;
import ejb.session.stateless.MenuItemSessionBeanLocal;
import entity.MenuCategory;
import entity.MenuItem;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllCategoryRsp;
import ws.datamodel.RetrieveAllMenuItemRsp;
import ws.datamodel.RetrievePopularMenuItemRsp;

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

    @Path("retrieveAllMenuItem")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllMenuItem() {
        try {

            List<MenuItem> menuItems = menuItemSessionBeanLocal.retrieveAllMenuItems();
            for(MenuItem m : menuItems) {
                m.setMenuCategory(null);
            }
            return Response.status(Response.Status.OK).entity(new RetrieveAllMenuItemRsp(menuItems)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
    
    @Path("retrievePopularMenuItem")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrievePopularMenuItem(@QueryParam("maxItem") String maxItem) {
        try {

            List<MenuItem> menuItems = menuItemSessionBeanLocal.retrievePopularMenuItems(Integer.parseInt(maxItem));
            for(MenuItem m : menuItems) {
                m.setMenuCategory(null);
            }
            return Response.status(Response.Status.OK).entity(new RetrievePopularMenuItemRsp(menuItems)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
    
    @Path("retrieveAllMenuItemByCategory")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllMenuItemByCategory(@QueryParam("menuCategoryId") String menuCatagoryId) {
        try {

            List<MenuItem> menuItems = menuItemSessionBeanLocal.retrieveAllMenuItemsByCategory(Long.parseLong(menuCatagoryId));
            for(MenuItem m : menuItems) {
                m.setMenuCategory(null);
            }
            return Response.status(Response.Status.OK).entity(new RetrieveAllMenuItemRsp(menuItems)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
    
    @Path("retrieveAllTopMenuCategory")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllTopMenuCategory() {
        try {

            List<MenuCategory> menuCategories = menuCategorySessionBeanLocal.retrieveAllRootMenuCategories();
            for(MenuCategory m : menuCategories) {
                m.setParentMenuCategory(null);
                m.setSubMenuCategories(null);
                m.setMenuItems(null);}
            return Response.status(Response.Status.OK).entity(new RetrieveAllCategoryRsp(menuCategories)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrieveAllCategoryByParent")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllCategoryByParentCategory(@QueryParam("menuCategoryId") String menuCatagoryId) {
        try {

            List<MenuCategory> menuCategories = menuCategorySessionBeanLocal.retrieveAllMenuCategoriesByParentCategory(Long.parseLong(menuCatagoryId));
            for(MenuCategory m : menuCategories) {
                m.setParentMenuCategory(null);
                m.setSubMenuCategories(null);
                m.setMenuItems(null);}
            return Response.status(Response.Status.OK).entity(new RetrieveAllCategoryRsp(menuCategories)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
    

}
