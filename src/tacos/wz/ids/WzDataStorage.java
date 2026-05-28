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
package tacos.wz.ids;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import odin.provider.IMapleData;
import odin.provider.IMapleDataDirectoryEntry;
import odin.provider.IMapleDataEntity;
import odin.provider.IMapleDataProvider;
import tacos.config.Content;
import tacos.debug.DebugLoadTime;
import tacos.debug.DebugLogger;
import tacos.wz.TacosWz;

/**
 *
 * @author Riremito
 */
public class WzDataStorage {

    public static final WzDataStorage SKIN = new WzDataStorage(WzType.SKIN);
    public static final WzDataStorage FACE = new WzDataStorage(WzType.FACE);
    public static final WzDataStorage HAIR = new WzDataStorage(WzType.HAIR);
    public static final WzDataStorage JOB = new WzDataStorage(WzType.JOB);
    public static final WzDataStorage ITEM = new WzDataStorage(WzType.ITEM);
    public static final WzDataStorage MAP = new WzDataStorage(WzType.MAP);
    public static final WzDataStorage MOB = new WzDataStorage(WzType.MOB);
    public static final WzDataStorage NPC = new WzDataStorage(WzType.NPC);
    public static final WzDataStorage REACTOR = new WzDataStorage(WzType.REACTOR);
    public static final WzDataStorage SKILL = new WzDataStorage(WzType.SKILL);
    private static final Random RAND = new Random();

    private WzType type;
    private ArrayList<Integer> data = null;

    public WzDataStorage(WzType type) {
        this.type = type;
    }

    private boolean load() {
        if (this.data != null) {
            DebugLogger.ErrorLog("WzDataStorage load : already loaded, " + this.type);
            return false;
        }

        boolean is_single_data_wz = Content.Wz_SingleFile.get();

        this.data = new ArrayList<>();

        DebugLoadTime dlt = new DebugLoadTime("WzDataStorage (" + this.type + ")");
        switch (this.type) {
            case SKIN: {
                String path = is_single_data_wz ? "Data.wz/Character" : "Character.wz";
                String regex = "0*(\\d+)\\.img";

                IMapleDataProvider wz = (new TacosWz(path)).getWzRoot();
                if (wz == null) {
                    DebugLogger.ErrorLog("WzDataStorage load : path = " + path);
                    return false;
                }

                Pattern pattern = Pattern.compile(regex);
                for (IMapleDataEntity dir : wz.getRootDirectory().getFiles()) {
                    Matcher matcher = pattern.matcher(dir.getName());
                    if (matcher.matches()) {
                        int id = Integer.parseInt(matcher.group(1)) % 100;
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case FACE: {
                String path = is_single_data_wz ? "Data.wz/Character/Face" : "Character.wz/Face";
                String regex = "0*(\\d+)\\.img";

                if (!loadXML(path, regex)) {
                    DebugLogger.ErrorLog("WzDataStorage load : path = " + path);
                    return false;
                }

                dlt.End();
                return true;
            }
            case HAIR: {
                String path = is_single_data_wz ? "Data.wz/Character/Hair" : "Character.wz/Hair";
                String regex = "0*(\\d+)\\.img";

                if (!loadXML(path, regex)) {
                    DebugLogger.ErrorLog("WzDataStorage load : path = " + path);
                    return false;
                }

                dlt.End();
                return true;
            }
            case JOB: {
                String path = is_single_data_wz ? "Data.wz/Skill" : "Skill.wz";
                String regex = "(\\d+)\\.img";

                if (!loadXML(path, regex)) {
                    DebugLogger.ErrorLog("WzDataStorage load : path = " + path);
                    return false;
                }

                dlt.End();
                return true;
            }
            case ITEM: {
                loadItemXML(is_single_data_wz ? "Data.wz/Item/Cash/" : "Item.wz/Cash/");
                loadItemXML(is_single_data_wz ? "Data.wz/Item/Consume/" : "Item.wz/Consume/");
                loadItemXML(is_single_data_wz ? "Data.wz/Item/Etc/" : "Item.wz/Etc/");
                loadItemXML(is_single_data_wz ? "Data.wz/Item/Install/" : "Item.wz/Install/");
                loadEquipXML(is_single_data_wz ? "Data.wz/Character/" : "Character.wz/");
                loadXML(is_single_data_wz ? "Data.wz/Item/Pet/" : "Item.wz/Pet/", "0*(\\d+)\\.img");

                dlt.End();
                return true;
            }
            case MAP: {
                String regex = "0*(\\d+)\\.img";

                loadXML(is_single_data_wz ? "Data.wz/Map/Map/Map0" : "Map.wz/Map/Map0", regex);
                loadXML(is_single_data_wz ? "Data.wz/Map/Map/Map1" : "Map.wz/Map/Map1", regex);
                loadXML(is_single_data_wz ? "Data.wz/Map/Map/Map2" : "Map.wz/Map/Map2", regex);
                loadXML(is_single_data_wz ? "Data.wz/Map/Map/Map3" : "Map.wz/Map/Map3", regex);
                loadXML(is_single_data_wz ? "Data.wz/Map/Map/Map4" : "Map.wz/Map/Map4", regex);
                loadXML(is_single_data_wz ? "Data.wz/Map/Map/Map5" : "Map.wz/Map/Map5", regex);
                loadXML(is_single_data_wz ? "Data.wz/Map/Map/Map6" : "Map.wz/Map/Map6", regex);
                loadXML(is_single_data_wz ? "Data.wz/Map/Map/Map7" : "Map.wz/Map/Map7", regex);
                loadXML(is_single_data_wz ? "Data.wz/Map/Map/Map8" : "Map.wz/Map/Map8", regex);
                loadXML(is_single_data_wz ? "Data.wz/Map/Map/Map9" : "Map.wz/Map/Map9", regex);

                dlt.End();
                return true;
            }
            case MOB: {
                String path = is_single_data_wz ? "Data.wz/Mob" : "Mob.wz";
                String regex = "0*(\\d+)\\.img";

                if (!loadXML(path, regex)) {
                    DebugLogger.ErrorLog("WzDataStorage load : path = " + path);
                    return false;
                }

                dlt.End();
                return true;
            }
            case NPC: {
                String path = is_single_data_wz ? "Data.wz/NPC" : "NPC.wz";
                String regex = "0*(\\d+)\\.img";

                if (!loadXML(path, regex)) {
                    DebugLogger.ErrorLog("WzDataStorage load : path = " + path);
                    return false;
                }

                dlt.End();
                return true;
            }
            case REACTOR: {
                String path = is_single_data_wz ? "Data.wz/Reactor" : "Reactor.wz";
                String regex = "0*(\\d+)\\.img";
                if (!loadXML(path, regex)) {
                    DebugLogger.ErrorLog("WzDataStorage load : path = " + path);
                    return false;
                }

                dlt.End();
                return true;
            }
            case SKILL: {
                String path = "";
                String regex = "";

                if (!loadXML(path, regex)) {
                    DebugLogger.ErrorLog("WzDataStorage load : path = " + path);
                    return false;
                }

                dlt.End();
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    private boolean loadXML(String path, String regex) {
        IMapleDataProvider wz = (new TacosWz(path)).getWzRoot();
        if (wz == null) {
            return false;
        }

        Pattern pattern = Pattern.compile(regex);
        for (IMapleDataEntity dir : wz.getRootDirectory().getFiles()) {
            Matcher matcher = pattern.matcher(dir.getName());
            if (matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1));
                add(id);
            }
        }

        return true;
    }

    private boolean loadItemXML(String path) {
        IMapleDataProvider wz = (new TacosWz(path)).getWzRoot();
        if (wz == null) {
            return false;
        }

        Pattern img_pattern = Pattern.compile("0*(\\d+)\\.img");
        Pattern id_pattern = Pattern.compile("0*(\\d+)");
        for (IMapleDataEntity dir : wz.getRootDirectory().getFiles()) {
            Matcher img_matcher = img_pattern.matcher(dir.getName());
            if (img_matcher.matches()) {
                for (IMapleData md : wz.getData(dir.getName()).getChildren()) {
                    Matcher id_matcher = id_pattern.matcher(md.getName());
                    if (id_matcher.matches()) {
                        int id = Integer.parseInt(md.getName());
                        add(id);
                    } else {
                        DebugLogger.DebugLog("invalid item data = " + dir.getName() + " -> " + md.getName());
                    }
                }
            }
        }

        return true;
    }

    public boolean loadEquipXML(String path) {
        IMapleDataProvider wz = (new TacosWz(path)).getWzRoot();
        if (wz == null) {
            return false;
        }

        Pattern img_pattern = Pattern.compile("0*(\\d+)\\.img");
        for (IMapleDataDirectoryEntry equip_dir : wz.getRootDirectory().getSubDirectories()) {
            for (IMapleDataEntity dir : equip_dir.getFiles()) {
                Matcher img_matcher = img_pattern.matcher(dir.getName());
                if (img_matcher.matches()) {
                    int id = Integer.parseInt(img_matcher.group(1));
                    // ignore hair
                    if (1000000 <= id) {
                        add(id);
                    }
                }
            }
        }

        return true;
    }

    private boolean add(int id) {
        if (this.data.contains(id)) {
            return false;
        }

        this.data.add(id);
        return true;
    }

    public ArrayList<Integer> getIds() {
        if (this.data == null) {
            load();
        }
        return this.data;
    }

    public boolean check(int id) {
        if (this.data == null) {
            load();
        }

        // KMS001, beginner job does not have any skill.
        if (this.type == WzType.JOB) {
            if (id == 0) {
                return true;
            }
        }

        return this.data.contains(id);
    }

    public int getRandom() {
        if (this.data == null) {
            load();
        }

        return this.data.get(RAND.nextInt(this.data.size()));
    }
}
