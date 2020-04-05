package ws.datamodel;

public class JoinQueueRsp {

    private Long queueId;

    public JoinQueueRsp() {
    }

    public JoinQueueRsp(Long queueId) {
        this.queueId = queueId;
    }

    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

}
