package ws.restful;

import ejb.session.stateless.CreditCardSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.CreditCard;
import entity.Customer;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exceptions.CreateNewCreditCardException;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.DeleteCreditCardException;
import ws.datamodel.CreateCreditCardReq;
import ws.datamodel.CreateCreditCardRsp;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveCreditCardRsp;

@Path("CreditCard")
public class CreditCardResource {

    private final SessionBeanLookup sessionBeanLookup;

    private final CreditCardSessionBeanLocal creditCardSessionBeanLocal;

    private final CustomerSessionBeanLocal customerSessionBeanLocal;

    public CreditCardResource() {
        sessionBeanLookup = new SessionBeanLookup();
        creditCardSessionBeanLocal = sessionBeanLookup.lookupCreditCardSessionBeanLocal();
        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCreditCard(CreateCreditCardReq createCreditCardReq) {

        if (createCreditCardReq != null) {

            try {

                Long creditCardId = creditCardSessionBeanLocal.createNewCreditCard(createCreditCardReq.getEmail(), createCreditCardReq.getCreditCard());
                CreateCreditCardRsp createCreditCardRsp = new CreateCreditCardRsp(creditCardId);

                return Response.status(Response.Status.OK).entity(createCreditCardRsp).build();

            } catch (CreateNewCreditCardException ex) {

                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();

            } catch (Exception ex) {

                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }

        } else {

            ErrorRsp errorRsp = new ErrorRsp("Invalid create credit card request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();

        }
    }

    @Path("retrieveCreditCard")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCreditCard(@QueryParam("email") String email) {
        try {

            Customer customer = customerSessionBeanLocal.retrieveCustomerByEmail(email);
            customer.getCustomerOrders().clear();
            customer.getNotifications().clear();
            customer.setAllocatedDiningTable(null);
            customer.setCurrentQueue(null);
            customer.getShoppingCart().getOrderLineItems().forEach(oli -> oli.getMenuItem().setMenuCategory(null));

            CreditCard customerCreditCard = customer.getCreditCard();
            customer.setCreditCard(null);

            if (customerCreditCard != null) {
                return Response.status(Response.Status.OK).entity(new RetrieveCreditCardRsp(customerCreditCard)).build();
            } else {
                ErrorRsp errorRsp = new ErrorRsp("No Credit Card Associated with Customer");
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            }

        } catch (CustomerNotFoundException ex) {

            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();

        }
    }

    @Path("{creditCardId}")
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCreditCard(@PathParam("creditCardId") Long creditCardId) {
        try {
            creditCardSessionBeanLocal.deleteCreditCard(creditCardId);

            return Response.status(Response.Status.OK).build();
        } catch (DeleteCreditCardException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
