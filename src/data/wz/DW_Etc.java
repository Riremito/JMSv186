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
package data.wz;

import debug.Debug;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import provider.WzXML.MapleDataType;
import server.StructSetItem;
import server.StructSetItem.SetItem;

/**
 *
 * @author Riremito
 */
public class DW_Etc {

    private static DataWz wz = null;

    private static DataWz getWz() {
        if (wz == null) {
            wz = new DataWz("Etc.wz");
        }
        return wz;
    }

    private static MapleDataProvider getWzRoot() {
        return getWz().getWzRoot();
    }

    private static MapleData img_ForbiddenName = null;
    private static MapleData img_NpcLocation = null;
    private static MapleData img_ItemMake = null;
    private static MapleData img_Commodity = null;
    private static MapleData img_CashPackage = null;

    public static MapleData getForbiddenName() {
        if (img_ForbiddenName == null) {
            img_ForbiddenName = getWz().loadData("ForbiddenName.img");
        }
        return img_ForbiddenName;
    }

    public static MapleData getNpcLocation() {
        if (img_NpcLocation == null) {
            img_NpcLocation = getWz().loadData("NpcLocation.img");
        }
        return img_NpcLocation;
    }

    public static MapleData getItemMake() {
        if (img_ItemMake == null) {
            img_ItemMake = getWz().loadData("ItemMake.img");
        }
        return img_ItemMake;
    }

    public static MapleData getCommodity() {
        if (img_Commodity == null) {
            img_Commodity = getWz().loadData("Commodity.img");
        }
        return img_Commodity;
    }

    public static MapleData getCashPackage() {
        if (img_CashPackage == null) {
            img_CashPackage = getWz().loadData("CashPackage.img");
        }
        return img_CashPackage;
    }

    private static List<String> list_fn = null;

    private static List<String> getFN() {
        if (list_fn != null) {
            return list_fn;
        }

        list_fn = new ArrayList<String>();
        for (final MapleData data : getForbiddenName().getChildren()) {
            list_fn.add(MapleDataTool.getString(data));
        }

        return list_fn;
    }

    public static boolean isForbiddenName(String character_name) {
        for (final String forbidden_name : getFN()) {
            if (character_name.contains(forbidden_name)) {
                return true;
            }
        }
        return false;
    }

    private static MapleData img_SetItemInfo = null;
    private static Map<Integer, StructSetItem> map_SetItemInfo = null;

    public static MapleData getSetItemInfo() {
        if (img_SetItemInfo == null) {
            img_SetItemInfo = getWz().loadData("SetItemInfo.img");
        }
        return img_SetItemInfo;
    }

    public static Map<Integer, StructSetItem> getSetItemInfoList() {
        if (map_SetItemInfo != null) {
            return map_SetItemInfo;
        }

        map_SetItemInfo = new HashMap<>();

        if (getSetItemInfo() == null) {
            return map_SetItemInfo;
        }

        for (MapleData dat : getSetItemInfo()) {
            StructSetItem itemz = new StructSetItem();
            itemz.setItemID = Integer.parseInt(dat.getName());
            itemz.completeCount = MapleDataTool.getIntConvert("completeCount", dat, 0);
            for (MapleData level : dat.getChildByPath("ItemID")) {
                if (level.getType() != MapleDataType.INT) {
                    Debug.ErrorLog("SetItemInfo.img, " + dat.getName() + " error");
                    continue;
                }
                itemz.itemIDs.add(MapleDataTool.getIntConvert(level));
            }
            for (MapleData level : dat.getChildByPath("Effect")) {
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
