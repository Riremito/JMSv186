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

import java.util.ArrayList;
import java.util.TreeMap;
import odin.provider.IMapleData;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class WzNameStorage {

    public static final WzNameStorage ITEM = new WzNameStorage(WzType.ITEM);
    public static final WzNameStorage MAP = new WzNameStorage(WzType.MAP);
    public static final WzNameStorage MOB = new WzNameStorage(WzType.MOB);
    public static final WzNameStorage NPC = new WzNameStorage(WzType.NPC);
    public static final WzNameStorage REACTOR = new WzNameStorage(WzType.REACTOR);
    public static final WzNameStorage SKILL = new WzNameStorage(WzType.SKILL);
    private static final String NO_NAME = "<NO_NAME>";
    private static final String NO_STRING_DATA = "<NO_STRING_DATA>";

    private WzType type;
    private TreeMap<Integer, WzName> data = null;

    private WzNameStorage(WzType type) {
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
                for (int item_id : WzDataStorage.ITEM.getIds()) {
                    add(item_id);
                }
                for (IMapleData wz_root : WzXML.STRING.getEqp().getChildren()) {
                    for (IMapleData wz_data : wz_root.getChildren()) {
                        int item_id = Integer.parseInt(wz_data.getName());
                        String item_name = WzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                        updateName(item_id, item_name);
                    }
                }
                for (IMapleData wz_data : WzXML.STRING.getConsume().getChildren()) {
                    int item_id = Integer.parseInt(wz_data.getName());
                    String item_name = WzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(item_id, item_name);
                }
                for (IMapleData wz_data : WzXML.STRING.getIns().getChildren()) {
                    int item_id = Integer.parseInt(wz_data.getName());
                    String item_name = WzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(item_id, item_name);
                }
                for (IMapleData wz_data : WzXML.STRING.getEtc().getChildren()) {
                    int item_id = Integer.parseInt(wz_data.getName());
                    String item_name = WzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(item_id, item_name);
                }
                for (IMapleData wz_data : WzXML.STRING.getPet().getChildren()) {
                    int item_id = Integer.parseInt(wz_data.getName());
                    String item_name = WzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(item_id, item_name);
                }
                for (IMapleData wz_data : WzXML.STRING.getCash().getChildren()) {
                    int item_id = Integer.parseInt(wz_data.getName());
                    String item_name = WzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(item_id, item_name);
                }
                return true;
            }
            case MAP: {
                for (int map_id : WzDataStorage.MAP.getIds()) {
                    addMap(map_id);
                }
                for (IMapleData wz_root : WzXML.STRING.getMap().getChildren()) {
                    for (IMapleData wz_data : wz_root.getChildren()) {
                        int map_id = Integer.parseInt(wz_data.getName());
                        String mapName = WzDataTool.getString(wz_data.getChildByPath("mapName"), NO_NAME);
                        String streetName = WzDataTool.getString(wz_data.getChildByPath("streetName"), NO_NAME);
                        updateMapName(map_id, mapName, streetName);
                    }
                }
                return true;
            }
            case MOB: {
                for (int mob_id : WzDataStorage.MOB.getIds()) {
                    add(mob_id);
                }
                for (IMapleData wz_data : WzXML.STRING.getMob().getChildren()) {
                    int mob_id = Integer.parseInt(wz_data.getName());
                    String mob_name = WzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(mob_id, mob_name);
                }
                return true;
            }
            case NPC: {
                for (int npc_id : WzDataStorage.NPC.getIds()) {
                    add(npc_id);
                }
                for (IMapleData wz_data : WzXML.STRING.getNpc().getChildren()) {
                    int npc_id = Integer.parseInt(wz_data.getName());
                    String npc_name = WzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
                    updateName(npc_id, npc_name);
                }
                return true;
            }
            case REACTOR: {
                for (int npc_id : WzDataStorage.REACTOR.getIds()) {
                    add(npc_id);
                }
                // no reactor name data.
                return true;
            }
            case SKILL: {
                for (int skill_id : WzDataStorage.SKILL.getIds()) {
                    add(skill_id);
                }
                for (IMapleData wz_data : WzXML.STRING.getSkill().getChildren()) {
                    if (wz_data.getChildByPath("bookName") != null) {
                        continue;
                    }
                    int skill_id = Integer.parseInt(wz_data.getName());
                    String skill_name = WzDataTool.getString(wz_data.getChildByPath("name"), NO_NAME);
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
        WzName nd = new WzName();
        nd.setId(id);
        nd.setName(NO_STRING_DATA);
        nd.setAvailable(true);
        this.data.put(id, nd);
        return true;
    }

    private boolean updateName(int id, String name) {
        WzName nd = this.data.get(id);
        if (nd == null) {
            nd = new WzName();
            nd.setId(id);
            nd.setAvailable(false);
        }

        nd.setName(name);
        this.data.put(id, nd);
        return true;
    }

    private boolean addMap(int id) {
        WzName nd = new WzName();
        nd.setId(id);
        nd.setMapName(NO_STRING_DATA);
        nd.setStreetName(NO_STRING_DATA);
        nd.setAvailable(true);
        this.data.put(id, nd);
        return true;
    }

    private boolean updateMapName(int id, String map_name, String street_name) {
        WzName nd = this.data.get(id);
        if (nd == null) {
            nd = new WzName();
            nd.setId(id);
            nd.setAvailable(false);
        }

        nd.setMapName(map_name);
        nd.setStreetName(street_name);
        this.data.put(id, nd);
        return true;
    }

    public WzName get(int id) {
        if (this.data == null) {
            load();
        }

        return this.data.get(id);
    }

    public ArrayList<WzName> find(String name) {
        return find(name, true);
    }

    public ArrayList<WzName> find(String name, boolean ignore_string_only) {
        if (this.type == WzType.MAP) {
            return findMap(name, ignore_string_only);
        }

        if (this.data == null) {
            load();
        }

        String lower_name = name.toLowerCase();
        ArrayList<WzName> result = new ArrayList<>();
        for (WzName nd : this.data.values()) {
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

    public ArrayList<WzName> findMap(String name, boolean ignore_string_only) {
        if (this.data == null) {
            load();
        }

        String lower_name = name.toLowerCase();
        ArrayList<WzName> result = new ArrayList<>();
        for (WzName nd : this.data.values()) {
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
