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

import tacos.config.Content;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import odin.server.StructSetItem;
import odin.server.StructSetItem.SetItem;
import odin.provider.IMapleData;

/**
 *
 * @author Riremito
 */
public class EtcWz extends WzXML {

    public EtcWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Etc" : "Etc.wz");
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
            list_fn.add(WzDataTool.getString(data));
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
            itemz.completeCount = WzDataTool.getIntPath("completeCount", dat, 0);
            for (IMapleData level : dat.getChildByPath("ItemID")) {
                itemz.itemIDs.add(WzDataTool.getInt(level, 0));
            }
            for (IMapleData level : dat.getChildByPath("Effect")) {
                SetItem itez = new SetItem();
                itez.incPDD = WzDataTool.getIntPath("incPDD", level, 0);
                itez.incMDD = WzDataTool.getIntPath("incMDD", level, 0);
                itez.incSTR = WzDataTool.getIntPath("incSTR", level, 0);
                itez.incDEX = WzDataTool.getIntPath("incDEX", level, 0);
                itez.incINT = WzDataTool.getIntPath("incINT", level, 0);
                itez.incLUK = WzDataTool.getIntPath("incLUK", level, 0);
                itez.incACC = WzDataTool.getIntPath("incACC", level, 0);
                itez.incPAD = WzDataTool.getIntPath("incPAD", level, 0);
                itez.incMAD = WzDataTool.getIntPath("incMAD", level, 0);
                itez.incSpeed = WzDataTool.getIntPath("incSpeed", level, 0);
                itez.incMHP = WzDataTool.getIntPath("incMHP", level, 0);
                itez.incMMP = WzDataTool.getIntPath("incMMP", level, 0);
                itemz.items.put(Integer.valueOf(level.getName()), itez);
            }
            map_SetItemInfo.put(itemz.setItemID, itemz);
        }

        return map_SetItemInfo;
    }
}
