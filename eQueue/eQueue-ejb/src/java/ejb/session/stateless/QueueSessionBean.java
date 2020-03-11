package ejb.session.stateless;

import entity.Customer;
import entity.Queue;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.QueueNotFoundException;
import util.exceptions.UnableToJoinQueueException;

@Stateless
public class QueueSessionBean implements QueueSessionBeanLocal {

    @EJB
    CustomerSessionBeanLocal customerSessionBeanLocal;

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    @Override
    public Queue retrieveQueueByCustomerId(Long customerId) {
        Query query = em.createQuery("SELECT q FROM Queue q WHERE q.customer.customerId = :inCustomerId");
        query.setParameter("inCustomerId", customerId);

        try {
            return (Queue) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
        
    }

    @Override
    public Long joinQueue(Long customerId, Long numOfPax) throws UnableToJoinQueueException {

        try {

            Customer c = customerSessionBeanLocal.retrieveCustomerById(customerId);
            Queue q = new Queue(numOfPax);
            q.setCustomer(c);
            em.persist(q);
            em.flush();
            return q.getQueueId();
        } catch (CustomerNotFoundException ex) {
            throw new UnableToJoinQueueException("Unknown Customer: " + ex);
        }

    }

}
