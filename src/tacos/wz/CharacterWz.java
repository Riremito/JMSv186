/*
 * Copyright (C) 2025 Riremito
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

import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import tacos.config.Content;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class CharacterWz extends WzXML {

    public CharacterWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Character" : "Character.wz");
    }

    public MapleData getItemData(int id) {
        int item_type = id / 1000000;
        if (2 <= item_type) {
            return null;
        }

        String target_img_name = String.format("%08d.img", id);
        for (MapleDataDirectoryEntry mdde : rootDirectory.getSubDirectories()) {
            for (MapleDataEntity mdfe : mdde.getFiles()) {
                if (mdfe.getName().equals(target_img_name)) {
                    MapleData md_equip = getData(mdde.getName() + "/" + mdfe.getName());
                    if (md_equip == null) {
                        DebugLogger.XmlLog("getItemData : invalid equip data, " + id);
                        return null;
                    }
                    return md_equip;
                }
            }
        }

        DebugLogger.XmlLog("getItemData : not found, " + id);
        return null;
    }

    @Getter
    public class EquipData {

        private int tuc;
    }

    private final HashMap<Integer, EquipData> equip_infos = new HashMap<>();

    public EquipData getEquipData(int item_id) {
        EquipData ret = this.equip_infos.get(item_id);
        if (ret == null) {
            MapleData equip_img = getItemData(item_id);
            if (equip_img != null) {
                MapleData info = equip_img.getChildByPath("info");
                if (info != null) {
                    ret = new EquipData();
                    ret.tuc = info.getInt("tuc");
                }
                this.equip_infos.put(item_id, ret);
            }
        }
        return ret;
    }

    @Getter
    public class EquipInfoLevel {

        private int exp; // no idea.
        private int incDEXMax;
        private int incDEXMin;
        private int incLUKMax;
        private int incLUKMin;
        private int incPADMax;
        private int incPADMin;
    }

    private final HashMap<Integer, ArrayList<EquipInfoLevel>> equip_info_levels = new HashMap<>();

    public ArrayList<EquipInfoLevel> getEquipLevelInfo(int item_id) {
        ArrayList<EquipInfoLevel> growth = this.equip_info_levels.get(item_id);
        if (growth != null) {
            return growth;
        }
        growth = new ArrayList<>();
        MapleData equip_img = getItemData(item_id);
        if (equip_img != null) {
            MapleData info_level = equip_img.getChildByPath("info/level/info");
            if (info_level != null) {
                for (MapleData level_node : info_level.getChildren()) {
                    EquipInfoLevel eil = new EquipInfoLevel();
                    eil.exp = level_node.getInt("exp");
                    eil.incDEXMax = level_node.getInt("incDEXMax");
                    eil.incDEXMin = level_node.getInt("incDEXMin");
                    eil.incLUKMax = level_node.getInt("incLUKMax");
                    eil.incLUKMin = level_node.getInt("incLUKMin");
                    eil.incPADMax = level_node.getInt("incPADMax");
                    eil.incPADMin = level_node.getInt("incPADMin");
                    growth.add(eil);
                }
            }
        }
        this.equip_info_levels.put(item_id, growth);
        return growth;
    }
}
