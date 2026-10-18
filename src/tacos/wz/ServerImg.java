/*
 * Copyright (C) 2026 Riremito
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */
package tacos.wz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import lombok.Getter;
import tacos.config.Config;
import tacos.config.Region;
import tacos.wz.WzXML.XmlDomData;
import tacos.debug.DebugLogger;
import tacos.property.Property_Java;

/**
 *
 * @author Riremito
 */
public class ServerImg {

    public static final ServerImg BMS8 = new ServerImg(Property_Java.getDir_WzXml_BMS8());
    public static final ServerImg JMS147 = new ServerImg(Property_Java.getDir_WzXml_JMS147());
    public static final ServerImg JMS186 = new ServerImg(Property_Java.getDir_WzXml_JMS186());

    public static ArrayList<NpcShopData> getAutoNpcShopData(int npc_id) {
        if (Region.BMS.check()) {
            return BMS8.getNpcShopData(npc_id);
        }
        return JMS186.getNpcShopData(npc_id);
    }

    public static ArrayList<RewardData> getAutoRewardData(int mob_id) {
        if (Region.BMS.check()) {
            return BMS8.getRewardData(mob_id);
        }
        if (Config.GreaterOrEqual(Region.JMS, 186)) {
            return JMS186.getRewardData(mob_id);
        }
        ArrayList<RewardData> ret = JMS147.getRewardData(mob_id);
        if (ret == null || ret.isEmpty()) {
            ret = JMS186.getRewardData(mob_id);
        }
        return ret;
    }

    private ServerImg(String path) {
        setWzRoot(path);
    }

    // ship data.
    private MapleData getContinent() {
        return getData("Continent.img");
    }

    // event data.
    private MapleData getFieldSet() {
        return getData("FieldSet.img");
    }

    // gchapon data.
    private MapleData getGachapon() {
        return getData("Gachapon.img");
    }

    // npc shop data.
    public MapleData getNpcShop() {
        return getData("NpcShop.img");
    }

    @Getter
    public static class NpcShopData {

        private int item;
        private int price;
        private int period;
        private int stock;
        private double unitPrice;
    }

    private final TreeMap<Integer, ArrayList<NpcShopData>> npc_shops = new TreeMap<>();

    public ArrayList<NpcShopData> getNpcShopData(int npc_id) {
        MapleData md_npc_shop_img = getNpcShop();
        if (md_npc_shop_img == null) {
            return null;
        }

        if (this.npc_shops.containsKey(npc_id)) {
            return this.npc_shops.get(npc_id);
        }

        ArrayList<NpcShopData> items = null;
        MapleData md_npc_shop = md_npc_shop_img.getChildByPath(String.format("%07d", npc_id));
        if (md_npc_shop != null) {
            items = new ArrayList<>();
            for (MapleData md_data : md_npc_shop.getChildren()) {
                NpcShopData item = new NpcShopData();
                item.item = md_data.getInt("item");
                item.price = md_data.getInt("price");
                item.period = md_data.getInt("period");
                item.stock = md_data.getInt("stock");
                String unitPrice_str = md_data.getString("prob", "[R8]0.0").replace("[R8]", "");
                item.unitPrice = Double.parseDouble(unitPrice_str);
                if (item.price == 0) {
                    // TODO : star and bullet recharge.
                    continue;
                }
                if (!WzDataStorage.ITEM.check(item.item)) {
                    DebugLogger.XmlLog("getNpcShopData : " + npc_id + ", invalid item id = " + item.item);
                    continue;
                }
                items.add(item);
            }
        }

        this.npc_shops.put(npc_id, items);
        return items;
    }

    // reactor data.
    private MapleData getReactorAction() {
        return getData("ReactorAction.img");
    }

    // drop item data.
    private MapleData getReward() {
        return getData("Reward.img");
    }

    @Getter
    public static class RewardData {

        private int money;
        private int item;
        private int prob;
        private int min;
        private int max;
    }

    // unofficial prob.
    private boolean setProb(RewardData rd) {
        if (rd.prob != 0) {
            return false;
        }
        if (rd.money != 0) {
            rd.prob = 650000; // 65%
            return true;
        }
        int item_type = rd.item / 1000000;
        int item_sub_type = rd.item / 10000;
        switch (item_type) {
            case 1 -> {
                rd.prob = 1000;
            }
            case 2 -> {
                switch (item_sub_type) {
                    case 204, 207, 233, 238, 228, 229 -> {
                        rd.prob = 1000;
                    }
                    default -> {
                        rd.prob = 10000;
                    }
                }
            }
            case 4 -> {
                rd.prob = (item_sub_type == 400) ? 500000 : 5000;
            }
            default -> {
                rd.prob = 100;
            }
        }
        return true;
    }

    private final TreeMap<Integer, ArrayList<RewardData>> rewards = new TreeMap<>();
    public static final int PROB_MAX = 1000000;

    public ArrayList<RewardData> getRewardData(int mob_id) {
        MapleData md_reward_img = getReward();
        if (md_reward_img == null) {
            return null;
        }

        if (this.rewards.containsKey(mob_id)) {
            return this.rewards.get(mob_id);
        }

        ArrayList<RewardData> list_reward = null;
        MapleData mob_drop_table = md_reward_img.getChildByPath(String.format("m%07d", mob_id));
        // found.
        if (mob_drop_table != null) {
            list_reward = new ArrayList<>();
            for (MapleData mob_drop : mob_drop_table.getChildren()) {
                RewardData reward = new RewardData();
                reward.money = mob_drop.getInt("money");
                reward.item = mob_drop.getInt("item");
                String prob_str = mob_drop.getString("prob", "[R8]0.0").replace("[R8]", "");
                reward.prob = (int) (Double.parseDouble(prob_str) * PROB_MAX);
                reward.min = mob_drop.getInt("min", 1);
                reward.max = mob_drop.getInt("max", 1);

                if (reward.item != 0) {
                    if (!WzDataStorage.ITEM.check(reward.item)) {
                        DebugLogger.ErrorLog("getRewardData : " + mob_id + ", invalid item = " + reward.item);
                        continue;
                    }
                }

                if (reward.prob == 0) {
                    setProb(reward);
                }

                list_reward.add(reward);
            }
            DebugLogger.XmlLog("getRewardData : " + mob_id + ", count = " + list_reward.size());
        }

        this.rewards.put(mob_id, list_reward);
        return list_reward;
    }

    private File root_dir;

    private boolean setWzRoot(String path) {
        File file = new File(path);

        if (!file.exists()) {
            return false;
        }

        if (!file.isDirectory()) {
            return false;
        }

        DebugLogger.XmlLog("setWzRoot(S) : " + path);
        this.root_dir = file;
        return true;
    }

    private final TreeMap<String, MapleData> xml_cache = new TreeMap<>();

    private MapleData getData(String data_path) {
        if (this.xml_cache.containsKey(data_path)) {
            return this.xml_cache.get(data_path);
        }

        File dataFile = new File(this.root_dir, data_path + ".xml");
        if (!dataFile.exists()) {
            this.xml_cache.put(data_path, null);
            return null;
        }

        File imageDataDir = new File(this.root_dir, data_path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(dataFile);
        } catch (FileNotFoundException ex) {
        }

        if (fis == null) {
            this.xml_cache.put(data_path, null);
            return null;
        }

        XmlDomData domMapleData = new XmlDomData(fis, imageDataDir.getParentFile());
        try {
            fis.close();
        } catch (IOException ex) {
        }

        this.xml_cache.put(data_path, domMapleData);
        DebugLogger.XmlLog("getData(S) : " + data_path);
        return domMapleData;
    }
}
