package ws.restful;

import ejb.session.stateless.CustomerOrderSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.Customer;
import entity.CustomerOrder;
import entity.Notification;
import entity.OrderLineItem;
import java.util.ArrayList;
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
import util.enumeration.TableStatusEnum;
import util.exceptions.CreateNewCustomerOrderException;
import util.exceptions.CreateNewOrderLineItemException;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.EmptyCartException;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuItemNotFoundException;
import util.exceptions.OrderLineItemNotFoundException;
import util.exceptions.PriceMismatchException;
import util.exceptions.UnknownPersistenceException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveCustomerNotificationsRsp;
import ws.datamodel.RetrieveCustomerOrdersRsp;

@Path("CustomerOrder")
public class CustomerOrderResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final CustomerSessionBeanLocal customerSessionBeanLocal;
    private final CustomerOrderSessionBeanLocal customerOrderSessionBeanLocal;

    public CustomerOrderResource() {
        sessionBeanLookup = new SessionBeanLookup();
        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
        customerOrderSessionBeanLocal = sessionBeanLookup.lookupCustomerOrderSessionBeanLocal();
    }

    @Path("retrieveCustomerOrders")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerOrders(@QueryParam("customerId") String customerId) {
        try {

            List<CustomerOrder> customerOrders = customerOrderSessionBeanLocal.retrieveAllCustomerOrdersByCustomerId(Long.parseLong(customerId));

            List<CustomerOrder> result = new ArrayList<>();

            for(CustomerOrder customerOrder : customerOrders) {
                
                CustomerOrder parsedOrder = new CustomerOrder();
                
                parsedOrder.setIsCompleted(customerOrder.getIsCompleted());
                parsedOrder.setOrderDate(customerOrder.getOrderDate());
                parsedOrder.setOrderId(customerOrder.getOrderId());
                parsedOrder.setStatus(customerOrder.getStatus());
                parsedOrder.setTotalAmount(customerOrder.getTotalAmount());
                
                result.add(parsedOrder);
                
            }

            return Response.status(Response.Status.OK).entity(new RetrieveCustomerOrdersRsp(result)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("submitCustomerOrder")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitCustomerOrder(@QueryParam("customerId") String customerId) {

        try {

            Customer customer = customerSessionBeanLocal.retrieveCustomerById(Long.parseLong(customerId));

            //Verify that customer is 
            if (customer.getAllocatedDiningTable() != null
                    && (customer.getAllocatedDiningTable().getTableStatus() == TableStatusEnum.FROZEN_OCCUPIED
                    || customer.getAllocatedDiningTable().getTableStatus() == TableStatusEnum.UNFROZEN_OCCUPIED)) {

                customerOrderSessionBeanLocal.processOrderFromCart(Long.parseLong(customerId));
                System.out.println("Sucessful: ordering created.");
                return Response.status(Response.Status.OK).entity(null).build();

            } else {
                ErrorRsp errorRsp = new ErrorRsp("Invalid table allocation.");
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            }

        } catch (CustomerNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp("Customer not found.");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (EmptyCartException ex) {
            ErrorRsp errorRsp = new ErrorRsp("Empty Cart.");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (CreateNewCustomerOrderException | CreateNewOrderLineItemException | MenuItemNotFoundException | NumberFormatException | OrderLineItemNotFoundException | UnknownPersistenceException ex) {
            ErrorRsp errorRsp = new ErrorRsp("System Error.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        } catch (InputDataValidationException | PriceMismatchException ex) {
            ErrorRsp errorRsp = new ErrorRsp("Cart validation error, please reset cart." + ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }

    }

}
