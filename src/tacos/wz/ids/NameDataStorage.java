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
package tacos.wz.ids;

import java.util.ArrayList;
import java.util.TreeMap;
import odin.provider.IMapleData;
import tacos.debug.DebugLogger;
import tacos.wz.TacosWzDataTool;
import tacos.wz.data.StringWz;

/**
 *
 * @author Riremito
 */
public class NameDataStorage {

    public static final NameDataStorage ITEM = new NameDataStorage(NameDataType.ITEM);
    public static final NameDataStorage MAP = new NameDataStorage(NameDataType.MAP);
    public static final NameDataStorage MOB = new NameDataStorage(NameDataType.MOB);
    public static final NameDataStorage NPC = new NameDataStorage(NameDataType.NPC);
    public static final NameDataStorage REACTOR = new NameDataStorage(NameDataType.REACTOR);
    public static final NameDataStorage SKILL = new NameDataStorage(NameDataType.SKILL);
    private static final String NO_NAME = "<NO_NAME>";

    public enum NameDataType {
        ITEM,
        MAP,
        MOB,
        NPC,
        REACTOR,
        SKILL,
        UNKNOWN;
    }

    private NameDataType type;
    private TreeMap<Integer, NameData> data = null;

    private NameDataStorage(NameDataType type) {
        this.type = type;
    }

    private boolean load() {
        if (this.data != null) {
            DebugLogger.ErrorLog("NameData load : already loaded, " + this.type);
            return false;
        }

        this.data = new TreeMap<>();
        switch (this.type) {
            case MOB: {
                for (IMapleData wz_data : StringWz.get().getMob().getChildren()) {
                    int mob_id = Integer.parseInt(wz_data.getName());
                    String mob_name = TacosWzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    add(mob_id, mob_name);
                }
                return true;
            }
            case SKILL: {
                for (IMapleData wz_data : StringWz.get().getSkill().getChildren()) {
                    if (wz_data.getChildByPath("bookName") != null) {
                        continue;
                    }
                    int skill_id = Integer.parseInt(wz_data.getName());
                    String skill_name = TacosWzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    add(skill_id, skill_name);
                }
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    private boolean add(int id, String name) {
        if (this.data.containsKey(id)) {
            DebugLogger.ErrorLog("NameData add : duplicated, " + this.type + ", id =" + id);
            return false;
        }

        NameData nd = new NameData();
        nd.id = id;
        nd.name = name;
        this.data.put(id, nd);
        return true;
    }

    public NameData get(int id) {
        if (this.data == null) {
            load();
        }

        return this.data.get(id);
    }

    public ArrayList<NameData> find(String name) {
        String lower_name = name.toLowerCase();

        if (this.data == null) {
            load();
        }

        ArrayList<NameData> result = new ArrayList<>();
        for (NameData nd : this.data.values()) {
            if (nd.name.toLowerCase().contains(lower_name)) {
                result.add(nd);
            }
        }

        return result;
    }
}
