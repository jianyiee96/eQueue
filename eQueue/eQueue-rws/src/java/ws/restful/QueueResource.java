/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import ejb.session.stateless.QueueSessionBeanLocal;
import entity.Customer;
import entity.Queue;
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
import util.exceptions.QueueNotFoundException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveQueueResponse;


@Path("Queue")
public class QueueResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final QueueSessionBeanLocal queueSessionBeanLocal;

    public QueueResource() {

        sessionBeanLookup = new SessionBeanLookup();
        queueSessionBeanLocal = sessionBeanLookup.lookupQueueSessionBeanLocal();

    }

    @Path("retrieveQueueByCustomerId/{customerId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveQueueByCustomerId(@QueryParam("customerId") String customerId) {
        
        try {
            Queue queue = queueSessionBeanLocal.retrieveQueueByCustomerId(Long.parseLong(customerId));

            if(queue == null) {
                return Response.status(Response.Status.OK).entity(new RetrieveQueueResponse(queue)).build();
            }
            
            queue.setCustomer(null);
            return Response.status(Response.Status.OK).entity(new RetrieveQueueResponse(queue)).build();
            
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
