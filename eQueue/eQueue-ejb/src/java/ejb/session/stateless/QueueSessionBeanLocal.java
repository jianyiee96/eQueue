/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Queue;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.UnableToJoinQueueException;

/**
 *
 * @author User
 */
@Local
public interface QueueSessionBeanLocal {
    
    public List<Queue> retrieveQueueByCustomerId(Long customerId);
    
    public Long joinQueue(Long customerId) throws UnableToJoinQueueException;
    
}
