package ws.datamodel;

import entity.DiningTable;

public class RetrieveDiningTableRsp {

    private DiningTable diningTable;

    public RetrieveDiningTableRsp() {
    }

    public RetrieveDiningTableRsp(DiningTable diningTable) {
        this.diningTable = diningTable;
    }

    public DiningTable getDiningTable() {
        return diningTable;
    }

    public void setDiningTable(DiningTable diningTable) {
        this.diningTable = diningTable;
    }

}
