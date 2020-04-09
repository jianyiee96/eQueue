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
import util.exceptions.CustomerNotFoundException;
import util.exceptions.MenuItemNotFoundException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveCustomerNotificationsRsp;
import ws.datamodel.SaveShoppingCartReq;

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

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveShoppingCart(SaveShoppingCartReq saveShoppingCartReq) {

        if (saveShoppingCartReq != null) {

            try {
                this.shoppingCartSessionBeanLocal.saveShoppingCart(saveShoppingCartReq.getCustomerId(), saveShoppingCartReq.getShoppingCart());
                return Response.status(Response.Status.OK).entity(null).build();
            } catch (CustomerNotFoundException | MenuItemNotFoundException ex) {

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorRsp("Failed to save shopping cart.")).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorRsp("Supplied null value.")).build();
        }

    }
}
