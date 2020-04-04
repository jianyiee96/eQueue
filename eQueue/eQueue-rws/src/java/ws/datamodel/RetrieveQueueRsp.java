package ws.datamodel;

import entity.Queue;

public class RetrieveQueueRsp {

    private Queue queue;
    private Long position;

    public RetrieveQueueRsp() {
    }

    public RetrieveQueueRsp(Queue queue, Long position) {
        this.queue = queue;
        this.position = position;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

}
