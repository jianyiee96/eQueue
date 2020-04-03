package ws.datamodel;

import entity.DiningTable;

public class RetrieveTableResponse {

    private DiningTable diningTable;

    public RetrieveTableResponse() {
    }

    public RetrieveTableResponse(DiningTable diningTable) {
        this.diningTable = diningTable;
    }

    public DiningTable getDiningTable() {
        return diningTable;
    }

    public void setDiningTable(DiningTable diningTable) {
        this.diningTable = diningTable;
    }

}
