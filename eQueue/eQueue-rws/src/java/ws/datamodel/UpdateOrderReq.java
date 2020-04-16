/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

/**
 *
 * @author User
 */
public class UpdateOrderReq {
    
    private Long orderLineItemId;
    private Long newQuantity;
    private String newComment;
    private Long customerOrderId;

    public UpdateOrderReq() {
    }

    public UpdateOrderReq(Long orderLineItemId, Long newQuantity, String newComment, Long customerOrderId) {
        this.orderLineItemId = orderLineItemId;
        this.newQuantity = newQuantity;
        this.newComment = newComment;
        this.customerOrderId = customerOrderId;
    }

    public Long getOrderLineItemId() {
        return orderLineItemId;
    }

    public void setOrderLineItemId(Long orderLineItemId) {
        this.orderLineItemId = orderLineItemId;
    }

    public Long getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(Long newQuantity) {
        this.newQuantity = newQuantity;
    }

    public String getNewComment() {
        return newComment;
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

    public Long getCustomerOrderId() {
        return customerOrderId;
    }

    public void setCustomerOrderId(Long customerOrderId) {
        this.customerOrderId = customerOrderId;
    }
    
    
    
}
