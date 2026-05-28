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
    private static final String NO_STRING_DATA = "<NO_STRING_DATA>";

    private enum NameDataType {
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
            case ITEM: {
                for (int item_id : DWI_LoadXML.getItem().getIds()) {
                    add(item_id);
                }
                for (IMapleData wz_root : StringWz.get().getEqp().getChildren()) {
                    for (IMapleData wz_data : wz_root.getChildren()) {
                        int item_id = Integer.parseInt(wz_data.getName());
                        String item_name = TacosWzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                        updateName(item_id, item_name);
                    }
                }
                for (IMapleData wz_data : StringWz.get().getConsume().getChildren()) {
                    int item_id = Integer.parseInt(wz_data.getName());
                    String item_name = TacosWzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(item_id, item_name);
                }
                for (IMapleData wz_data : StringWz.get().getIns().getChildren()) {
                    int item_id = Integer.parseInt(wz_data.getName());
                    String item_name = TacosWzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(item_id, item_name);
                }
                for (IMapleData wz_data : StringWz.get().getEtc().getChildren()) {
                    int item_id = Integer.parseInt(wz_data.getName());
                    String item_name = TacosWzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(item_id, item_name);
                }
                for (IMapleData wz_data : StringWz.get().getPet().getChildren()) {
                    int item_id = Integer.parseInt(wz_data.getName());
                    String item_name = TacosWzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(item_id, item_name);
                }
                for (IMapleData wz_data : StringWz.get().getCash().getChildren()) {
                    int item_id = Integer.parseInt(wz_data.getName());
                    String item_name = TacosWzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(item_id, item_name);
                }
                return true;
            }
            case MAP: {
                for (int map_id : DWI_LoadXML.getMap().getIds()) {
                    addMap(map_id);
                }
                for (IMapleData wz_root : StringWz.get().getMap().getChildren()) {
                    for (IMapleData wz_data : wz_root.getChildren()) {
                        int map_id = Integer.parseInt(wz_data.getName());
                        String mapName = TacosWzDataTool.getString(wz_data.getChildByPath("mapName"), NO_NAME);
                        String streetName = TacosWzDataTool.getString(wz_data.getChildByPath("streetName"), NO_NAME);
                        updateMapName(map_id, mapName, streetName);
                    }
                }
                return true;
            }
            case MOB: {
                for (int mob_id : DWI_LoadXML.getMob().getIds()) {
                    add(mob_id);
                }
                for (IMapleData wz_data : StringWz.get().getMob().getChildren()) {
                    int mob_id = Integer.parseInt(wz_data.getName());
                    String mob_name = TacosWzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(mob_id, mob_name);
                }
                return true;
            }
            case NPC: {
                for (int npc_id : DWI_LoadXML.getNpc().getIds()) {
                    add(npc_id);
                }
                for (IMapleData wz_data : StringWz.get().getNpc().getChildren()) {
                    int npc_id = Integer.parseInt(wz_data.getName());
                    String npc_name = TacosWzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(npc_id, npc_name);
                }
                return true;
            }
            case REACTOR: {
                for (int npc_id : DWI_LoadXML.getReactor().getIds()) {
                    add(npc_id);
                }
                // no reactor name data.
                return true;
            }
            case SKILL: {
                // TODO : skill ids.
                for (IMapleData wz_data : StringWz.get().getSkill().getChildren()) {
                    if (wz_data.getChildByPath("bookName") != null) {
                        continue;
                    }
                    int skill_id = Integer.parseInt(wz_data.getName());
                    String skill_name = TacosWzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(skill_id, skill_name);
                }
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    private boolean add(int id) {
        NameData nd = new NameData();
        nd.setId(id);
        nd.setName(NO_STRING_DATA);
        nd.setAvailable(true);
        this.data.put(id, nd);
        return true;
    }

    private boolean updateName(int id, String name) {
        NameData nd = this.data.get(id);
        if (nd == null) {
            nd = new NameData();
            nd.setId(id);
            nd.setAvailable(false);
        }

        nd.setName(name);
        this.data.put(id, nd);
        return true;
    }

    private boolean addMap(int id) {
        NameData nd = new NameData();
        nd.setId(id);
        nd.setMapName(NO_STRING_DATA);
        nd.setStreetName(NO_STRING_DATA);
        nd.setAvailable(true);
        this.data.put(id, nd);
        return true;
    }

    private boolean updateMapName(int id, String map_name, String street_name) {
        NameData nd = this.data.get(id);
        if (nd == null) {
            nd = new NameData();
            nd.setId(id);
            nd.setAvailable(false);
        }

        nd.setMapName(map_name);
        nd.setStreetName(street_name);
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
        return find(name, true);
    }

    public ArrayList<NameData> find(String name, boolean ignore_string_only) {
        if (this.type == NameDataType.MAP) {
            return findMap(name, ignore_string_only);
        }

        if (this.data == null) {
            load();
        }

        String lower_name = name.toLowerCase();
        ArrayList<NameData> result = new ArrayList<>();
        for (NameData nd : this.data.values()) {
            if (ignore_string_only) {
                if (!nd.isAvailable()) {
                    continue;
                }
            }
            if (nd.getName().toLowerCase().contains(lower_name)) {
                result.add(nd);
            }
        }

        return result;
    }

    public ArrayList<NameData> findMap(String name, boolean ignore_string_only) {
        if (this.data == null) {
            load();
        }

        String lower_name = name.toLowerCase();
        ArrayList<NameData> result = new ArrayList<>();
        for (NameData nd : this.data.values()) {
            if (ignore_string_only) {
                if (!nd.isAvailable()) {
                    continue;
                }
            }
            if (nd.getMapName().toLowerCase().contains(lower_name) || nd.getStreetName().toLowerCase().contains(lower_name)) {
                result.add(nd);
            }
        }

        return result;
    }
}
