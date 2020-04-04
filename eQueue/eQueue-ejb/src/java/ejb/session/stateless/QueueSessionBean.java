package ejb.session.stateless;

import entity.Customer;
import entity.Queue;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.QueueStatusEnum;
import util.enumeration.TableStatusEnum;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.QueueDoesNotExistException;
import util.exceptions.QueueNotFoundException;
import util.exceptions.UnableToJoinQueueException;

@Stateless
public class QueueSessionBean implements QueueSessionBeanLocal {

    @EJB
    CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    DiningTableSessionBeanLocal diningTableSessionBeanLocal;

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
    public List<Queue> retrieveAllActiveQueues() {

        Query query = em.createQuery("SELECT q FROM Queue q WHERE q.queueStatus = :inStatus ORDER BY q.startDateTime ASC");
        query.setParameter("inStatus", QueueStatusEnum.ACTIVE);

        return query.getResultList();

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

    @Override
    public void allocateQueue(Long queueId) {

        Queue queue = em.find(Queue.class, queueId);
        queue.setAllocatedDateTime(new Date());
        queue.setQueueStatus(QueueStatusEnum.ALLOCATED);

    }

    @Override
    public void invalidateCustomerQueue(Long customerId) throws QueueDoesNotExistException {

        Queue queue = retrieveQueueByCustomerId(customerId);

        if (queue == null) {
            throw new QueueDoesNotExistException();
        } else {

            diningTableSessionBeanLocal.removeCustomerTableRelationship(customerId);

            queue.getCustomer().setCurrentQueue(null);
            //queue.setCustomer(null);

            em.remove(queue);
        }

    }

    @Override
    public Long getPositionByQueueId(Long queueId) {

        Queue queue = em.find(Queue.class, queueId);

        List<Queue> queues = retrieveAllActiveQueues();

        Long position = 1l;

        for (Queue q : queues) {
            if (q.getStartDateTime().before(queue.getStartDateTime())) {
                position++;
            }
        }
        return position;
    }

}
