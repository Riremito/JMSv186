package odin.constants;

import tacos.property.Property_Pachinko;

public class BeansConstants {

    private static BeansConstants instance = null;

    public int get豆豆奖励范围() {
        return Property_Pachinko.豆豆奖励范围;
    }

    public int get力度搞假() {
        return Property_Pachinko.力度搞假;
    }

    public String[] get白怪奖励() {
        return Property_Pachinko.白怪奖励;
    }

    public String[] get色怪奖励() {
        return Property_Pachinko.色怪奖励;
    }

    public String[] get五职业奖励() {
        return Property_Pachinko.五职业奖励;
    }

    public String[] get女皇奖励() {
        return Property_Pachinko.女皇奖励;
    }

    public String[] get大白怪() {
        return Property_Pachinko.大白怪;
    }

    public String[] get小白怪() {
        return Property_Pachinko.小白怪;
    }

    public String[] get紫色怪() {
        return Property_Pachinko.紫色怪;
    }

    public String[] get粉色怪() {
        return Property_Pachinko.粉色怪;
    }

    public String[] get飞侠() {
        return Property_Pachinko.飞侠;
    }

    public String[] get海盗() {
        return Property_Pachinko.海盗;
    }

    public String[] get法师() {
        return Property_Pachinko.法师;
    }

    public String[] get战士() {
        return Property_Pachinko.战士;
    }

    public String[] get弓箭手() {
        return Property_Pachinko.弓箭手;
    }

    public String[] get女皇() {
        return Property_Pachinko.女皇;
    }

    public String[] get豆豆装备() {
        return Property_Pachinko.豆豆装备;
    }

    public String[] get豆豆坐骑() {
        return Property_Pachinko.豆豆坐骑;
    }

    public String[] get消耗品() {
        return Property_Pachinko.消耗品;
    }

    public int get海洋帽子几率() {
        return Property_Pachinko.海洋帽子几率;
    }

    public String[] get黄金狗几率() {
        return Property_Pachinko.黄金狗几率;
    }

    public static BeansConstants getInstance() {
        if (instance == null) {
            instance = new BeansConstants();
        }
        return instance;
    }

}
