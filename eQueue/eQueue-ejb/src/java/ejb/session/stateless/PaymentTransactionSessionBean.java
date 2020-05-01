package ejb.session.stateless;

import entity.CustomerOrder;
import entity.PaymentTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.OrderStatusEnum;
import util.exceptions.CreateNewPaymentTransactionException;
import util.exceptions.CustomerOrderNotFoundException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Stateless
public class PaymentTransactionSessionBean implements PaymentTransactionSessionBeanLocal {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    @EJB(name = "CustomerOrderSessionBeanLocal")
    private CustomerOrderSessionBeanLocal customerOrderSessionBeanLocal;

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    public PaymentTransactionSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewPaymentTransactionByCustomer(PaymentTransaction newPaymentTransaction) throws CreateNewPaymentTransactionException, CustomerOrderNotFoundException, InputDataValidationException, UnknownPersistenceException {
//        System.out.println("newPaymentTransaction_date: " + newPaymentTransaction.getTransactionDate());
//        System.out.println("newPaymentTransaction_val : " + newPaymentTransaction.getTransactionValue());
//        System.out.println("newPaymentTransaction_gst : " + newPaymentTransaction.getGst());
//        System.out.println("newPaymentTransaction_type: " + newPaymentTransaction.getPaymentType());
//        System.out.println("newPaymentTransaction_ords: ");
//        for (CustomerOrder co : newPaymentTransaction.getCustomerOrders()) {
//            System.out.println("                      item: " + co.getOrderId());
//        }

        try {
            if (newPaymentTransaction.getCustomerOrders().isEmpty()) {
                throw new CreateNewPaymentTransactionException("There is no customer order associated with this table currently!");
            }

            Set<ConstraintViolation<PaymentTransaction>> constraintViolations = validator.validate(newPaymentTransaction);

            if (constraintViolations.isEmpty()) {

                List<CustomerOrder> customerOrders = new ArrayList<>();

                for (CustomerOrder co : newPaymentTransaction.getCustomerOrders()) {
                    CustomerOrder customerOrder = customerOrderSessionBeanLocal.retrieveCustomerOrderById(co.getOrderId());

                    customerOrder.setPaymentTransaction(newPaymentTransaction);
                    customerOrder.setStatus(OrderStatusEnum.PAID);

                    customerOrders.add(customerOrder);
                }

                newPaymentTransaction.setCustomerOrders(customerOrders);

                em.persist(newPaymentTransaction);
                em.flush();

//                System.out.println("Payment done!!!");
                return newPaymentTransaction.getPaymentTransactionId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (PersistenceException ex) {
            throw new UnknownPersistenceException(ex.getMessage());
        }
    }

    @Override
    public List<PaymentTransaction> retrieveAllPastTransactions() {
        Query query = em.createQuery("SELECT pt FROM PaymentTransaction pt");
        List<PaymentTransaction> pastTransactions = query.getResultList();

        for (PaymentTransaction pt : pastTransactions) {
            List<CustomerOrder> customerOrders = pt.getCustomerOrders();
            customerOrders.size();
            
            for (CustomerOrder co: customerOrders) {
                co.getOrderLineItems().size();
            }
        }
        
        return pastTransactions;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<PaymentTransaction>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
