package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.Customer;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exceptions.CustomerInvalidLoginCredentialException;
import ws.datamodel.CustomerLoginRsp;
import ws.datamodel.ErrorRsp;

@Path("Customer")
public class CustomerResource {

    private final SessionBeanLookup sessionBeanLookup;

    private final CustomerSessionBeanLocal customerSessionBeanLocal;

    public CustomerResource() {
        sessionBeanLookup = new SessionBeanLookup();
        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
    }

    @Path("customerLogin")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response customerLogin(@QueryParam("email") String email,
            @QueryParam("password") String password) {
        
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(email, password);
            System.out.println("=-=-=-=-=-=-=-= Customer " + customer.getEmail() + " login remotely via web service");

            customer.setPassword(null);
            customer.setSalt(null);
            customer.getCustomerOrders().clear();
            customer.getNotifications().clear();
            customer.setAllocatedDiningTable(null);
            customer.setCurrentQueue(null);

            return Response.status(Response.Status.OK).entity(new CustomerLoginRsp(customer)).build();
        } catch (CustomerInvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

}