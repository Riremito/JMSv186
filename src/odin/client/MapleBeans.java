// CMS v72のソースを流用
package odin.client;
public class MapleBeans {

    private final int number;
    private final int type;
    private final int pos;

    public MapleBeans(int pos, int type, int number) {
        this.pos = pos;
        this.number = number;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    public int getPos() {
        return pos;
    }
}