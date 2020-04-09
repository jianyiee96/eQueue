/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import ejb.session.stateless.ShoppingCartSessionBeanLocal;
import entity.Notification;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveCustomerNotificationsRsp;


@Path("ShoppingCart")
public class ShoppingCartResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;
    
    private final ShoppingCartSessionBeanLocal shoppingCartSessionBeanLocal; 
    
    public ShoppingCartResource() {
        sessionBeanLookup = new SessionBeanLookup();
        
        this.shoppingCartSessionBeanLocal = sessionBeanLookup.lookupShoppingCartSessionBeanLocal();
    }

//    @Path("saveCustomerShoppingCart")
//    @PUT
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response saveCustomerShoppingCart(SaveShoppingCartReq SaveShoppingCartReq) {
//        try {
//
//            List<Notification> notifications = notificationSessionBeanLocal.retrieveNotificationsByCustomerId(Long.parseLong(customerId));
//            notifications.forEach(n -> n.setCustomer(null));
//            return Response.status(Response.Status.OK).entity(new RetrieveCustomerNotificationsRsp(notifications)).build();
//
//        } catch (Exception ex) {
//            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
//
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
//        }
//    }
}
