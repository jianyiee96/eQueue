package ws.datamodel;

import entity.Store;

public class RetrieveStoreRsp {

    private Store store;

    public RetrieveStoreRsp() {

    }

    public RetrieveStoreRsp(Store store) {
        this.store = store;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

}
