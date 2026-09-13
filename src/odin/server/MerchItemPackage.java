package odin.server;

import java.util.List;
import java.util.ArrayList;
import odin.client.inventory.Item;

public class MerchItemPackage {

    private int mesos = 0;
    private int packageid;
    private List<Item> items = new ArrayList<>();

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public int getMesos() {
        return mesos;
    }

}
