package odin.server;

public class CashItemInfo {

    private int itemId;
    private int count;
    private int price;
    private int sn;
    private int expire;
    private int gender;

    public CashItemInfo(int itemId, int count, int price, int sn, int expire, int gender, boolean sale) {
        this.itemId = itemId;
        this.count = count;
        this.price = price;
        this.sn = sn;
        this.expire = expire;
        this.gender = gender;
    }

    public int getId() {
        return itemId;
    }

    public int getCount() {
        return count;
    }

    public int getPrice() {
        return price;
    }

    public int getSN() {
        return sn;
    }

    public int getPeriod() {
        return expire;
    }
}
