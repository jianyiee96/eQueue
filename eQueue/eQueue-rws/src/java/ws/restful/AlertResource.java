package ws.restful;

import ejb.session.stateless.AlertSessionBeanLocal;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;
import ws.datamodel.CreateAlertReq;
import ws.datamodel.ErrorRsp;

@Path("Alert")
public class AlertResource {

    private final SessionBeanLookup sessionBeanLookup;

    private final AlertSessionBeanLocal alertSessionBeanLocal;

    public AlertResource() {
        sessionBeanLookup = new SessionBeanLookup();
        alertSessionBeanLocal = sessionBeanLookup.lookupAlertSessionBeanLocal();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAlert(CreateAlertReq createAlertReq) {

        if (createAlertReq != null) {

            try {

                alertSessionBeanLocal.createNewAlert(createAlertReq.getAlert());

                return Response.status(Response.Status.OK).build();

            } catch (InputDataValidationException | UnknownPersistenceException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }

        } else {

            ErrorRsp errorRsp = new ErrorRsp("Invalid register customer request");

            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }

    }

}
