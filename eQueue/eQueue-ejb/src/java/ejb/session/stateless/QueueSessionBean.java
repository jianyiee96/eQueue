package ejb.session.stateless;

import entity.Customer;
import entity.Queue;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.UnableToJoinQueueException;

@Stateless
public class QueueSessionBean implements QueueSessionBeanLocal {

    @EJB
    CustomerSessionBeanLocal customerSessionBeanLocal;

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    @Override
    public List<Queue> retrieveQueueByCustomerId(Long customerId) {
        Query query = em.createQuery("SELECT q FROM Queue q WHERE q.customer.customerId = :inCustomer");
        query.setParameter("inCustomer", customerId);
        List<Queue> queues = query.getResultList();

        return queues;
    }

    @Override
    public Long joinQueue(Long customerId) throws UnableToJoinQueueException {

        try {

            Customer c = customerSessionBeanLocal.retrieveCustomerById(customerId);
            
            
            

        } catch (CustomerNotFoundException ex) {
            throw new UnableToJoinQueueException("Unknown Customer");
        }

        return 0l;

    }

}
