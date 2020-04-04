package ws.datamodel;

import entity.Queue;

public class RetrieveQueueResponse {

    private Queue queue;

    public RetrieveQueueResponse() {
    }

    public RetrieveQueueResponse(Queue queue) {
        this.queue = queue;
    }

    public Queue getDiningTable() {
        return queue;
    }

    public void setDiningTable(Queue queue) {
        this.queue = queue;
    }

}
