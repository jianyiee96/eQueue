package ws.restful;

import ejb.session.stateless.PaymentTransactionSessionBeanLocal;
import entity.PaymentTransaction;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exceptions.CustomerOrderNotFoundException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;
import ws.datamodel.CreateNewPaymentTransactionReq;
import ws.datamodel.CreateNewPaymentTransactionRsp;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrievePaymentTransactionsRsp;

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

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrievePaymentTransactions(@QueryParam("customerId") String customerId) {

        try {

            if (customerId != null) {
                List<PaymentTransaction> paymentTransactions = this.paymentTransactionSessionBeanLocal.retrievePaymentTransactions(Long.parseLong(customerId));
                
                for (PaymentTransaction pt : paymentTransactions) {
                    pt.setCustomerOrders(null);
                    pt.setEmployee(null);
                }
                
                RetrievePaymentTransactionsRsp retrievePaymentTransactionsRsp = new RetrievePaymentTransactionsRsp(paymentTransactions);

                return Response.status(Response.Status.OK).entity(retrievePaymentTransactionsRsp).build();
                
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorRsp("Invalid customerId provided (null)")).build();
            }

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

}
