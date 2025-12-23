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
package tacos.wz.data;

import tacos.wz.TacosWz;
import tacos.config.Content;
import tacos.debug.DebugLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import odin.provider.MapleDataTool;
import odin.provider.WzXML.MapleDataType;
import odin.server.StructSetItem;
import odin.server.StructSetItem.SetItem;
import odin.provider.IMapleData;

/**
 *
 * @author Riremito
 */
public class EtcWz extends TacosWz {

    private static EtcWz wz = null;

    public static EtcWz get() {
        if (wz == null) {
            wz = new EtcWz(Content.Wz_SingleFile.get() ? "Data.wz/Etc" : "Etc.wz");
        }

        return wz;
    }

    public EtcWz(String path) {
        super(path);
    }

    public IMapleData getForbiddenName() {
        return getData("ForbiddenName.img");
    }

    public IMapleData getNpcLocation() {
        return getData("NpcLocation.img");
    }

    public IMapleData getItemMake() {
        return getData("ItemMake.img");
    }

    public IMapleData getCommodity() {
        return getData("Commodity.img");
    }

    public IMapleData getCashPackage() {
        return getData("CashPackage.img");
    }

    public IMapleData getSetItemInfo() {
        return getData("SetItemInfo.img");
    }

    private List<String> list_fn = null;

    private List<String> getFN() {
        if (list_fn != null) {
            return list_fn;
        }

        list_fn = new ArrayList<>();
        for (final IMapleData data : getForbiddenName().getChildren()) {
            list_fn.add(MapleDataTool.getString(data));
        }

        return list_fn;
    }

    public boolean isForbiddenName(String character_name) {
        for (final String forbidden_name : getFN()) {
            if (character_name.contains(forbidden_name)) {
                return true;
            }
        }
        return false;
    }

    private Map<Integer, StructSetItem> map_SetItemInfo = null;

    public Map<Integer, StructSetItem> getSetItemInfoList() {
        if (map_SetItemInfo != null) {
            return map_SetItemInfo;
        }

        map_SetItemInfo = new HashMap<>();

        if (getSetItemInfo() == null) {
            return map_SetItemInfo;
        }

        for (IMapleData dat : getSetItemInfo()) {
            StructSetItem itemz = new StructSetItem();
            itemz.setItemID = Integer.parseInt(dat.getName());
            itemz.completeCount = MapleDataTool.getIntConvert("completeCount", dat, 0);
            for (IMapleData level : dat.getChildByPath("ItemID")) {
                if (level.getType() != MapleDataType.INT) {
                    DebugLogger.ErrorLog("SetItemInfo.img, " + dat.getName() + " error");
                    continue;
                }
                itemz.itemIDs.add(MapleDataTool.getIntConvert(level));
            }
            for (IMapleData level : dat.getChildByPath("Effect")) {
                SetItem itez = new SetItem();
                itez.incPDD = MapleDataTool.getIntConvert("incPDD", level, 0);
                itez.incMDD = MapleDataTool.getIntConvert("incMDD", level, 0);
                itez.incSTR = MapleDataTool.getIntConvert("incSTR", level, 0);
                itez.incDEX = MapleDataTool.getIntConvert("incDEX", level, 0);
                itez.incINT = MapleDataTool.getIntConvert("incINT", level, 0);
                itez.incLUK = MapleDataTool.getIntConvert("incLUK", level, 0);
                itez.incACC = MapleDataTool.getIntConvert("incACC", level, 0);
                itez.incPAD = MapleDataTool.getIntConvert("incPAD", level, 0);
                itez.incMAD = MapleDataTool.getIntConvert("incMAD", level, 0);
                itez.incSpeed = MapleDataTool.getIntConvert("incSpeed", level, 0);
                itez.incMHP = MapleDataTool.getIntConvert("incMHP", level, 0);
                itez.incMMP = MapleDataTool.getIntConvert("incMMP", level, 0);
                itemz.items.put(Integer.parseInt(level.getName()), itez);
            }
            map_SetItemInfo.put(itemz.setItemID, itemz);
        }

        return map_SetItemInfo;
    }

}
