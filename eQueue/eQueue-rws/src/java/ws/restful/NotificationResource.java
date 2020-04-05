package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.NotificationSessionBeanLocal;
import ejb.session.stateless.QueueSessionBeanLocal;
import entity.Notification;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import ws.datamodel.DeleteNotificationRsp;
import ws.datamodel.ErrorRsp;
import ws.datamodel.ReadNotificationRsp;
import ws.datamodel.RetrieveCustomerNotificationsRsp;

@Path("Notification")
public class NotificationResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final NotificationSessionBeanLocal notificationSessionBeanLocal;
    private final CustomerSessionBeanLocal customerSessionBeanLocal;

    public NotificationResource() {
        this.sessionBeanLookup = new SessionBeanLookup();
        notificationSessionBeanLocal = sessionBeanLookup.lookupNotificationSessionBeanLocal();
        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
    }

    @Path("retrieveCustomerNotifications")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerNotifications(@QueryParam("customerId") String customerId) {
        try {

            List<Notification> notifications = notificationSessionBeanLocal.retrieveNotificationsByCustomerId(Long.parseLong(customerId));
            notifications.forEach(n -> n.setCustomer(null));
            return Response.status(Response.Status.OK).entity(new RetrieveCustomerNotificationsRsp(notifications)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
    
    @Path("readNotification")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response readNotification(@QueryParam("notificationId") String notificationId) {
        try {

            Boolean change = notificationSessionBeanLocal.readNotification(Long.parseLong(notificationId));
            
            return Response.status(Response.Status.OK).entity(new ReadNotificationRsp(change)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
    
    @Path("deleteNotification")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteNotification(@QueryParam("notificationId") String notificationId) {
        try {

            Boolean change = notificationSessionBeanLocal.deleteNotification(Long.parseLong(notificationId));
            
            return Response.status(Response.Status.OK).entity(new DeleteNotificationRsp(change)).build();

        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
    

}
