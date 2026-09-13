package odin.server;

import java.util.List;
import java.util.ArrayList;
import odin.client.inventory.IItem;

public class MerchItemPackage {

    private int mesos = 0, packageid;
    private List<IItem> items = new ArrayList<>();

    public void setItems(List<IItem> items) {
        this.items = items;
    }

    public List<IItem> getItems() {
        return items;
    }

    public int getMesos() {
        return mesos;
    }

}
