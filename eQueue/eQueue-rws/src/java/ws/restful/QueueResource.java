/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.QueueSessionBeanLocal;
import entity.Customer;
import entity.Queue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exceptions.UnableToJoinQueueException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.JoinQueueRsp;
import ws.datamodel.RetrieveQueueRsp;

@Path("Queue")
public class QueueResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final QueueSessionBeanLocal queueSessionBeanLocal;
    private final CustomerSessionBeanLocal customerSessionBeanLocal;

    public QueueResource() {

        sessionBeanLookup = new SessionBeanLookup();
        queueSessionBeanLocal = sessionBeanLookup.lookupQueueSessionBeanLocal();
        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();

    }

    @Path("retrieveQueueByCustomerId")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveQueueByCustomerId(@QueryParam("customerId") String customerId) {
        
        try {
            Queue queue = queueSessionBeanLocal.retrieveQueueByCustomerId(Long.parseLong(customerId));

            if (queue == null) {
                return Response.status(Response.Status.OK).entity(new RetrieveQueueRsp(queue)).build();
            }
            queue.setCustomer(null);
            return Response.status(Response.Status.OK).entity(new RetrieveQueueRsp(queue)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("joinQueue")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response joinQueue(@QueryParam("customerId") String customerId, @QueryParam("pax") String pax) {

        try {

            Customer customer = customerSessionBeanLocal.retrieveCustomerById(Long.parseLong(customerId));

            if (customer == null || customer.getCurrentQueue() != null || customer.getAllocatedDiningTable() != null) {
                ErrorRsp errorRsp = new ErrorRsp("Invalid join queue request; customer does not exist or customer has existing queue/allocated table.");
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            }
            
            Long queueId = queueSessionBeanLocal.joinQueue(customer.getCustomerId(), Long.parseLong(pax));
            JoinQueueRsp joinQueueRsp = new JoinQueueRsp(queueId);

            return Response.status(Response.Status.OK).entity(joinQueueRsp).build();

        } catch (UnableToJoinQueueException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {

            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }
}
