package ws.restful;

import ejb.session.stateless.PaymentTransactionSessionBeanLocal;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exceptions.CustomerOrderNotFoundException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;
import ws.datamodel.CreateNewPaymentTransactionReq;
import ws.datamodel.CreateNewPaymentTransactionRsp;
import ws.datamodel.ErrorRsp;

@Path("PaymentTransaction")
public class PaymentTransactionResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final PaymentTransactionSessionBeanLocal paymentTransactionSessionBeanLocal;

    public PaymentTransactionResource() {
        this.sessionBeanLookup = new SessionBeanLookup();
        this.paymentTransactionSessionBeanLocal = this.sessionBeanLookup.lookupPaymentTransactionSessionBeanLocal();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewPaymentTransaction(CreateNewPaymentTransactionReq createNewPaymentTransactionReq) {

        if (createNewPaymentTransactionReq != null) {
            try {
                Long paymentTransactionId = this.paymentTransactionSessionBeanLocal.createNewPaymentTransactionByCustomer(createNewPaymentTransactionReq.getNewPaymentTransaction());

                CreateNewPaymentTransactionRsp createNewPaymentTransactionRsp = new CreateNewPaymentTransactionRsp(paymentTransactionId);

                return Response.status(Response.Status.OK).entity(createNewPaymentTransactionRsp).build();
            } catch (CustomerOrderNotFoundException | InputDataValidationException | UnknownPersistenceException ex) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorRsp("Failed to create a new payment transaction: " + ex.getMessage())).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorRsp("Invalid new transaction provided (null)")).build();
        }
    }
}
