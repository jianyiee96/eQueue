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
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveDiningTableResponse;

@Path("DiningTable")
public class DiningTableResource {

    private final SessionBeanLookup sessionBeanLookup;

    private final DiningTableSessionBeanLocal diningTableSessionBeanLocal;

    public DiningTableResource() {
        sessionBeanLookup = new SessionBeanLookup();
        diningTableSessionBeanLocal = sessionBeanLookup.lookupDiningTableSessionBeanLocal();
    }

    @Path("retrieveDiningTableByCustomerId")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDiningTableByCustomerId(@QueryParam("customerId") String customerId) {

        try {
            DiningTable diningTable = diningTableSessionBeanLocal.retrieveDiningTableByCustomerId(Long.parseLong(customerId));

            if (diningTable == null) {
                return Response.status(Response.Status.OK).entity(new RetrieveDiningTableResponse(diningTable)).build();
            }

            diningTable.setCustomer(null);
            return Response.status(Response.Status.OK).entity(new RetrieveDiningTableResponse(diningTable)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

}
