package ws.datamodel;

import entity.Queue;

public class RetrieveQueueRsp {

    private Queue queue;

    public RetrieveQueueRsp() {
    }

    public RetrieveQueueRsp(Queue queue) {
        this.queue = queue;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

}
