package ws.datamodel;

import entity.DiningTable;

public class RetrieveDiningTableResponse {

    private DiningTable diningTable;

    public RetrieveDiningTableResponse() {
    }

    public RetrieveDiningTableResponse(DiningTable diningTable) {
        this.diningTable = diningTable;
    }

    public DiningTable getDiningTable() {
        return diningTable;
    }

    public void setDiningTable(DiningTable diningTable) {
        this.diningTable = diningTable;
    }

}
