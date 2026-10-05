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

    public static final ServerImg SI = new ServerImg();

    public ServerImg() {
        setWzRoot();
    }

    boolean loaded = false;

    public boolean isLoaded() {
        return this.loaded;
    }

    // 船
    public MapleData getContinent() {
        return getData("Continent.img");
    }

    // イベント
    public MapleData getFieldSet() {
        return getData("FieldSet.img");
    }

    // ガチャポン
    public MapleData getGachapon() {
        return getData("Gachapon.img");
    }

    // NPC商店
    public MapleData getNpcShop() {
        return getData("NpcShop.img");
    }

    // Reactor
    public MapleData getReactorAction() {
        return getData("ReactorAction.img");
    }

    // ドロップ
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
        ArrayList<RewardData> list_reward = this.rewards.get(mob_id);
        if (list_reward != null) {
            // already loaded.
            return list_reward;
        }

        list_reward = new ArrayList<>();
        MapleData mob_drop_table = getReward().getChildByPath(String.format("m%07d", mob_id));
        // found.
        if (mob_drop_table != null) {
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
                        DebugLogger.ErrorLog("getReward : " + mob_id + ", invalid item = " + reward.item);
                        continue;
                    }
                }

                list_reward.add(reward);
            }
        }

        this.rewards.put(mob_id, list_reward);
        DebugLogger.XmlLog("getReward : " + mob_id + ", count = " + list_reward.size());
        return list_reward;
    }

    private File root_dir;

    private boolean setWzRoot() {
        String path = Property_Java.getDir_WzXml_BMS8() + "/";
        File file = new File(path);

        if (!file.exists()) {
            return false;
        }

        if (!file.isDirectory()) {
            return false;
        }

        DebugLogger.XmlLog("setWzRoot(S) : " + path);
        this.root_dir = file;
        this.loaded = true;
        return true;
    }

    private TreeMap<String, MapleData> xml_cache = new TreeMap<>();

    private MapleData getData(String data_path) {
        MapleData data = this.xml_cache.get(data_path);
        if (data != null) {
            return data;
        }

        File dataFile = new File(this.root_dir, data_path + ".xml");
        if (!dataFile.exists()) {
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
