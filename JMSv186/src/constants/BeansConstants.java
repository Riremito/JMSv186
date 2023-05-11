package constants;

import config.ServerConfig;

public class BeansConstants {

    private static BeansConstants instance = null;

    public int get豆豆奖励范围() {
        return ServerConfig.豆豆奖励范围;
    }

    public int get力度搞假() {
        return ServerConfig.力度搞假;
    }

    public String[] get白怪奖励() {
        return ServerConfig.白怪奖励;
    }

    public String[] get色怪奖励() {
        return ServerConfig.色怪奖励;
    }

    public String[] get五职业奖励() {
        return ServerConfig.五职业奖励;
    }

    public String[] get女皇奖励() {
        return ServerConfig.女皇奖励;
    }

    public String[] get大白怪() {
        return ServerConfig.大白怪;
    }

    public String[] get小白怪() {
        return ServerConfig.小白怪;
    }

    public String[] get紫色怪() {
        return ServerConfig.紫色怪;
    }

    public String[] get粉色怪() {
        return ServerConfig.粉色怪;
    }

    public String[] get飞侠() {
        return ServerConfig.飞侠;
    }

    public String[] get海盗() {
        return ServerConfig.海盗;
    }

    public String[] get法师() {
        return ServerConfig.法师;
    }

    public String[] get战士() {
        return ServerConfig.战士;
    }

    public String[] get弓箭手() {
        return ServerConfig.弓箭手;
    }

    public String[] get女皇() {
        return ServerConfig.女皇;
    }

    public String[] get豆豆装备() {
        return ServerConfig.豆豆装备;
    }

    public String[] get豆豆坐骑() {
        return ServerConfig.豆豆坐骑;
    }

    public String[] get消耗品() {
        return ServerConfig.消耗品;
    }

    public int get海洋帽子几率() {
        return ServerConfig.海洋帽子几率;
    }

    public String[] get黄金狗几率() {
        return ServerConfig.黄金狗几率;
    }

    public static BeansConstants getInstance() {
        if (instance == null) {
            instance = new BeansConstants();
        }
        return instance;
    }

}
