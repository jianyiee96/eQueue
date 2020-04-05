package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.DiningTableSessionBeanLocal;
import ejb.session.stateless.QueueSessionBeanLocal;
import entity.Customer;
import entity.DiningTable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exceptions.CustomerNotFoundException;
import ws.datamodel.CheckInRsp;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveDiningTableRsp;

@Path("DiningTable")
public class DiningTableResource {

    private final SessionBeanLookup sessionBeanLookup;

    private final DiningTableSessionBeanLocal diningTableSessionBeanLocal;
    private final CustomerSessionBeanLocal customerSessionBeanLocal;

    public DiningTableResource() {
        sessionBeanLookup = new SessionBeanLookup();
        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
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
                return Response.status(Response.Status.OK).entity(new RetrieveDiningTableRsp(diningTable)).build();

            }
            diningTable.setCustomer(null);
            return Response.status(Response.Status.OK).entity(new RetrieveDiningTableRsp(diningTable)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

    @Path("checkIn")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkIn(@QueryParam("customerId") String customerId, @QueryParam("code") String code) {

        try {

            Customer customer = customerSessionBeanLocal.retrieveCustomerById(Long.parseLong(customerId));

            if (customer == null || customer.getCurrentQueue() == null || customer.getAllocatedDiningTable() == null) {
                ErrorRsp errorRsp = new ErrorRsp("Invalid check in request; customer does not exist or customer does not have existing queue/allocated table.");
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            }

            if (customer.getAllocatedDiningTable().getQrCode().equals(code)) {

                diningTableSessionBeanLocal.seatCustomerToDiningTable(customer.getAllocatedDiningTable().getDiningTableId());
                CheckInRsp checkInRsp = new CheckInRsp(true);
                return Response.status(Response.Status.OK).entity(checkInRsp).build();

            } else {

                CheckInRsp checkInRsp = new CheckInRsp(false);
                return Response.status(Response.Status.OK).entity(checkInRsp).build();

            }

        } catch (CustomerNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {

            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }

    }

}
