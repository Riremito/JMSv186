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
import tacos.wz.WzXML.XmlDomData;
import tacos.debug.DebugLogger;
import tacos.property.Property_Java;

/**
 *
 * @author Riremito
 */
public class ServerImg {

    public static final ServerImg BMS8 = new ServerImg(Property_Java.getDir_WzXml_BMS8());

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
                item.item = WzDataTool.getIntPath("item", md_data, 0);
                item.price = WzDataTool.getIntPath("price", md_data, 0);
                item.period = WzDataTool.getIntPath("period", md_data, 0);
                item.stock = WzDataTool.getIntPath("stock", md_data, 0);
                String unitPrice_str = WzDataTool.getStringPath("prob", md_data, "[R8]0.0").replace("[R8]", "");
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
                reward.money = WzDataTool.getIntPath("money", mob_drop, 0);
                reward.item = WzDataTool.getIntPath("item", mob_drop, 0);
                String prob_str = WzDataTool.getStringPath("prob", mob_drop, "[R8]0.0").replace("[R8]", "");
                reward.prob = (int) (Double.parseDouble(prob_str) * PROB_MAX);
                reward.min = WzDataTool.getIntPath("min", mob_drop, 1);
                reward.max = WzDataTool.getIntPath("max", mob_drop, 1);

                if (reward.item != 0) {
                    if (!WzDataStorage.ITEM.check(reward.item)) {
                        DebugLogger.ErrorLog("getRewardData : " + mob_id + ", invalid item = " + reward.item);
                        continue;
                    }
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
