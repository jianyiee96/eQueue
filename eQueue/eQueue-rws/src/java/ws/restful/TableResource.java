package ws.restful;

import ejb.session.stateless.DiningTableSessionBeanLocal;
import entity.DiningTable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import ws.datamodel.RetrieveTableResponse;

@Path("Table")
public class TableResource {

    private final SessionBeanLookup sessionBeanLookup;

    private final DiningTableSessionBeanLocal diningTableSessionBeanLocal;

    public TableResource() {
        sessionBeanLookup = new SessionBeanLookup();
        diningTableSessionBeanLocal = sessionBeanLookup.lookupDiningTableSessionBeanLocal();
    }

    @Path("retrieveTableByCustomerId/{customerId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveTableByCustomerId(@QueryParam("customerId") Long customerId) {

        DiningTable diningTable = diningTableSessionBeanLocal.retrieveDiningTableByCustomerId(customerId);

        return Response.status(Status.OK).entity(new RetrieveTableResponse(diningTable)).build();
    }

}
